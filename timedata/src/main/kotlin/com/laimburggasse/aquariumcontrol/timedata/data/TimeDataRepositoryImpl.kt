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

package com.laimburggasse.aquariumcontrol.timedata.data

import com.laimburggasse.aquariumcontrol.common.domain.GlobalConstants
import com.laimburggasse.aquariumcontrol.common.domain.SessionManager
import com.laimburggasse.aquariumcontrol.common.di.ApplicationScope
import com.laimburggasse.aquariumcontrol.common.di.IoDispatcher
import com.laimburggasse.aquariumcontrol.timedata.data.remote.TimeDataReader
import com.laimburggasse.aquariumcontrol.timedata.data.remote.dto.TimeDataStringSample
import com.laimburggasse.aquariumcontrol.timedata.domain.model.TimeDataSample
import com.laimburggasse.aquariumcontrol.common.data.remote.DataFetchResult
import com.laimburggasse.aquariumcontrol.common.data.remote.WebApiUrls
import com.laimburggasse.aquariumcontrol.timedata.data.remote.TimeDataApiService
import com.laimburggasse.aquariumcontrol.timedata.domain.repository.TimeDataRepository
import kotlinx.coroutines.CoroutineDispatcher
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
import javax.inject.Inject

open class TimeDataRepositoryImpl @Inject constructor(
    private val sessionManager: SessionManager,
    private val apiService: TimeDataApiService,
    @param:ApplicationScope private val repositoryScope: CoroutineScope,
    @param:IoDispatcher private val context: CoroutineDispatcher
): TimeDataRepository {
    private val refreshTrigger = MutableSharedFlow<Unit>(replay = 1)

    @OptIn(ExperimentalCoroutinesApi::class)
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
                    withContext(context) {
                        val url: String =
                            "http://" + sessionManager.getControllerAddress() + ":" +
                            sessionManager.getControllerPort() + WebApiUrls.LOAD_TIME_DATA
                        val reader = TimeDataReader(
                            apiService, url, username = controller.user, password = controller.password
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
     * Public function to allow the ViewModel to trigger a manual refresh (e.g., for pull-to-refresh).
     */
    override suspend fun triggerRefresh() {
        refreshTrigger.emit(value = Unit)
    }
}