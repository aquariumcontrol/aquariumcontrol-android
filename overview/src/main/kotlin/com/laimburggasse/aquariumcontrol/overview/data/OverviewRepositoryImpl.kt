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

package com.laimburggasse.aquariumcontrol.overview.data

import com.laimburggasse.aquariumcontrol.common.domain.SessionManager
import com.laimburggasse.aquariumcontrol.common.di.ApplicationScope
import com.laimburggasse.aquariumcontrol.common.di.IoDispatcher
import com.laimburggasse.aquariumcontrol.overview.data.remote.OverviewDataReader
import com.laimburggasse.aquariumcontrol.overview.domain.model.OverviewData
import com.laimburggasse.aquariumcontrol.common.data.remote.DataFetchResult
import com.laimburggasse.aquariumcontrol.common.data.remote.WebApiUrls
import com.laimburggasse.aquariumcontrol.overview.data.remote.OverviewDataApiService
import com.laimburggasse.aquariumcontrol.overview.domain.repository.OverviewRepository
import kotlinx.coroutines.CoroutineDispatcher
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
import javax.inject.Inject

@OptIn(DelicateCoroutinesApi::class)
open class OverviewRepositoryImpl @Inject constructor(
    sessionManager: SessionManager,
    private val apiService: OverviewDataApiService,
    @param:ApplicationScope private val repositoryScope: CoroutineScope,
    @param:IoDispatcher private val context: CoroutineDispatcher
): OverviewRepository {
    // This SharedFlow doesn't hold state; it just emits a signal when we call emit().
    // We use replay=1 so it emits the last signal to new subscribers if needed.
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
    override val overviewData: Flow<DataFetchResult<OverviewData>> =
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

                val result: DataFetchResult<OverviewData> = withContext(context) {
                    val url = "http://${controller.address}:${controller.port}${WebApiUrls.OVERVIEW_GET_DATA}"
                    val reader = OverviewDataReader(
                        apiService, url, username = controller.user, password = controller.password
                    )
                    reader.readOverviewData()
                }

                emit(value = result)
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
    override suspend fun fetchOverviewData() {
        refreshTrigger.emit(value = Unit)
    }
}