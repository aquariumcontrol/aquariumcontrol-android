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

package com.laimburggasse.aquariumcontrol.feed.data.repository

import androidx.paging.*

import com.laimburggasse.aquariumcontrol.common.data.repository.HistoryRepositoryImpl
import com.laimburggasse.aquariumcontrol.common.domain.SessionManager
import com.laimburggasse.aquariumcontrol.feed.domain.model.FeedHistoryLogEntry
import com.laimburggasse.aquariumcontrol.feed.domain.repository.FeedHistoryRepository
import kotlinx.coroutines.flow.Flow
import me.tatarka.inject.annotations.Inject

/**
 * Implementation of [FeedHistoryRepository] for fetching feed history logs.
 * Inherits generic paging logic from [HistoryRepositoryImpl].
 *
 * @param sessionManager The manager for active session state.
 * @param pager The Pager instance used for loading paged history data.
 */
open class FeedHistoryRepositoryImpl @Inject constructor(
    sessionManager: SessionManager,
    pager: Pager<Int, FeedHistoryLogEntry>
) : HistoryRepositoryImpl<FeedHistoryLogEntry>(sessionManager, pager), FeedHistoryRepository {

        /**
     * A flow of paged feed history log entries.
     */
    override val feedHistoryEventFlow: Flow<PagingData<FeedHistoryLogEntry>> = historyEventFlow

        /**
     * Fetches history entries for a specific date.
     * Currently, it returns an empty list as date-specific fetching is not implemented.
     *
     * @param date The date in epoch milliseconds.
     * @return A list of [FeedHistoryLogEntry] for the given date.
     */
    override suspend fun getHistory(date: Long): List<FeedHistoryLogEntry> = emptyList()
}