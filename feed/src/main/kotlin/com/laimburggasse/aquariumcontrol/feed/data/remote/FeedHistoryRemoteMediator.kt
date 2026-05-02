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

package com.laimburggasse.aquariumcontrol.feed.data.remote

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.laimburggasse.aquariumcontrol.feed.data.local.FeedHistoryDatabase
import com.laimburggasse.aquariumcontrol.feed.domain.model.FeedHistoryLogEntry
import com.laimburggasse.aquariumcontrol.common.domain.SessionManager
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalPagingApi::class)
class FeedHistoryRemoteMediator(
    private val feedHistoryDatabase: FeedHistoryDatabase,
    private val feedHistoryApiService: FeedHistoryApiService,
    private val sessionManager: SessionManager, // Injected to access server timestamp
    private var url: String, // Made var to allow updates
): RemoteMediator<Int, FeedHistoryLogEntry>() {

    private var authHeaderValue: String = ""

    fun updateConfig(newUrl: String, newAuthHeader: String) {
        this.url = newUrl
        this.authHeaderValue = newAuthHeader
    }

    private fun getCurrentDate(): String {
        val serverTime = sessionManager.serverTimestamp.value
        return if (serverTime != null) {
            // Server timestamp format is "yyyy-MM-dd HH:mm:ss"
            serverTime.substringBefore(' ')
        } else {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            dateFormat.format(Date())
        }
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, FeedHistoryLogEntry>
    ): MediatorResult {
        return try {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            
            val loadKey = when(loadType) {
                LoadType.REFRESH -> getCurrentDate()
                LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
                LoadType.APPEND -> {
                    val lastItem = state.lastItemOrNull()
                    if (lastItem == null) {
                        getCurrentDate()
                    }
                    else {
                        // Calculate the next date to fetch (previous day)
                        val lastDateStr = lastItem.timestamp.substringBefore(' ')
                        val calendar = Calendar.getInstance()
                        calendar.time = dateFormat.parse(lastDateStr) ?: Date()
                        calendar.add(Calendar.DAY_OF_YEAR, -1)
                        dateFormat.format(calendar.time)
                    }
                }
            }

            val feedHistoryLogEntries = feedHistoryApiService.getFeedHistory(
                url,
                authHeader = authHeaderValue,
                date = loadKey
            )

            feedHistoryDatabase.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    feedHistoryDatabase.feedHistoryDao().clearAll()
                }
                feedHistoryDatabase.feedHistoryDao().upsertAll(feedHistoryLogEntries)
            }
            // @todo: Extend end of pagination - currently ending with first empty entry
            MediatorResult.Success(endOfPaginationReached = feedHistoryLogEntries.isEmpty())
        } catch (e: Exception) {
            MediatorResult.Error(e)
        }
    }
}