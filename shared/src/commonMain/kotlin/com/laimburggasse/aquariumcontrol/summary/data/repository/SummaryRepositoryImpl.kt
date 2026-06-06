/*
 *  Copyright 2026 Uwe Martin
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.laimburggasse.aquariumcontrol.summary.data.repository



import com.laimburggasse.aquariumcontrol.common.domain.SessionManager
import com.laimburggasse.aquariumcontrol.common.di.IoDispatcher
import com.laimburggasse.aquariumcontrol.summary.data.remote.SummaryDataReader
import com.laimburggasse.aquariumcontrol.summary.domain.model.SummaryData
import com.laimburggasse.aquariumcontrol.common.data.remote.DataFetchResult
import com.laimburggasse.aquariumcontrol.common.data.remote.WebApiUrls
import com.laimburggasse.aquariumcontrol.common.util.Logger
import com.laimburggasse.aquariumcontrol.summary.data.remote.SummaryDataApiService
import com.laimburggasse.aquariumcontrol.summary.domain.repository.SummaryRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.withContext
import kotlinx.coroutines.flow.onEach
import me.tatarka.inject.annotations.Inject

@OptIn(DelicateCoroutinesApi::class)

/**
 * Implementation of [SummaryRepository] for managing aquarium summary data.
 * Provides a reactive stream of the latest sensor and actuator states.
 *
 * @property logger Utility for recording exceptions.
 * @property sessionManager Manager providing the active controller and session state.
 * @property apiService Network service for fetching summary data.
 * @property repositoryScope Coroutine scope for reactive flow management.
 * @property context Dispatcher provider for IO operations.
 */
open class SummaryRepositoryImpl @Inject constructor(
    private val logger: Logger,
    private val sessionManager: SessionManager,
    private val apiService: SummaryDataApiService,
    repositoryScope: CoroutineScope,
    private val context: IoDispatcher
): SummaryRepository {
    /** Internal trigger used to manually refresh the summary data. */
    private val refreshTrigger = MutableSharedFlow<Unit>(replay = 1)

    /**
     * This is the main reactive data source for the UI.
     * It combines two triggers:
     *  1. `sessionManager.selectedControllerProfile`: Automatically fetches when the controller changes.
     *  2. `refreshTrigger`: Manually fetches when `fetchOverviewData()` is called.
     *
     * It uses `flatMapLatest` to ensure that whenever EITHER trigger emits,
     * it cancels any ongoing fetch and starts a new one.
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    override val summaryData: Flow<DataFetchResult<SummaryData>> =
        combine(
            flow = sessionManager.selectedControllerProfile,
            flow2 = refreshTrigger
        ) { controller, _ ->
            controller
        }
        .filterNotNull()
        .flatMapLatest { controller ->
            flow {
                // UI shall temporarily show loading state, while fetching data
                emit(value = DataFetchResult.Loading)

                val result: DataFetchResult<SummaryData> = withContext(context.dispatcher) {
                    val url = "http://${controller.address}:${controller.port}${WebApiUrls.OVERVIEW_GET_DATA}"
                    val reader = SummaryDataReader(
                        logger, apiService, url, username = controller.user, password = controller.password
                    )
                    reader.readSummaryData()
                }

                emit(value = result)
            }
        }
        .onEach { result ->
            if (result is DataFetchResult.Success) {
                sessionManager.updateServerTimestamp(result.data.timestamp)
            }
        }
        .stateIn(
            scope = repositoryScope,
            started = SharingStarted.Eagerly,
            initialValue = DataFetchResult.Loading
        )

    /**
     * This function acts as a manual trigger for the reactive `overviewData` flow.
     * It simply emits a new signal on the `refreshTrigger` flow, which causes the
     * `flatMapLatest` block above to re-execute.
     */
    override suspend fun fetchSummaryData() {
        refreshTrigger.emit(value = Unit)
    }
}