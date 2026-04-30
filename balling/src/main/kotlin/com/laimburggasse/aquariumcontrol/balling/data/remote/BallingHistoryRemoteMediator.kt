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

package com.laimburggasse.aquariumcontrol.balling.data.remote

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.laimburggasse.aquariumcontrol.balling.data.local.BallingHistoryDatabase
import com.laimburggasse.aquariumcontrol.balling.domain.model.BallingHistoryLogEntry
import okhttp3.Credentials
import okio.IOException
import retrofit2.HttpException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalPagingApi::class)
class BallingHistoryRemoteMediator(
    private val ballingHistoryDatabase: BallingHistoryDatabase,
    private val ballingHistoryApiService: BallingHistoryApiService,
    private var url: String, // Made var to allow updates
): RemoteMediator<Int, BallingHistoryLogEntry>() {

    fun updateUrl(newUrl: String) {
        this.url = newUrl
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, BallingHistoryLogEntry>
    ): MediatorResult {
        return try {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            
            val loadKey = when(loadType) {
                LoadType.REFRESH -> dateFormat.format(Date())
                LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
                LoadType.APPEND -> {
                    val lastItem = state.lastItemOrNull()
                    if (lastItem == null) {
                        dateFormat.format(Date())
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
            val authHeaderValue: String = Credentials.basic("username", "password")

            val ballingHistoryLogEntries = ballingHistoryApiService.getBallingHistory(
                url,
                authHeader = authHeaderValue,
                date = loadKey
            )

            ballingHistoryDatabase.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    ballingHistoryDatabase.ballingHistoryDao().clearAll()
                }
                ballingHistoryDatabase.ballingHistoryDao().upsertAll(ballingHistoryLogEntries)
            }
            // We consider it the end of pagination if we get an empty list for a specific date
            // Note: This might need adjustment if some days have no logs but earlier days do.
            MediatorResult.Success(endOfPaginationReached = ballingHistoryLogEntries.isEmpty())
        } catch (e: Exception) {
            MediatorResult.Error(e)
        }
    }
}