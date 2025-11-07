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

    @OptIn(ExperimentalCoroutinesApi::class)
    open val profilesFlow: StateFlow<DataFetchResult<List<FeedProfile>>> =
        combine(
            flow = sessionManager.selectedControllerProfile,
            flow2 = refreshTrigger.onStart { emit(value = Unit) },
            flow3 = pendingDeletions,
        ) { controller, _, deletions ->
            Pair(first = controller, second = deletions)
        }
        .filter{ it.first != null }
        .flatMapLatest { (controller: ControllerProfile?, deletions: Set<Int>) ->
            flow {
                // UI shall temporarily show loading state, while fetching data
                emit(value = DataFetchResult.Loading)

                val networkResult: DataFetchResult<List<FeedProfileImport>> =
                    withContext(context = Dispatchers.IO) {
                        val profileUrl =
                            "http://${controller?.address}${WebApiUrls.FEED_GET_PROFILES}"
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

    open suspend fun triggerRefresh() {
        refreshTrigger.emit(value = Unit)
    }

    open suspend fun deleteFeedProfile(feedProfile: FeedProfile) {
        // Optimistic Update: Immediately add the ID to the pending deletions list.
        // The UI will react to this instantly.
        pendingDeletions.value += feedProfile.profileID

        // Perform the actual network request in the background.
        repositoryScope.launch(context = Dispatchers.IO) {
            webApiPostRequestExecution(
                apiUrl = WebApiUrls.FEED_POST_REMOVE_PROFILE,
                sessionManager = sessionManager,
                webApiPostRequest = WebApiPostRequest(),
                optionalGetParams = feedProfile.getFeedProfileApiCallParameters()
            )
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
                // @todo: emit error state
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
            webApiPostRequestExecution(
                sessionManager = sessionManager,
                apiUrl = WebApiUrls.FEED_POST_PROFILE_UPDATE,
                webApiPostRequest = WebApiPostRequest(),
                optionalGetParams = apiCallParameters
            )
        }
        // Refresh ist not required since this call emanates from FeedProfileEditViewModel.
    }
}