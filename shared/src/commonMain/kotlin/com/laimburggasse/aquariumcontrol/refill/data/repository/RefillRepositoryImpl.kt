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

package com.laimburggasse.aquariumcontrol.refill.data.repository

import com.laimburggasse.aquariumcontrol.common.util.Base64



import com.laimburggasse.aquariumcontrol.common.data.remote.DataFetchResult
import com.laimburggasse.aquariumcontrol.common.data.remote.WebApiUrls
import com.laimburggasse.aquariumcontrol.common.di.IoDispatcher
import com.laimburggasse.aquariumcontrol.common.domain.ControllerProfile
import com.laimburggasse.aquariumcontrol.common.domain.SessionManager
import com.laimburggasse.aquariumcontrol.common.util.Logger
import com.laimburggasse.aquariumcontrol.refill.data.remote.RefillStateApiService
import com.laimburggasse.aquariumcontrol.refill.data.remote.RefillStateReader
import com.laimburggasse.aquariumcontrol.refill.domain.model.RefillState
import com.laimburggasse.aquariumcontrol.refill.domain.repository.RefillRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.tatarka.inject.annotations.Inject


/**
 * Implementation of [RefillRepository] for managing the aquarium refill system.
 * Handles state observation and dispatching control commands (Start, Stop, Reset).
 *
 * @property logger A utility for recording exceptions.
 * @property sessionManager The manager providing the currently selected controller.
 * @property apiService The network service for refill control.
 * @property repositoryScope The coroutine scope tied to the repository\'s lifecycle.
 * @property context The dispatcher provider for IO operations.
 */
open class RefillRepositoryImpl @Inject constructor(
    private val logger: Logger,
    private val sessionManager: SessionManager,
    private val apiService: RefillStateApiService,
    private val repositoryScope: CoroutineScope,
    private val context: IoDispatcher
): RefillRepository {
    /** Internal shared flow used to trigger a re-fetch of the refill state. */
    private val refreshTrigger = MutableSharedFlow<Unit>(replay = 1)

    /** Internal mutable flow for emitting error messages from failed commands. */
    private val _errorEvents = MutableSharedFlow<String>()

    /** Flow emitting error messages from failed command requests. */
    override val errorEvents = _errorEvents.asSharedFlow()

    /**
     * A reactive Flow that provides the Refill state.
     * It combines two sources:
     *  - The selected controller from SessionManager.
     *  - The manual refresh trigger.
     *
     * It automatically re-fetches data when any of these inputs change.
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    override val refillStateFlow: Flow<DataFetchResult<RefillState>> =
        combine(
            flow = sessionManager.selectedControllerProfile,
            flow2 = refreshTrigger.onStart { emit(value = Unit) }, // Trigger on first collection
        ) { controller, _ ->
            Pair(first = controller, second = Unit)
        }
            .filter { it.first != null } // Only proceed if a controller is selected
            .flatMapLatest { (controller: ControllerProfile?, _) ->
                flow {
                    // UI shall temporarily show loading state, while fetching data
                    emit(value = DataFetchResult.Loading)

                    val url = "http://${controller!!.address}:${controller.port}${WebApiUrls.REFILL_LOAD_STATE}"

                    val result: DataFetchResult<RefillState> =
                        withContext(context.dispatcher) {
                            val reader = RefillStateReader(
                                logger,
                                apiService,
                                url,
                                username = controller.user,
                                password = controller.password

                            )
                            reader.readRefillState()
                        }

                    emit(value = result)
                }
            }
            .stateIn(
                scope = repositoryScope,
                started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
                initialValue = DataFetchResult.Loading
            )

        /**
     * Sends a request to the controller to start the automatic refill process.
     */
    override suspend fun sendStartRequest() {
        executeRequest(apiUrl = WebApiUrls.REFILL_CONTROL_START)
    }

        /**
     * Sends a request to the controller to stop any active refill process.
     */
    override suspend fun sendStopRequest() {
        executeRequest(apiUrl = WebApiUrls.REFILL_CONTROL_STOP)
    }

        /**
     * Sends a request to the controller to reset any active refill system errors.
     */
    override suspend fun sendResetRequest() {
        executeRequest(apiUrl = WebApiUrls.REFILL_CONTROL_RESET)
    }

        /**
     * Executes a POST request to a specific refill control endpoint.
     *
     * @param apiUrl The specific API endpoint path to call.
     */
    private fun executeRequest(apiUrl: String) {
        val user = sessionManager.getControllerUser() ?: ""
        val password = sessionManager.getControllerPassword() ?: ""
        val controllerAddress: String? = sessionManager.getControllerAddress()
        if (controllerAddress.isNullOrBlank()) {
            return
        }
        val controllerPort = sessionManager.getControllerPort()
        val url = "http://${controllerAddress}:${controllerPort}${apiUrl}"
        val authHeader = "Basic " + Base64.encode("$user:$password".encodeToByteArray())

        repositoryScope.launch(context.dispatcher) {
            val requestSuccess = apiService.resetRefillErrors(
                url, authHeader
            )
            if (!requestSuccess) {
                _errorEvents.emit(value = "Request failed. Please check connection and server configuration and try again.")
            }
        }
    }

    /**
     * Public function for the ViewModel to trigger a manual refresh.
     */
    override suspend fun triggerRefresh() {
        refreshTrigger.emit(value = Unit)
    }
}