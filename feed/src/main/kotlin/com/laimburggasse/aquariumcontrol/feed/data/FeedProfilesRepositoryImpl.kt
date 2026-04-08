/*
 * Copyright 2025 Uwe Martin
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.laimburggasse.aquariumcontrol.feed.data

import android.util.Log
import com.laimburggasse.aquariumcontrol.common.domain.GlobalConstants
import com.laimburggasse.aquariumcontrol.common.domain.SessionManager
import com.laimburggasse.aquariumcontrol.common.domain.ControllerProfile
import com.laimburggasse.aquariumcontrol.common.di.ApplicationScope
import com.laimburggasse.aquariumcontrol.feed.data.remote.FeedProfileReader
import com.laimburggasse.aquariumcontrol.feed.data.remote.dto.FeedProfileImport
import com.laimburggasse.aquariumcontrol.feed.domain.model.FeedProfile
import com.laimburggasse.aquariumcontrol.feed.domain.repository.FeedProfilesRepository
import com.laimburggasse.aquariumcontrol.common.data.remote.DataFetchResult
import com.laimburggasse.aquariumcontrol.common.data.remote.WebApiParams
import com.laimburggasse.aquariumcontrol.common.data.remote.WebApiPostRequest
import com.laimburggasse.aquariumcontrol.common.data.remote.WebApiUrls
import com.laimburggasse.aquariumcontrol.common.data.remote.webApiPostRequestExecution
import com.laimburggasse.aquariumcontrol.feed.data.remote.FeedProfileApiService
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
open class FeedProfilesRepositoryImpl @Inject constructor(
    private val sessionManager: SessionManager,
    private val apiService: FeedProfileApiService,
    @param:ApplicationScope private val repositoryScope: CoroutineScope
): FeedProfilesRepository {
    private val refreshTrigger = MutableSharedFlow<Unit>(replay = 1)

    // Holds a list of profile IDs that are pending deletions (POST request triggered, local update required).
    private val pendingDeletions = MutableStateFlow<Set<Int>>(value = emptySet())

    // A flow to hold the failure value
    private val failureResult = MutableStateFlow<DataFetchResult.Error?>(value = null)

    @OptIn(ExperimentalCoroutinesApi::class)
    override val profilesFlow: StateFlow<DataFetchResult<List<FeedProfile>>> =
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
                        errorMessage = GlobalConstants.ERR_MSG_NO_CONTROLLER_SELECTED
                    ))
                    return@flow
                }

                // If there is no failure and we have a controller, proceed with loading.
                emit(value = DataFetchResult.Loading)

                val networkResult: DataFetchResult<List<FeedProfileImport>> =
                    withContext(context = Dispatchers.IO) {
                        val url = "http://${controller.address}:${controller.port}${WebApiUrls.FEED_LOAD_PROFILES}"
                        val profileReader = FeedProfileReader(
                            apiService, url, username = controller.user, password = controller.password
                        )
                        profileReader.readFeedProfiles()
                    }

                // Merge optimistic updates with network result
                val finalResult: DataFetchResult<List<FeedProfile>> = when (networkResult) {
                    is DataFetchResult.Success -> {
                        var profiles: List<FeedProfile> = networkResult.data.map {
                            FeedProfile.fromFeedProfileImport(feedProfileImport = it)
                        }
                        Log.d("FeedProfilesRepositoryImpl", "profiles: $profiles")
                        // Apply pending deletions to the fetched list
                        if (deletions.isNotEmpty()) {
                            profiles = profiles.filterNot { deletions.contains(it.profileID) }
                        }

                        DataFetchResult.Success(data = profiles)
                    }
                    is DataFetchResult.Error -> networkResult // Pass through errors
                    is DataFetchResult.Loading -> DataFetchResult.Error(
                        exception = null,
                        errorMessage = GlobalConstants.ERR_MSG_LOADING_FEED_PROFILES_FAILED
                    )
                }
                emit(value = finalResult)
            }
        }.stateIn(
            scope = repositoryScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000L),
            initialValue = DataFetchResult.Loading
        )

    override suspend fun deleteFeedProfile(feedProfile: FeedProfile) {
        // Optimistic Update: Immediately add the ID to the pending deletions list.
        // The UI will react to this instantly.
        pendingDeletions.value += feedProfile.profileID

        // Perform the actual network request in the background.
        repositoryScope.launch(context = Dispatchers.IO) {
            val success: Boolean = webApiPostRequestExecution(
                apiUrl = WebApiUrls.FEED_DELETE_PROFILE + feedProfile.profileID,
                sessionManager = sessionManager,
                webApiPostRequest = WebApiPostRequest(),
                getParams = feedProfile.getFeedProfileApiCallParameters()
            )
            if (!success) {
                failureResult.value = DataFetchResult.Error(
                    exception = null,
                    errorMessage = GlobalConstants.ERR_MSG_COULD_NOT_DELETE_FEED_PROFILE
                )
            }
        }
    }

    override suspend fun addFeedProfile(feedProfileName: String) {
        repositoryScope.launch(context = Dispatchers.IO) {
            val success: Boolean = webApiPostRequestExecution(
                sessionManager = sessionManager,
                apiUrl = WebApiUrls.FEED_PROFILE_CREATE,
                webApiPostRequest = WebApiPostRequest(),
                postParams = mapOf(WebApiParams.PROFILE_NAME to feedProfileName)
            )
            if (success) {
                delay(timeMillis = 3000L)
                triggerRefresh()
            } else {
                failureResult.value = DataFetchResult.Error(
                    exception = null,
                    errorMessage = GlobalConstants.ERR_MSG_COULD_NOT_ADD_FEED_PROFILE
                )
            }
        }
    }

    override fun findProfileById(profileId: Int): FeedProfile? {
        val currentData: DataFetchResult<List<FeedProfile>> = profilesFlow.value
        return if (currentData is DataFetchResult.Success) {
            currentData.data.find { it.profileID == profileId }
        } else {
            null
        }
    }

    override fun findProfileByName(profileName: String): FeedProfile? {
        val currentData: DataFetchResult<List<FeedProfile>> = profilesFlow.value
        return if (currentData is DataFetchResult.Success) {
            currentData.data.find { it.profileName == profileName }
        } else {
            null
        }
    }

    override fun updateFeedProfile(modifiedFeedProfile: FeedProfile) {
        val apiCallParameters: Map<String, String> = modifiedFeedProfile.getFeedProfileApiCallParameters()
        repositoryScope.launch(context = Dispatchers.IO) {
            val success: Boolean = webApiPostRequestExecution(
                sessionManager = sessionManager,
                apiUrl = WebApiUrls.FEED_PROFILE_UPDATE,
                webApiPostRequest = WebApiPostRequest(),
                postParams = apiCallParameters
            )
            if (!success) {
                failureResult.value = DataFetchResult.Error(
                    exception = null,
                    errorMessage = GlobalConstants.ERR_MSG_UPDATING_FEED_PROFILE_FAILED
                )
            }
        }
        // Refresh is not required since this call emanates from FeedProfileEditViewModel.
    }

    /**
     * Public function to allow the ViewModel to trigger a manual refresh (e.g., for pull-to-refresh).
     */
    override suspend fun triggerRefresh() {
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