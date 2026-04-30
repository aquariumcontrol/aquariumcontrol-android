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

import androidx.paging.Pager
import com.laimburggasse.aquariumcontrol.balling.domain.model.BallingHistoryLogEntry
import com.laimburggasse.aquariumcontrol.balling.domain.repository.BallingHistoryRepository
import com.laimburggasse.aquariumcontrol.common.data.remote.DataFetchResult
import com.laimburggasse.aquariumcontrol.common.data.remote.HistoryLogApiService
import com.laimburggasse.aquariumcontrol.common.data.remote.HistoryLogEntryReader
import com.laimburggasse.aquariumcontrol.common.data.remote.WebApiUrls
import com.laimburggasse.aquariumcontrol.common.di.ApplicationScope
import com.laimburggasse.aquariumcontrol.common.di.BallingHistoryService
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
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import javax.inject.Inject

open class BallingHistoryRepositoryImpl<T> @Inject constructor(
    sessionManager: SessionManager,
    @param:BallingHistoryService private val apiService: HistoryLogApiService,
    private val json: Json,
    @param:ApplicationScope private val repositoryScope: CoroutineScope,
    pager: Pager<Int, BallingHistoryLogEntry>
): BallingHistoryRepository<T> {

    // Trigger for manual refreshes (e.g., pull-to-refresh)
    private val refreshTrigger = MutableSharedFlow<Unit>(replay = 1)

    private val showFullWeek = MutableStateFlow(value = false)

    /**
     * A reactive Flow that provides the Balling History events.
     * It combines three sources:
     *  - The selected controller from SessionManager.
     *  - The manual refresh trigger.
     *
     * It automatically re-fetches data when any of these inputs change.
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    override val ballingHistoryDosingEventFlow: Flow<DataFetchResult<List<T>>> =
        combine(
            flow = sessionManager.selectedControllerProfile,
            flow2 = refreshTrigger.onStart { emit(value = Unit) }, // Trigger on first collection
            flow3 = showFullWeek
        ) { controller, _, _ ->
            Pair(first = controller, second = Unit)
        }
        .filter { it.first != null } // Only proceed if a controller is selected
        .flatMapLatest { (controller: ControllerProfile?, _) ->
            flow {
                // UI shall temporarily show loading state, while fetching data
                emit(value = DataFetchResult.Loading)

                val url: String =
                    "http://${controller!!.address}:${controller.port}${WebApiUrls.BALLING_LOAD_EVENTS_7D}"

                val result: DataFetchResult<List<T>> =
                    withContext(context = Dispatchers.IO) {
                        val reader = HistoryLogEntryReader(
                            apiService,
                            url,
                            username = controller.user,
                            password = controller.password,
                            decode = { jsonString ->
                                // specific serializer for BallingHistoryLogEntry
                                @Suppress("UNCHECKED_CAST")
                                json.decodeFromString<List<BallingHistoryLogEntry>>(jsonString) as List<T>
                            }
                        )
                        reader.readHistoryLogEvents()
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