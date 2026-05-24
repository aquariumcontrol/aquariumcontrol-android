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



import com.laimburggasse.aquariumcontrol.common.domain.GlobalConstants
import com.laimburggasse.aquariumcontrol.common.domain.SessionManager
import com.laimburggasse.aquariumcontrol.common.domain.ControllerProfile
import com.laimburggasse.aquariumcontrol.common.di.ApplicationScope
import com.laimburggasse.aquariumcontrol.common.di.IoDispatcher
import com.laimburggasse.aquariumcontrol.heating.domain.model.HeatingStatsEntry
import com.laimburggasse.aquariumcontrol.heating.data.remote.HeatingStatsReader
import com.laimburggasse.aquariumcontrol.heating.data.remote.dto.HeatingStatsStringEntry
import com.laimburggasse.aquariumcontrol.heating.domain.repository.HeatingStatsRepository
import com.laimburggasse.aquariumcontrol.common.data.remote.DataFetchResult
import com.laimburggasse.aquariumcontrol.common.data.remote.WebApiUrls
import com.laimburggasse.aquariumcontrol.heating.data.remote.HeatingStatsApiService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.withContext
import me.tatarka.inject.annotations.Inject


@ApplicationScope

open class HeatingStatsRepositoryImpl @Inject constructor(
    sessionManager: SessionManager,
    private val apiService: HeatingStatsApiService,
    private val repositoryScope: CoroutineScope,
    private val ioDispatcher: IoDispatcher
): HeatingStatsRepository {
    // for triggering manual refreshes
    private val refreshTrigger = MutableSharedFlow<Unit>(replay = 1)

    @OptIn(ExperimentalCoroutinesApi::class)
    override val heatingStatsFlow: StateFlow<DataFetchResult<List<HeatingStatsEntry>>> = combine(
        flow = sessionManager.selectedControllerProfile,
        flow2 = refreshTrigger.onStart { emit(value = Unit) } // Triggers the first fetch
    ) { controller, _ ->
        controller
    }
    .filterNotNull()
    .flatMapLatest { controller: ControllerProfile ->
        flow {
            // UI shall temporarily show loading state, while fetching data
            emit(value = DataFetchResult.Loading)

            val result: DataFetchResult<List<HeatingStatsEntry>> =
                withContext(context = ioDispatcher.dispatcher) {
                    val url = "http://${controller.address}:${controller.port}${WebApiUrls.HEATING_LOAD_STATS}"
                    val heatingStatsReader = HeatingStatsReader(
                        apiService, url, username = controller.user, password = controller.password
                    )
                    val stringResult: DataFetchResult<List<HeatingStatsStringEntry>> =
                        heatingStatsReader.readStatsEntry()

                    // Transform the result before returning it from the background thread
                    when (stringResult) {
                        is DataFetchResult.Success -> {
                            val heatingStats: List<HeatingStatsEntry> = stringResult.data.map {
                                it.transformToHeatingStatsEntry()
                            }
                            DataFetchResult.Success(data = heatingStats)
                        }
                        is DataFetchResult.Error -> {
                            // Propagate the error result
                            DataFetchResult.Error(
                                exception = stringResult.exception,
                                errorMessage = stringResult.errorMessage
                            )
                        }
                        is DataFetchResult.Loading -> DataFetchResult.Error(
                            exception = null,
                            errorMessage = GlobalConstants.ERR_MSG_LOADING_HEATING_STATS_FAILED
                        )
                    }
                }
            emit(value = result)
        }
    }.stateIn(
        scope = repositoryScope,
        started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000L),
        initialValue = DataFetchResult.Loading
    )

    /**
     * Triggers a manual refresh of the heating stats.
     */
    override suspend fun triggerRefresh() {
        refreshTrigger.emit(value = Unit)
    }
}