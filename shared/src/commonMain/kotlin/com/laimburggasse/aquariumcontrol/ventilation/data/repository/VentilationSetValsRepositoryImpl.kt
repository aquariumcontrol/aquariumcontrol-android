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

package com.laimburggasse.aquariumcontrol.ventilation.data.repository

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
import com.laimburggasse.aquariumcontrol.ventilation.data.remote.VentilationSetValsApiService
import com.laimburggasse.aquariumcontrol.ventilation.data.remote.VentilationSetValsReader
import com.laimburggasse.aquariumcontrol.ventilation.domain.model.VentilationSetVals
import com.laimburggasse.aquariumcontrol.ventilation.domain.repository.VentilationSetValsRepository
import com.laimburggasse.aquariumcontrol.ventilation.domain.ventilationSetValsSanityCheck
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
 * Implementation of [VentilationSetValsRepository] for managing ventilation thresholds.
 * Handles background fetching, optimistic UI updates, and remote synchronization.
 *
 * @property logger Utility for recording exceptions.
 * @property sessionManager Manager providing the active controller profile.
 * @property apiService Network service for ventilation configuration.
 * @property context Dispatcher provider for IO operations.
 * @param repositoryScope Coroutine scope for reactive flows.
 */
@ApplicationScope
open class VentilationSetValsRepositoryImpl @Inject constructor(
    private val logger: Logger,
    private val sessionManager: SessionManager,
    private val apiService: VentilationSetValsApiService,
    repositoryScope: CoroutineScope,
    private val context: IoDispatcher
): VentilationSetValsRepository {
    /** Internal trigger flow for manually refreshing the ventilation data. */
    private val refreshTrigger = MutableSharedFlow<Unit>(replay = 1)

    /** Internal state flow holding optimistic local modifications pending server confirmation. */
    private val pendingUpdate = MutableStateFlow<VentilationSetVals?>(value = null)

    /** Backup of the last known successful setpoints. */
    private var ventilationSetValsBackup: VentilationSetVals? = null

    /** Internal flow holding the last network or validation error. */
    private val failureResult = MutableStateFlow<DataFetchResult.Error?>(value = null)

    /**
     * Internal reactive flow that performs the actual network fetch for ventilation setpoints.
     * Reacts to controller changes, manual refreshes, and failure resets.
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    private val networkDataFlow: StateFlow<DataFetchResult<VentilationSetVals>> = combine(
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

            val networkResult: DataFetchResult<VentilationSetVals> = withContext(context.dispatcher) {
                val url = "http://" + controller.address + ":" + controller.port + WebApiUrls.VENTILATION_LOAD_SET_VALS
                val ventilationSetValsReader = VentilationSetValsReader(
                    logger, apiService, url, username = controller.user, password = controller.password
                )
                when (val setValsList: DataFetchResult<List<VentilationSetVals>> =
                    ventilationSetValsReader.readVentilationSetVals()) {
                    is DataFetchResult.Success -> {
                        if (setValsList.data.isNotEmpty()) {
                            val firstItem: VentilationSetVals = setValsList.data.first()
                            val checkResult: SetValsSanityCheckResult<Unit> = ventilationSetValsSanityCheck(ventilationSetVals = firstItem)
                            when (checkResult) {
                                is SetValsSanityCheckResult.Error -> {
                                    DataFetchResult.Error(
                                        exception = null,
                                        errorMessage = checkResult.errorMessage
                                    )
                                }
                                is SetValsSanityCheckResult.Success<*> -> {
                                    ventilationSetValsBackup = firstItem
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
     * Reactive state flow emitting the current ventilation thresholds.
     * Integrates network results with pending local modifications.
     */
    override val ventilationSetValsFlow: StateFlow<DataFetchResult<VentilationSetVals>> = combine(
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
     * Updates the local optimistic state of the ventilation thresholds.
     *
     * @param ventilationSetVals The new values to display immediately.
     */
    override fun updateLocalValues(ventilationSetVals: VentilationSetVals) {
        pendingUpdate.value = ventilationSetVals
    }

    /**
     * Commits the current optimistic modifications to the remote server.
     */
    override suspend fun updateValues() {
        val currentValues: VentilationSetVals? = pendingUpdate.value ?:
        (ventilationSetValsFlow.value as? DataFetchResult.Success)?.data

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

            val url = "http://" + controllerAddress + ":" + port + WebApiUrls.VENTILATION_UPDATE_SET_VALS
            val authHeader = "Basic " + Base64.encode(("$user:$password").encodeToByteArray())
            val postParams = mapOf(
                WebApiParams.VENTILATION_SWITCH_ON_TEMPERATURE to currentValues.ventilationSwitchOnTemp.toString(),
                WebApiParams.VENTILATION_SWITCH_OFF_TEMPERATURE to currentValues.ventilationSwitchOffTemp.toString()
            )

            val requestSuccess = withContext(context.dispatcher) {
                try {
                    apiService.updateVentilationSetVals(url, authHeader, postParams)
                } catch (e: Exception) {
                    logger.recordException(e)
                    false
                }
            }

            if (requestSuccess) {
                failureResult.value = null
                refreshTrigger.emit(value = Unit)
                kotlinx.coroutines.delay(500.milliseconds)
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
     * Manually triggers a re-fetch of the ventilation setpoints from the server.
     */
    override suspend fun triggerRefresh() {
        if (failureResult.value != null) {
            failureResult.value = null
        } else {
            refreshTrigger.emit(value = Unit)
        }
    }
}
