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

package com.laimburggasse.aquariumcontrol.balling.data

import com.google.firebase.Firebase
import com.google.firebase.crashlytics.crashlytics
import com.laimburggasse.aquariumcontrol.balling.data.remote.BallingProfileApiService
import com.laimburggasse.aquariumcontrol.balling.data.remote.BallingProfileReader
import com.laimburggasse.aquariumcontrol.balling.domain.model.BallingProfile
import com.laimburggasse.aquariumcontrol.balling.domain.repository.BallingProfilesRepository
import com.laimburggasse.aquariumcontrol.common.data.remote.DataFetchResult
import com.laimburggasse.aquariumcontrol.common.data.remote.WebApiParams
import com.laimburggasse.aquariumcontrol.common.data.remote.WebApiUrls
import com.laimburggasse.aquariumcontrol.common.di.ApplicationScope
import com.laimburggasse.aquariumcontrol.common.domain.ControllerProfile
import com.laimburggasse.aquariumcontrol.common.domain.GlobalConstants
import com.laimburggasse.aquariumcontrol.common.domain.SessionManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.Credentials
import javax.inject.Inject

open class BallingProfilesRepositoryImpl @Inject constructor(
    private val sessionManager: SessionManager,
    private val apiService: BallingProfileApiService,
    @param:ApplicationScope private val repositoryScope: CoroutineScope
): BallingProfilesRepository {

    // For triggering manual refreshes
    private val refreshTrigger = MutableSharedFlow<Unit>(replay = 1)

    // A flow to hold the (potential) single pending optimistic update and the (potential) failure value
    // This way we avoid race conditions when updating both in sequence.
    private val localState = MutableStateFlow(BallingProfilesState())

    /**
     * A single, reactive Flow that provides the Balling Profiles.
     * It combines two triggers: the selected controller and the manual refresh trigger.
     * Whenever EITHER of them emits, it re-fetches the data.
     */
    @Suppress("UNCHECKED_CAST")
    @OptIn(ExperimentalCoroutinesApi::class)
    override val ballingProfiles: Flow<DataFetchResult<List<BallingProfile>>> =
        combine(
            // 1. We still listen to the selected controller profile.
            flow = sessionManager.selectedControllerProfile,
            flow2 = refreshTrigger.onStart { emit(value = Unit) },
            flow3 = localState,
        ) { controller, _, localState ->
            Pair(first = controller, second = localState)
        }
        .filter{ it.first != null }
        .flatMapLatest { (controller: ControllerProfile?, localState: BallingProfilesState) ->
            flow {
                if (localState.failureResult != null) {
                    emit(value = localState.failureResult)
                    return@flow
                }
                if (controller == null) {
                    emit(value = DataFetchResult.Error(
                        exception = null,
                        errorMessage = GlobalConstants.ERR_MSG_NO_CONTROLLER_SELECTED
                    ))
                    return@flow
                }

                // If there is no failure, and we have a controller, proceed with loading.
                emit(value = DataFetchResult.Loading)

                val url = "http://${controller.address}:${controller.port}${WebApiUrls.BALLING_LOAD_SET_VALS}"
                val reader = BallingProfileReader(
                    apiService, url, username = controller.user, password = controller.password
                )
                // Perform the network request on a background thread.
                val networkResult: DataFetchResult<List<BallingProfile>> =
                    withContext(context = Dispatchers.IO) {
                        reader.readBallingProfiles()
                    }
                val finalResult: DataFetchResult<List<BallingProfile>> = if (
                        networkResult is DataFetchResult.Success
                ) {
                    // If there is a pending update, merge it now.
                    if (localState.pendingUpdate != null) {
                        val mergedList: List<BallingProfile> = networkResult.data.map { networkProfile ->
                            // If this profile's pumpId matches the pending update, use the updated version.
                            // Otherwise, use the one from the network.
                            if (networkProfile.pumpId == localState.pendingUpdate.pumpId)
                                localState.pendingUpdate
                            else
                                networkProfile
                        }
                        DataFetchResult.Success(data = mergedList)
                    } else { networkResult} // There are no pending updates, just use the network result.
                } else { networkResult} // If the network call failed, just pass on the error.
                // Emit the final result: local update merged into network result.
                emit(value = finalResult)
            }
        }
        .stateIn(
            scope = repositoryScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
            initialValue = DataFetchResult.Loading
        )

    override fun updateBallingProfile(ballingProfile: BallingProfile) {
        // set the updated profile as the pending update
        // this will trigger instant UI update
        val controllerAddress: String? = sessionManager.getControllerAddress()
        if (controllerAddress.isNullOrBlank()) {
            localState.update { currentState -> currentState.copy(
                    pendingUpdate = null,
                    failureResult = DataFetchResult.Error(
                        exception = null,
                        errorMessage = GlobalConstants.ERR_MSG_INVALID_CONTROLLER_ADDRESS
                    )
                )
            }
            return
        }

        repositoryScope.launch(context = Dispatchers.IO) {
            // Retrieve credentials and construct the dynamic URL
            val user = sessionManager.getControllerUser() ?: ""
            val password = sessionManager.getControllerPassword() ?: ""
            val port = sessionManager.getControllerPort()

            val url = "http://$controllerAddress:$port${WebApiUrls.BALLING_UPDATE_SET_VALS}"
            val authHeader = Credentials.basic(user, password)

            val postParams = mapOf(
                WebApiParams.PUMP_ID to ballingProfile.pumpId.toString(),
                WebApiParams.DOSING_VOLUME to ballingProfile.volume.toString()
            )

            // Make the Retrofit call
            val requestSuccess = try {
                val response = apiService.updateBallingProfile(url, authHeader, postParams)
                response.isSuccessful
            } catch (e: Exception) { // recording exception in crashlytics
                Firebase.crashlytics.recordException(e)
                false // Catch network timeouts or connection errors
            }

            if (requestSuccess) {
                localState.update { currentState ->
                    currentState.copy(
                        pendingUpdate = null,
                        failureResult = null
                    )
                }
                refreshTrigger.emit(value = Unit) // This will cause the `profilesFlow` to re-fetch.
            } else {
                localState.update { currentState ->
                    currentState.copy(
                        pendingUpdate = null,
                        failureResult = DataFetchResult.Error(
                            exception = null,
                            errorMessage = GlobalConstants.ERR_MSG_UPDATING_BALLING_PROFILE_FAILED
                        )
                    )
                }
            }
        }
    }

    /**
     * Public function to allow the ViewModel to trigger a manual refresh (e.g., for pull-to-refresh).
     */
    override suspend fun triggerRefresh() {
        // Check if the current state is a failure.
        if (localState.value.failureResult != null) {
            // If it is, setting it to null is sufficient to trigger the combine/flatMapLatest chain again.
            localState.update { currentState ->
                currentState.copy(failureResult = null)
            }
        } else {
            // If the system is NOT in a failure state, then we must emit the refreshTrigger
            // to force a new fetch, as nothing else has changed.
            refreshTrigger.emit(value = Unit)
        }
    }
}
