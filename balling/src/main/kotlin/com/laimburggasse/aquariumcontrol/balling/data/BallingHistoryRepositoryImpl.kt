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

package com.laimburggasse.aquariumcontrol.balling.data

import com.laimburggasse.aquariumcontrol.balling.data.remote.BallingHistoryLogEntryReader
import com.laimburggasse.aquariumcontrol.balling.domain.model.BallingHistoryLogEntry
import com.laimburggasse.aquariumcontrol.balling.domain.repository.BallingHistoryRepository
import com.laimburggasse.aquariumcontrol.common.data.remote.DataFetchResult
import com.laimburggasse.aquariumcontrol.common.data.remote.WebApiUrls
import com.laimburggasse.aquariumcontrol.common.di.ApplicationScope
import com.laimburggasse.aquariumcontrol.common.domain.ControllerProfile
import com.laimburggasse.aquariumcontrol.common.domain.SessionManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.withContext
import javax.inject.Inject

open class BallingHistoryRepositoryImpl @Inject constructor(
    sessionManager: SessionManager,
    @param:ApplicationScope private val repositoryScope: CoroutineScope
): BallingHistoryRepository {

    // Trigger for manual refreshes (e.g., pull-to-refresh)
    private val refreshTrigger = MutableSharedFlow<Unit>(replay = 1)

    // State flow to hold the current time range choice (24h vs 7d)
    private val showFullWeek = MutableStateFlow(value = false)

    /**
     * A reactive Flow that provides the Balling History events.
     * It combines three sources:
     *  - The selected controller from SessionManager.
     *  - The manual refresh trigger.
     *  - The selected time range (24h or 7d).
     *
     * It automatically re-fetches data when any of these inputs change.
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    override val ballingHistoryDosingEventFlow: Flow<DataFetchResult<List<BallingHistoryLogEntry>>> =
        combine(
            flow = sessionManager.selectedControllerProfile,
            flow2 = refreshTrigger.onStart { emit(value = Unit) }, // Trigger on first collection
            flow3 = showFullWeek
        ) { controller, _, isFullWeek ->
            Triple(first = controller, second = Unit, third = isFullWeek)
        }
        .filter { it.first != null } // Only proceed if a controller is selected
        .flatMapLatest { (controller: ControllerProfile?, _, isFullWeek: Boolean) ->
            flow {
                // UI shall temporarily show loading state, while fetching data
                emit(value = DataFetchResult.Loading)

                val url: String = if (isFullWeek) {
                    "http://${controller!!.address}:${controller.port}${WebApiUrls.BALLING_LOAD_EVENTS_7D}"
                } else {
                    "http://${controller!!.address}:${controller.port}${WebApiUrls.BALLING_LOAD_EVENTS_24H}"
                }

                val result: DataFetchResult<List<BallingHistoryLogEntry>> =
                    withContext(context = Dispatchers.IO) {
                        val reader = BallingHistoryLogEntryReader(url)
                        reader.readBallingEvents()
                    }

                emit(value = result)
            }
        }
        .stateIn(
            scope = repositoryScope,
            started = SharingStarted.Companion.WhileSubscribed(stopTimeoutMillis = 5000),
            initialValue = DataFetchResult.Loading
        )

    /**
     * Public function to tell the repository to fetch a different time range.
     * Setting the value will automatically trigger the flow to re-fetch the data.
     */
    override suspend fun setHistoryTimeRange(fetchFullWeek: Boolean) {
        // Only emit if the value has changed to avoid unnecessary fetches.
        if (showFullWeek.value != fetchFullWeek) {
            showFullWeek.emit(value = fetchFullWeek)
        }
    }

    /**
     * Public function for the ViewModel to trigger a manual refresh.
     */
    override suspend fun triggerRefresh() {
        refreshTrigger.emit(value = Unit)
    }
}