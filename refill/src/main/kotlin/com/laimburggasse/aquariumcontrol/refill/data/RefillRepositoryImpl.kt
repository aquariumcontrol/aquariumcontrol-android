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

package com.laimburggasse.aquariumcontrol.refill.data

import com.laimburggasse.aquariumcontrol.common.data.remote.DataFetchResult
import com.laimburggasse.aquariumcontrol.common.data.remote.WebApiUrls
import com.laimburggasse.aquariumcontrol.common.di.ApplicationScope
import com.laimburggasse.aquariumcontrol.common.domain.ControllerProfile
import com.laimburggasse.aquariumcontrol.common.domain.SessionManager
import com.laimburggasse.aquariumcontrol.refill.data.remote.RefillStateApiService
import com.laimburggasse.aquariumcontrol.refill.data.remote.RefillStateReader
import com.laimburggasse.aquariumcontrol.refill.domain.model.RefillState
import com.laimburggasse.aquariumcontrol.refill.domain.repository.RefillRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
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
import okhttp3.Credentials
import javax.inject.Inject

open class RefillRepositoryImpl @Inject constructor(
    private val sessionManager: SessionManager,
    private val apiService: RefillStateApiService,
    @param:ApplicationScope private val repositoryScope: CoroutineScope,
): RefillRepository {
    // Trigger for manual refreshes (e.g., pull-to-refresh)
    private val refreshTrigger = MutableSharedFlow<Unit>(replay = 1)
    private val _errorEvents = MutableSharedFlow<String>()
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
                        withContext(context = Dispatchers.IO) {
                            val reader = RefillStateReader(
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

    override suspend fun sendStartRequest() {
        executeRequest(apiUrl = WebApiUrls.REFILL_CONTROL_START)
    }

    override suspend fun sendStopRequest() {
        executeRequest(apiUrl = WebApiUrls.REFILL_CONTROL_STOP)
    }

    override suspend fun sendResetRequest() {
        executeRequest(apiUrl = WebApiUrls.REFILL_CONTROL_RESET)
    }

    private fun executeRequest(apiUrl: String) {
        val user = sessionManager.getControllerUser() ?: ""
        val password = sessionManager.getControllerPassword() ?: ""
        val controllerAddress: String? = sessionManager.getControllerAddress()
        if (controllerAddress.isNullOrBlank()) {
            return
        }
        val controllerPort = sessionManager.getControllerPort()
        val url = "http://${controllerAddress}:${controllerPort}${apiUrl}"
        val authHeader = Credentials.basic(user, password)

        repositoryScope.launch {
            val requestSuccess = apiService.resetRefillErrors(
                url, authHeader
            ).isSuccessful
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