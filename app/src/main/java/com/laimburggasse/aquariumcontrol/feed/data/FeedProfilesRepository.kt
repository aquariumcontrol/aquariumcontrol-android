package com.laimburggasse.aquariumcontrol.feed.data

import com.laimburggasse.aquariumcontrol.SessionManager
import com.laimburggasse.aquariumcontrol.controller.domain.ControllerProfile
import com.laimburggasse.aquariumcontrol.di.ApplicationScope
import com.laimburggasse.aquariumcontrol.feed.data.remote.FeedProfileReader
import com.laimburggasse.aquariumcontrol.feed.data.remote.dto.FeedProfileImport
import com.laimburggasse.aquariumcontrol.feed.domain.model.FeedProfile
import com.laimburggasse.aquariumcontrol.webapi.DataFetchResult
import com.laimburggasse.aquariumcontrol.webapi.WebApiParams
import com.laimburggasse.aquariumcontrol.webapi.WebApiPostRequest
import com.laimburggasse.aquariumcontrol.webapi.WebApiUrls
import com.laimburggasse.aquariumcontrol.webapi.webApiPostRequestExecution
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
open class FeedProfilesRepository @Inject constructor(
    private val sessionManager: SessionManager,
    @ApplicationScope private val repositoryScope: CoroutineScope
) {
    private val refreshTrigger = MutableSharedFlow<Unit>(replay = 1)

    // Holds a list of profile IDs that are pending deletions (POST request triggered, local update required).
    private val pendingDeletions = MutableStateFlow<Set<Int>>(value = emptySet())

    // A flow to hold the failure value
    private val failureResult = MutableStateFlow<DataFetchResult.Error?>(value = null)

    @OptIn(ExperimentalCoroutinesApi::class)
    open val profilesFlow: StateFlow<DataFetchResult<List<FeedProfile>>> =
        combine(
            flow = sessionManager.selectedControllerProfile,
            flow2 = refreshTrigger.onStart { emit(value = Unit) },
            flow3 = pendingDeletions,
            flow4 = failureResult
        ) { controller, _, deletions, failure ->
            Triple(first = controller, second = deletions, third = failure)
        }
        .filter{ it.first != null }
        .flatMapLatest { (controller: ControllerProfile?, deletions: Set<Int>, failure: DataFetchResult.Error?) ->
            flow {
                if (failure != null) {
                    emit(value = failure)
                    return@flow
                }
                if (controller == null) {
                    emit(value = DataFetchResult.Error(
                        exception = null,
                        errorMessage = "No controller selected."
                    ))
                    return@flow
                }

                // If there is no failure and we have a controller, proceed with loading.
                emit(value = DataFetchResult.Loading)

                val networkResult: DataFetchResult<List<FeedProfileImport>> =
                    withContext(context = Dispatchers.IO) {
                        val profileUrl = "http://${controller.address}:${controller.port}${WebApiUrls.FEED_GET_PROFILES}"
                        val profileReader = FeedProfileReader(profileUrl)
                        profileReader.readFeedProfiles()
                    }

                // Merge optimistic updates with network result
                val finalResult: DataFetchResult<List<FeedProfile>> = when (networkResult) {
                    is DataFetchResult.Success -> {
                        var profiles: List<FeedProfile> = networkResult.data.map {
                            FeedProfile.Companion.fromFeedProfileImport(feedProfileImport = it)
                        }

                        // Apply pending deletions to the fetched list
                        if (deletions.isNotEmpty()) {
                            profiles = profiles.filterNot { deletions.contains(it.profileID) }
                        }

                        DataFetchResult.Success(data = profiles)
                    }
                    is DataFetchResult.Error -> networkResult // Pass through errors
                    is DataFetchResult.Loading -> DataFetchResult.Error(
                        exception = null,
                        errorMessage = "Could not load feed profiles."
                    )
                }
                emit(value = finalResult)
            }
        }.stateIn(
            scope = repositoryScope,
            started = SharingStarted.Companion.WhileSubscribed(stopTimeoutMillis = 5000L),
            initialValue = DataFetchResult.Loading
        )

    open suspend fun deleteFeedProfile(feedProfile: FeedProfile) {
        // Optimistic Update: Immediately add the ID to the pending deletions list.
        // The UI will react to this instantly.
        pendingDeletions.value += feedProfile.profileID

        // Perform the actual network request in the background.
        repositoryScope.launch(context = Dispatchers.IO) {
            val success: Boolean = webApiPostRequestExecution(
                apiUrl = WebApiUrls.FEED_POST_REMOVE_PROFILE,
                sessionManager = sessionManager,
                webApiPostRequest = WebApiPostRequest(),
                optionalGetParams = feedProfile.getFeedProfileApiCallParameters()
            )
            if (!success) {
                failureResult.value = DataFetchResult.Error(
                    exception = null,
                    errorMessage = "Could not delete the feed profile."
                )
            }
        }
    }

    open suspend fun addFeedProfile(feedProfileName: String) {
        repositoryScope.launch(context = Dispatchers.IO) {
            val success: Boolean = webApiPostRequestExecution(
                sessionManager = sessionManager,
                apiUrl = WebApiUrls.FEED_POST_PROFILE_CREATE,
                webApiPostRequest = WebApiPostRequest(),
                optionalGetParams = mapOf(WebApiParams.PROFILE_NAME to feedProfileName)
            )
            if (success) {
                delay(timeMillis = 3000L)
                triggerRefresh()
            } else {
                failureResult.value = DataFetchResult.Error(
                    exception = null,
                    errorMessage = "Could not delete the feed profile."
                )
            }
        }
    }

    open fun findProfileById(profileId: Int): FeedProfile? {
        val currentData: DataFetchResult<List<FeedProfile>> = profilesFlow.value
        return if (currentData is DataFetchResult.Success) {
            currentData.data.find { it.profileID == profileId }
        } else {
            null
        }
    }

    open fun findProfileByName(profileName: String): FeedProfile? {
        val currentData: DataFetchResult<List<FeedProfile>> = profilesFlow.value
        return if (currentData is DataFetchResult.Success) {
            currentData.data.find { it.profileName == profileName }
        } else {
            null
        }
    }

    fun updateFeedProfile(modifiedFeedProfile: FeedProfile) {
        val apiCallParameters: Map<String, String> = modifiedFeedProfile.getFeedProfileApiCallParameters()
        repositoryScope.launch(context = Dispatchers.IO) {
            val success: Boolean = webApiPostRequestExecution(
                sessionManager = sessionManager,
                apiUrl = WebApiUrls.FEED_POST_PROFILE_UPDATE,
                webApiPostRequest = WebApiPostRequest(),
                optionalGetParams = apiCallParameters
            )
            if (!success) {
                failureResult.value = DataFetchResult.Error(
                    exception = null,
                    errorMessage = "Could not update the feed profile."
                )
            }
        }
        // Refresh is not required since this call emanates from FeedProfileEditViewModel.
    }

    /**
     * Public function to allow the ViewModel to trigger a manual refresh (e.g., for pull-to-refresh).
     */
    suspend fun triggerRefresh() {
        // Check if the current state is a failure.
        if (failureResult.value != null) {
            // If it is, setting it to null is sufficient to trigger the combine/flatMapLatest chain again.
            failureResult.value = null
        } else {
            // If the system is NOT in a failure state, then we must emit the refreshTrigger
            // to force a new fetch, as nothing else has changed.
            refreshTrigger.emit(value = Unit)
        }
    }
}