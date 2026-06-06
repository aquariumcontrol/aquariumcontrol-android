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

package com.laimburggasse.aquariumcontrol.timedata.data.repository



import com.laimburggasse.aquariumcontrol.common.domain.GlobalConstants
import com.laimburggasse.aquariumcontrol.common.domain.SessionManager
import com.laimburggasse.aquariumcontrol.common.di.IoDispatcher
import com.laimburggasse.aquariumcontrol.timedata.data.remote.TimeDataReader
import com.laimburggasse.aquariumcontrol.timedata.data.remote.dto.TimeDataStringSample
import com.laimburggasse.aquariumcontrol.timedata.domain.model.TimeDataSample
import com.laimburggasse.aquariumcontrol.common.data.remote.DataFetchResult
import com.laimburggasse.aquariumcontrol.common.data.remote.WebApiUrls
import com.laimburggasse.aquariumcontrol.common.util.Logger
import com.laimburggasse.aquariumcontrol.timedata.data.remote.TimeDataApiService
import com.laimburggasse.aquariumcontrol.timedata.domain.repository.TimeDataRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.withContext
import me.tatarka.inject.annotations.Inject


/**
 * Implementation of [TimeDataRepository] for managing aquarium telemetry data.
 * Provides a reactive stream of historical sensor and actuator samples.
 *
 * @property logger Utility for recording exceptions.
 * @property sessionManager Manager providing the active controller profile.
 * @property apiService Network service for telemetry retrieval.
 * @property repositoryScope Coroutine scope for reactive flow management.
 * @property context Dispatcher provider for IO operations.
 */
open class TimeDataRepositoryImpl @Inject constructor(
    private val logger: Logger,
    private val sessionManager: SessionManager,
    private val apiService: TimeDataApiService,
    private val repositoryScope: CoroutineScope,
    private val context: IoDispatcher
): TimeDataRepository {
    /** Internal trigger used to manually refresh the telemetry data. */
    private val refreshTrigger = MutableSharedFlow<Unit>(replay = 1)

    @OptIn(ExperimentalCoroutinesApi::class)
    /**
     * Reactive state flow emitting the list of telemetry samples.
     * Automatically re-fetches when the selected controller profile changes.
     */
    override val timeDataSamples: Flow<DataFetchResult<List<TimeDataSample>>> =
        combine(
            flow = sessionManager.selectedControllerProfile,
            flow2 = refreshTrigger.onStart { emit(value = Unit) }
        ) { controller, _ -> controller }
        .filterNotNull()
        .flatMapLatest { controller ->
            flow {
                // UI shall temporarily show loading state, while fetching data
                emit(value = DataFetchResult.Loading)

                // Perform the network request on a background thread.
                val readResult: DataFetchResult<List<TimeDataStringSample>> =
                    withContext(context.dispatcher) {
                        val url: String =
                            "http://" + sessionManager.getControllerAddress() + ":" +
                            sessionManager.getControllerPort() + WebApiUrls.LOAD_TIME_DATA
                        val reader = TimeDataReader(
                            logger, apiService, url, username = controller.user, password = controller.password
                        )
                        reader.readTimeDataSeries()
                    }
                val processedResult: DataFetchResult<List<TimeDataSample>> = when (readResult) {
                    is DataFetchResult.Success -> {
                        val timeDataStringList: List<TimeDataStringSample> = readResult.data
                        val timeDataSamples: List<TimeDataSample> =
                            if (timeDataStringList.isNotEmpty()) {
                                timeDataStringList.map { timeDataStringSample ->
                                    timeDataStringSample.transformToTimeDataSample()
                                }
                            } else {
                                emptyList()
                            }
                        DataFetchResult.Success(data = timeDataSamples)
                    }
                    is DataFetchResult.Error -> {
                        DataFetchResult.Error(
                            errorMessage = readResult.errorMessage,
                            exception = null
                        )
                    }
                    is DataFetchResult.Loading -> DataFetchResult.Error(
                        exception = null,
                        errorMessage = GlobalConstants.ERR_MSG_LOADING_TIME_DATA_FAILED
                    )
                }

                // Emit the final result (Success or Error) from the network call.
                emit(value = processedResult)
            }
        }
        .stateIn(
            scope = repositoryScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
            initialValue = DataFetchResult.Loading
        )

    /**
     * Manually triggers a re-fetch of the telemetry data from the server.
     */
    override suspend fun triggerRefresh() {
        refreshTrigger.emit(value = Unit)
    }
}