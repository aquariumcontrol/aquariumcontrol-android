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

package com.laimburggasse.aquariumcontrol.common.data.repository

import androidx.paging.Pager
import androidx.paging.PagingData
import com.laimburggasse.aquariumcontrol.common.domain.HistoryRepository
import com.laimburggasse.aquariumcontrol.common.domain.SessionManager
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import me.tatarka.inject.annotations.Inject

/**
 * Core implementation of [HistoryRepository] providing generic paging functionality.
 * Bridges the Room database and the network API via a [Pager].
 *
 * @param T The type of the data items being paged.
 * @property sessionManager The global session manager for controller context.
 * @property pager The Paging library Pager instance.
 */
open class HistoryRepositoryImpl<T : Any> @Inject constructor(
    sessionManager: SessionManager,
    private val pager: Pager<Int, T>
) : HistoryRepository<T> {

        /** Internal trigger flow for manually refreshing the history. */
    private val refreshTrigger = MutableSharedFlow<Unit>(replay = 1)

    @OptIn(ExperimentalCoroutinesApi::class)
        /**
     * A reactive flow of [PagingData] for the history items.
     * Responds to controller selection changes and manual refresh triggers.
     */
    override val historyEventFlow: Flow<PagingData<T>> =
        combine(
            flow = sessionManager.selectedControllerProfile,
            flow2 = refreshTrigger.onStart { emit(value = Unit) },
        ) { controller, _ ->
            controller
        }
        .filterNotNull()
        .flatMapLatest { _ ->
            pager.flow
        }

    /**
     * Fetches a full list of history items for a specific date.
     * Currently, it returns an empty list as date-specific batch fetching is not implemented.
     *
     * @param date The date in epoch milliseconds.
     * @return A list of history items for the given date.
     */
    override suspend fun getHistory(date: Long): List<T> = emptyList()

    /**
     * Triggers a manual refresh of the paging flow.
     */
    override suspend fun triggerRefresh() {
        refreshTrigger.emit(value = Unit)
    }
}
