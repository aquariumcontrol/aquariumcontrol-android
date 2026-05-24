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

package com.laimburggasse.aquariumcontrol.balling.data.repository

import com.laimburggasse.aquariumcontrol.balling.data.remote.BallingProfileApiService
import com.laimburggasse.aquariumcontrol.balling.data.remote.BallingProfileReader
import com.laimburggasse.aquariumcontrol.balling.domain.model.BallingProfile
import com.laimburggasse.aquariumcontrol.balling.domain.model.BallingProfilesState
import com.laimburggasse.aquariumcontrol.balling.domain.repository.BallingProfilesRepository
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
import com.laimburggasse.aquariumcontrol.common.util.DispatcherIO
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.tatarka.inject.annotations.Inject

@ApplicationScope
open class BallingProfilesRepositoryImpl @Inject constructor(
    private val logger: Logger,
    private val sessionManager: SessionManager,
    private val apiService: BallingProfileApiService,
    private val repositoryScope: CoroutineScope,
    private val ioDispatcher: IoDispatcher
): BallingProfilesRepository {
    private val refreshTrigger = MutableSharedFlow<Unit>(replay = 1)
    private val localState = MutableStateFlow(BallingProfilesState())

    @OptIn(ExperimentalCoroutinesApi::class)
    override val ballingProfiles: StateFlow<DataFetchResult<List<BallingProfile>>> =
        combine<ControllerProfile?, Unit, BallingProfilesState, Pair<ControllerProfile?, BallingProfilesState>>(
            flow = sessionManager.selectedControllerProfile,
            flow2 = refreshTrigger.onStart { emit(value = Unit) },
            flow3 = localState,
        ) { controller, _, state ->
            Pair(first = controller, second = state)
        }
        .filter{ it.first != null }
        .flatMapLatest<Pair<ControllerProfile?, BallingProfilesState>, DataFetchResult<List<BallingProfile>>> { (controller, state) ->
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

                val url = "http://${controller.address}:${controller.port}${WebApiUrls.BALLING_LOAD_SET_VALS}"
                val reader = BallingProfileReader(logger, apiService, url, username = controller.user, password = controller.password)
                val networkResult = withContext(ioDispatcher.dispatcher) {
                    reader.readBallingProfiles()
                }
                val finalResult: DataFetchResult<List<BallingProfile>> = if (networkResult is DataFetchResult.Success) {
                    if (state.pendingUpdate != null) {
                        val mergedList = networkResult.data.map { networkProfile ->
                            if (networkProfile.pumpId == state.pendingUpdate.pumpId) state.pendingUpdate else networkProfile
                        }
                        DataFetchResult.Success(data = mergedList)
                    } else { networkResult }
                } else { networkResult }
                emit(value = finalResult)
            }
        }.stateIn(scope = repositoryScope, started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000), initialValue = DataFetchResult.Loading)

    override fun updateBallingProfile(ballingProfile: BallingProfile) {
        localState.update { it.copy(pendingUpdate = ballingProfile) }
        val controllerAddress = sessionManager.getControllerAddress()
        if (controllerAddress.isNullOrBlank()) {
            localState.update { it.copy(pendingUpdate = null, failureResult = DataFetchResult.Error(exception = null, errorMessage = GlobalConstants.ERR_MSG_INVALID_CONTROLLER_ADDRESS)) }
            return
        }

        repositoryScope.launch(ioDispatcher.dispatcher) {
            val user = sessionManager.getControllerUser() ?: ""
            val password = sessionManager.getControllerPassword() ?: ""
            val port = sessionManager.getControllerPort()
            val url = "http://$controllerAddress:$port${WebApiUrls.BALLING_UPDATE_SET_VALS}"
            val authHeader = "Basic " + Base64.encode("$user:$password".encodeToByteArray())
            val postParams = mapOf(WebApiParams.PUMP_ID to ballingProfile.pumpId.toString(), WebApiParams.DOSING_VOLUME to ballingProfile.volume.toString())

            val requestSuccess = try {
                apiService.updateBallingProfile(url, authHeader, postParams)
            } catch (e: Exception) {
                logger.recordException(e)
                false
            }

            if (requestSuccess) {
                localState.update { it.copy(pendingUpdate = null, failureResult = null) }
                triggerRefresh()
            } else {
                localState.update { it.copy(pendingUpdate = null, failureResult = DataFetchResult.Error(exception = null, errorMessage = GlobalConstants.ERR_MSG_UPDATING_BALLING_PROFILE_FAILED)) }
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
}
