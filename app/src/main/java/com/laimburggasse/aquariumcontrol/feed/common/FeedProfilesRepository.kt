package com.laimburggasse.aquariumcontrol.feed.common

import android.util.Log
import com.laimburggasse.aquariumcontrol.SessionManager
import com.laimburggasse.aquariumcontrol.controller.common.ControllerProfile
import com.laimburggasse.aquariumcontrol.webapi.DataFetchResult
import com.laimburggasse.aquariumcontrol.webapi.WebApiParams
import com.laimburggasse.aquariumcontrol.webapi.WebApiPostRequest
import com.laimburggasse.aquariumcontrol.webapi.WebApiUrls
import com.laimburggasse.aquariumcontrol.webapi.webApiPostRequestExecution
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
open class FeedProfilesRepository @Inject constructor(private val sessionManager: SessionManager) {

    private val repositoryScope = CoroutineScope(context = SupervisorJob() + Dispatchers.Default)
    private val refreshTrigger = MutableSharedFlow<Unit>(replay = 1)

    // Holds a list of profile IDs that are pending deletions (POST request triggered, local update required).
    private val pendingDeletions = MutableStateFlow<Set<Int>>(value = emptySet())

    // Holds a new profile name that is pending addition (POST request triggered, local update required).
    private val pendingAddition = MutableStateFlow<String?>(value = null)
    // -----------------------------

    @OptIn(ExperimentalCoroutinesApi::class)
    open val profilesFlow: StateFlow<DataFetchResult<List<FeedProfile>>> =
        combine(
            flow = sessionManager.selectedControllerProfile,
            flow2 = refreshTrigger.onStart { emit(value = Unit) },
            flow3 = pendingDeletions,
            flow4 = pendingAddition
        ) { controller, _, deletions, addition ->
            Triple(first = controller, second = deletions, third = addition)
        }.flatMapLatest { (controller: ControllerProfile?, deletions: Set<Int>, addition: String?) ->
            flow {
                if (controller == null) {
                    emit(value = DataFetchResult.Error(exception = null, errorMessage = "No controller selected"))
                    return@flow
                }

                emit(value = DataFetchResult.Error(exception = null, errorMessage = "Loading"))
                Log.d("FeedProfilesRepository", "Fetching feed profiles from server.")
                val networkResult: DataFetchResult<List<FeedProfileImport>> =
                    withContext(context = Dispatchers.IO) {
                    val profileUrl = "http://${controller.address}${WebApiUrls.FEED_GET_PROFILES}"
                    val profileReader = FeedProfileReader(profileUrl)
                    profileReader.readFeedProfiles()
                }

                // Merge optimistic updates with network result
                val finalResult: DataFetchResult<List<FeedProfile>> = when (networkResult) {
                    is DataFetchResult.Success -> {
                        var profiles: List<FeedProfile> = networkResult.data.map {
                            FeedProfile.fromFeedProfileImport(feedProfileImport = it)
                        }

                        // Apply pending deletions to the fetched list
                        if (deletions.isNotEmpty()) {
                            profiles = profiles.filterNot { deletions.contains(it.profileID)}
                        }

                        // Apply pending addition to the fetched list
                        if (addition != null) {
                            // Create a temporary profile for UI display. ID can be a placeholder.
                            val tempProfile = FeedProfile(
                                profileID = -1,
                                profileName = "$addition (Saving...)",
                                feedProfilePhases = emptyList()
                            )
                            profiles = profiles + tempProfile
                        }
                        DataFetchResult.Success(profiles)
                    }
                    is DataFetchResult.Error -> networkResult // Pass through errors
                }
                emit(finalResult)
                // --------------------------------------------------
            }
        }.stateIn(
            scope = repositoryScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000L),
            initialValue = DataFetchResult.Error(exception = null, errorMessage = "Loading")
        )

    open suspend fun triggerRefresh() {
        refreshTrigger.emit(Unit)
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

    open suspend fun addFeedProfile(feedProfileName: String): Boolean {
        // Optimistic Update: Immediately set the pending addition name.
        // The UI will react instantly and show the temporary item.
        pendingAddition.value = feedProfileName

        return withContext(context = Dispatchers.IO) {
            val success: Boolean = webApiPostRequestExecution(
                sessionManager = sessionManager,
                apiUrl = WebApiUrls.FEED_POST_PROFILE_CREATE,
                webApiPostRequest = WebApiPostRequest(),
                optionalGetParams = mapOf(WebApiParams.PROFILE_NAME to feedProfileName)
            )

            // Reconciliation: Trigger a full refresh and clear the pending state.
            triggerRefresh()
            pendingAddition.value = null

            success
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

    suspend fun updateFeedProfile(modifiedFeedProfile: FeedProfile) {
        val apiCallParameters: Map<String, String> = modifiedFeedProfile.getFeedProfileApiCallParameters()
        val success: Boolean = withContext(context = Dispatchers.IO) {
            webApiPostRequestExecution(
                sessionManager = sessionManager,
                apiUrl = WebApiUrls.FEED_POST_PROFILE_UPDATE,
                webApiPostRequest = WebApiPostRequest(),
                optionalGetParams = apiCallParameters
            )
        }
        if (success) {
            triggerRefresh()
        }
    }
}
