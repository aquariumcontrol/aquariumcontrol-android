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

package com.laimburggasse.aquariumcontrol.heating.data.repository

import com.laimburggasse.aquariumcontrol.common.data.remote.DataFetchResult
import com.laimburggasse.aquariumcontrol.common.data.remote.WebApiParams
import com.laimburggasse.aquariumcontrol.common.data.remote.WebApiUrls
import com.laimburggasse.aquariumcontrol.common.di.ApplicationScope
import com.laimburggasse.aquariumcontrol.common.di.IoDispatcher
import com.laimburggasse.aquariumcontrol.common.domain.ControllerProfile
import com.laimburggasse.aquariumcontrol.common.domain.GlobalConstants
import com.laimburggasse.aquariumcontrol.common.domain.SessionManager
import com.laimburggasse.aquariumcontrol.common.domain.model.SetValsSanityCheckResult
import com.laimburggasse.aquariumcontrol.common.util.Base64
import com.laimburggasse.aquariumcontrol.common.util.Logger
import com.laimburggasse.aquariumcontrol.heating.data.remote.HeatingSetValsApiService
import com.laimburggasse.aquariumcontrol.heating.data.remote.HeatingSetValsReader
import com.laimburggasse.aquariumcontrol.heating.domain.heatingSetValsSanityCheck
import com.laimburggasse.aquariumcontrol.heating.domain.model.HeatingSetVals
import com.laimburggasse.aquariumcontrol.heating.domain.repository.HeatingSetValsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
import kotlinx.coroutines.withContext
import me.tatarka.inject.annotations.Inject
import kotlin.time.Duration.Companion.milliseconds

/**
 * Implementation of [HeatingSetValsRepository] for managing heating setpoints.
 * Handles background fetching, optimistic UI updates, and remote synchronization.
 *
 * @property logger Utility for recording exceptions.
 * @property sessionManager Manager providing the active controller profile.
 * @property apiService Network service for heating configuration.
 * @property repositoryScope Coroutine scope for reactive flows.
 * @property context Dispatcher provider for IO operations.
 */
@ApplicationScope
open class HeatingSetValsRepositoryImpl @Inject constructor(
    private val logger: Logger,
    private val sessionManager: SessionManager,
    private val apiService: HeatingSetValsApiService,
    private val repositoryScope: CoroutineScope,
    private val context: IoDispatcher
): HeatingSetValsRepository {
    private val refreshTrigger = MutableSharedFlow<Unit>(replay = 1)
    private val pendingUpdate = MutableStateFlow<HeatingSetVals?>(value = null)
    private var heatingSetValsBackup: HeatingSetVals? = null
    private val failureResult = MutableStateFlow<DataFetchResult.Error?>(value = null)

    @OptIn(ExperimentalCoroutinesApi::class)
    private val networkDataFlow: StateFlow<DataFetchResult<HeatingSetVals>> = combine(
        flow = sessionManager.selectedControllerProfile,
        flow2 = refreshTrigger.onStart { emit(value = Unit) },
        flow3 = failureResult
    ) { controller, _, failure ->
        Pair(first = controller, second = failure)
    }
    .filter { it.first != null }
    .flatMapLatest { (controller: ControllerProfile?, failure: DataFetchResult.Error?) ->
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

            emit(value = DataFetchResult.Loading)

            val networkResult: DataFetchResult<HeatingSetVals> = withContext(context.dispatcher) {
                val url = "http://" + controller.address + ":" + controller.port + WebApiUrls.HEATING_LOAD_SET_VALS
                val heatingSetValsReader = HeatingSetValsReader(
                    logger, apiService, url, username = controller.user, password = controller.password
                )
                when (val setValsList: DataFetchResult<List<HeatingSetVals>> =
                    heatingSetValsReader.readHeatingSetVals()) {
                    is DataFetchResult.Success -> {
                        if (setValsList.data.isNotEmpty()) {
                            val firstItem: HeatingSetVals = setValsList.data.first()
                            val checkResult: SetValsSanityCheckResult<Unit> =
                                heatingSetValsSanityCheck(heatingSetVals = firstItem)
                            when (checkResult) {
                                is SetValsSanityCheckResult.Error -> {
                                    DataFetchResult.Error(
                                        exception = null,
                                        errorMessage = checkResult.errorMessage
                                    )
                                }
                                is SetValsSanityCheckResult.Success<*> -> {
                                    heatingSetValsBackup = firstItem
                                    DataFetchResult.Success(data = firstItem)
                                }
                            }
                        } else {
                            DataFetchResult.Error(
                                exception = null,
                                errorMessage = GlobalConstants.ERR_MSG_SERVER_RETURNED_EMPTY_LIST
                            )
                        }
                    }
                    is DataFetchResult.Error -> setValsList
                    is DataFetchResult.Loading -> DataFetchResult.Error(
                        exception = null,
                        errorMessage = GlobalConstants.ERR_MSG_LOADING_SET_VALS_FAILED
                    )
                }
            }
            emit(value = networkResult)
        }
    }.stateIn(
        scope = repositoryScope,
        started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000L),
        initialValue = DataFetchResult.Loading
    )

    /**
     * Reactive state flow emitting the current heating setpoints.
     * Combines network data with any pending local modifications.
     */
    override val heatingSetValsFlow: StateFlow<DataFetchResult<HeatingSetVals>> = combine(
        flow = networkDataFlow,
        flow2 = pendingUpdate
    ) { networkResult, pending ->
        if (pending != null) {
            DataFetchResult.Success(data = pending)
        } else {
            networkResult
        }
    }.stateIn(
        scope = repositoryScope,
        started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000L),
        initialValue = DataFetchResult.Loading
    )

    /**
     * Performs an optimistic update of the setpoints in the local UI state.
     *
     * @param heatingSetVals The new setpoint values to display immediately.
     */
    override fun updateLocalValues(heatingSetVals: HeatingSetVals) {
        pendingUpdate.value = heatingSetVals
    }

    /**
     * Commits the current optimistic setpoints to the remote server.
     * Triggers a refresh and manages the transition between pending and server state.
     */
    override suspend fun updateValues() {
        val currentValues: HeatingSetVals? = pendingUpdate.value ?:
            (heatingSetValsFlow.value as? DataFetchResult.Success)?.data

        val controllerAddress: String? = sessionManager.getControllerAddress()
        if (controllerAddress.isNullOrBlank()) {
            pendingUpdate.value = null
            failureResult.value = DataFetchResult.Error(
                exception = null,
                errorMessage = GlobalConstants.ERR_MSG_INVALID_CONTROLLER_ADDRESS
            )
            return
        }

        if (currentValues != null) {
            val user = sessionManager.getControllerUser() ?: ""
            val password = sessionManager.getControllerPassword() ?: ""
            val port = sessionManager.getControllerPort()

            val url = "http://" + controllerAddress + ":" + port + WebApiUrls.HEATING_UPDATE_SET_VALS
            val authHeader = "Basic " + Base64.encode(("$user:$password").encodeToByteArray())

            val postParams = mapOf(
                WebApiParams.HEATING_SWITCH_ON_TEMPERATURE to currentValues.heatingSwitchOnTemp.toString(),
                WebApiParams.HEATING_SWITCH_OFF_TEMPERATURE to currentValues.heatingSwitchOffTemp.toString()
            )

            val requestSuccess = withContext(context.dispatcher) {
                try {
                    apiService.updateHeatingSetVals(url, authHeader, postParams)
                } catch (e: Exception) {
                    logger.recordException(e)
                    false
                }
            }

            if (requestSuccess) {
                failureResult.value = null
                refreshTrigger.emit(value = Unit)
                // Wait for the next network success before clearing pendingUpdate to avoid flicker
                kotlinx.coroutines.delay(500.milliseconds) // Small delay to let the network fetch start
                pendingUpdate.value = null
            } else {
                pendingUpdate.value = null
                failureResult.value = DataFetchResult.Error(
                    exception = null,
                    errorMessage = GlobalConstants.ERR_MSG_UPDATING_SET_VALS_FAILED
                )
            }
        }
    }

    /**
     * Discards any unsaved local modifications and reverts to the server state.
     */
    override fun reset() {
        pendingUpdate.value = null
    }

    /**
     * Manually triggers a re-fetch of the heating setpoints from the server.
     */
    override suspend fun triggerRefresh() {
        if (failureResult.value != null) {
            failureResult.value = null
        } else {
            refreshTrigger.emit(value = Unit)
        }
    }
}
