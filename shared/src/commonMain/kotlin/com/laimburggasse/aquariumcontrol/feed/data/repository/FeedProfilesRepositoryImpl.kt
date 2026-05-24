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

package com.laimburggasse.aquariumcontrol.feed.data.repository

import com.laimburggasse.aquariumcontrol.common.data.remote.DataFetchResult
import com.laimburggasse.aquariumcontrol.common.data.remote.WebApiParams
import com.laimburggasse.aquariumcontrol.common.data.remote.WebApiUrls
import com.laimburggasse.aquariumcontrol.common.di.ApplicationScope
import com.laimburggasse.aquariumcontrol.common.di.IoDispatcher
import com.laimburggasse.aquariumcontrol.common.domain.ControllerProfile
import com.laimburggasse.aquariumcontrol.common.domain.GlobalConstants
import com.laimburggasse.aquariumcontrol.common.domain.SessionManager
import com.laimburggasse.aquariumcontrol.common.util.Logger
import com.laimburggasse.aquariumcontrol.common.util.Base64
import com.laimburggasse.aquariumcontrol.feed.data.remote.FeedProfileApiService
import com.laimburggasse.aquariumcontrol.feed.data.remote.FeedProfileReader
import com.laimburggasse.aquariumcontrol.feed.data.remote.dto.FeedProfileImport
import com.laimburggasse.aquariumcontrol.feed.domain.model.FeedProfile
import com.laimburggasse.aquariumcontrol.feed.domain.model.FeedProfileMapper
import com.laimburggasse.aquariumcontrol.feed.domain.model.FeedProfilesState
import com.laimburggasse.aquariumcontrol.feed.domain.repository.FeedProfilesRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.tatarka.inject.annotations.Inject

@ApplicationScope
open class FeedProfilesRepositoryImpl @Inject constructor(
    private val logger: Logger,
    private val sessionManager: SessionManager,
    private val apiService: FeedProfileApiService,
    private val repositoryScope: CoroutineScope,
    private val ioDispatcher: IoDispatcher
): FeedProfilesRepository {
    private val refreshTrigger = MutableSharedFlow<Unit>(replay = 1)
    private val localState = MutableStateFlow(FeedProfilesState())

    @OptIn(ExperimentalCoroutinesApi::class)
    override val profilesFlow: StateFlow<DataFetchResult<List<FeedProfile>>> =
        combine<ControllerProfile?, Unit, FeedProfilesState, Pair<ControllerProfile?, FeedProfilesState>>(
            flow = sessionManager.selectedControllerProfile,
            flow2 = refreshTrigger.onStart { emit(value = Unit) },
            flow3 = localState,
        ) { controller, _, state ->
            Pair(first = controller, second = state)
        }
        .filter{ it.first != null }
        .flatMapLatest<Pair<ControllerProfile?, FeedProfilesState>, DataFetchResult<List<FeedProfile>>> { (controller, state) ->
            flow {
                if (state.failureResult != null) {
                    emit(value = state.failureResult)
                    return@flow
                }
                if (controller == null) {
                    emit(value = DataFetchResult.Error(exception = null, errorMessage = GlobalConstants.ERR_MSG_NO_CONTROLLER_SELECTED))
                    return@flow
                }
                emit(value = DataFetchResult.Loading)

                val networkResult: DataFetchResult<List<FeedProfileImport>> =
                    withContext(context = ioDispatcher.dispatcher) {
                        val url = "http://${controller.address}:${controller.port}${WebApiUrls.FEED_LOAD_PROFILES}"
                        val profileReader = FeedProfileReader(
                            logger, apiService, url, username = controller.user, password = controller.password
                        )
                        profileReader.readFeedProfiles()
                    }

                val finalResult: DataFetchResult<List<FeedProfile>> = when (networkResult) {
                    is DataFetchResult.Success -> {
                        var profiles = networkResult.data.map {
                            FeedProfileMapper.fromFeedProfileImport(it)
                        }
                        if (state.pendingDeletions.isNotEmpty()) {
                            profiles = profiles.filterNot { state.pendingDeletions.contains(it.profileID) }
                        }
                        if (state.pendingUpdates.isNotEmpty()) {
                            profiles = profiles.map { profile ->
                                state.pendingUpdates[profile.profileID] ?: profile
                            }
                        }
                        DataFetchResult.Success(data = profiles)
                    }
                    is DataFetchResult.Error -> networkResult
                    is DataFetchResult.Loading -> DataFetchResult.Error(exception = null, errorMessage = GlobalConstants.ERR_MSG_LOADING_FEED_PROFILES_FAILED)
                }
                emit(value = finalResult)
            }
        }.stateIn(scope = repositoryScope, started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000L), initialValue = DataFetchResult.Loading)

    override suspend fun deleteFeedProfile(feedProfile: FeedProfile) {
        val user = sessionManager.getControllerUser() ?: ""
        val password = sessionManager.getControllerPassword() ?: ""
        val port = sessionManager.getControllerPort()
        val controllerAddress = sessionManager.getControllerAddress()
        if (controllerAddress.isNullOrBlank()) {
            reportInvalidControllerAddress()
            return
        }
        val url = "http://$controllerAddress:$port${WebApiUrls.FEED_DELETE_PROFILE}" + feedProfile.profileID
        val authHeader = "Basic " + Base64.encode("$user:$password".encodeToByteArray())

        localState.update { it.copy(pendingDeletions = it.pendingDeletions + feedProfile.profileID) }

        repositoryScope.launch(ioDispatcher.dispatcher) {
            val requestSuccess = try {
                apiService.deleteFeedProfile(url, authHeader)
            } catch (e: Exception) {
                logger.recordException(e)
                false
            }

            if (!requestSuccess) {
                localState.update { it.copy(
                        pendingDeletions = it.pendingDeletions - feedProfile.profileID,
                        failureResult = DataFetchResult.Error(exception = null, errorMessage = GlobalConstants.ERR_MSG_COULD_NOT_DELETE_FEED_PROFILE)
                    )
                }
            } else {
                triggerRefresh()
            }
        }
    }

    override suspend fun addFeedProfile(feedProfileName: String) {
        val user = sessionManager.getControllerUser() ?: ""
        val password = sessionManager.getControllerPassword() ?: ""
        val port = sessionManager.getControllerPort()
        val controllerAddress = sessionManager.getControllerAddress()
        if (controllerAddress.isNullOrBlank()) {
            reportInvalidControllerAddress()
            return
        }
        val url = "http://$controllerAddress:$port${WebApiUrls.FEED_PROFILE_CREATE}"
        val authHeader = "Basic " + Base64.encode("$user:$password".encodeToByteArray())

        repositoryScope.launch(ioDispatcher.dispatcher) {
            val requestSuccess = try {
                apiService.addFeedProfile(url, authHeader, params = mapOf(WebApiParams.PROFILE_NAME to feedProfileName))
            } catch (e: Exception) {
                logger.recordException(e)
                false
            }
            if (requestSuccess) {
                delay(timeMillis = 3000L)
                triggerRefresh()
            } else {
                localState.update { it.copy(failureResult = DataFetchResult.Error(exception = null, errorMessage = GlobalConstants.ERR_MSG_COULD_NOT_ADD_FEED_PROFILE)) }
            }
        }
    }

    override fun findProfileById(profileId: Int): FeedProfile? {
        val currentData = profilesFlow.value
        return if (currentData is DataFetchResult.Success) currentData.data.find { it.profileID == profileId } else null
    }

    override fun findProfileByName(profileName: String): FeedProfile? {
        val currentData = profilesFlow.value
        return if (currentData is DataFetchResult.Success) currentData.data.find { it.profileName == profileName } else null
    }

    override fun updateFeedProfile(modifiedFeedProfile: FeedProfile) {
        val user = sessionManager.getControllerUser() ?: ""
        val password = sessionManager.getControllerPassword() ?: ""
        val port = sessionManager.getControllerPort()
        val controllerAddress = sessionManager.getControllerAddress()
        if (controllerAddress.isNullOrBlank()) {
            reportInvalidControllerAddress()
            return
        }
        val url = "http://$controllerAddress:$port${WebApiUrls.FEED_PROFILE_UPDATE}"
        val apiCallParameters = modifiedFeedProfile.getFeedProfileApiCallParameters()
        val authHeader = "Basic " + Base64.encode("$user:$password".encodeToByteArray())

        localState.update { it.copy(pendingUpdates = it.pendingUpdates + (modifiedFeedProfile.profileID to modifiedFeedProfile)) }

        repositoryScope.launch(ioDispatcher.dispatcher) {
            val requestSuccess = try {
                apiService.updateFeedProfile(url, authHeader, apiCallParameters)
            } catch (e: Exception) {
                logger.recordException(e)
                false
            }

            if (!requestSuccess) {
                localState.update { it.copy(
                        pendingUpdates = it.pendingUpdates - modifiedFeedProfile.profileID,
                        failureResult = DataFetchResult.Error(exception = null, errorMessage = GlobalConstants.ERR_MSG_UPDATING_FEED_PROFILE_FAILED)
                    )
                }
            } else {
                localState.update { it.copy(pendingUpdates = it.pendingUpdates - modifiedFeedProfile.profileID) }
            }
        }
    }

    override suspend fun triggerRefresh() {
        if (localState.value.failureResult != null) {
            localState.update { it.copy(failureResult = null) }
        } else {
            refreshTrigger.emit(value = Unit)
        }
    }

    private fun reportInvalidControllerAddress() {
        localState.update { it.copy(failureResult = DataFetchResult.Error(exception = null, errorMessage = GlobalConstants.ERR_MSG_INVALID_CONTROLLER_ADDRESS)) }
    }
}
