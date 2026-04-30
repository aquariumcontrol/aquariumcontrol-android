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

@OptIn(ExperimentalPagingApi::class)
class BallingHistoryRemoteMediator(
    private val ballingHistoryDatabase: BallingHistoryDatabase,
    private val ballingHistoryApiService: BallingHistoryApiService,
    private val url: String,
): RemoteMediator<String, BallingHistoryLogEntry>() {
    override suspend fun load(
        loadType: LoadType,
        state: PagingState<String, BallingHistoryLogEntry>
    ): MediatorResult {
        return try {
            val loadKey = when(loadType) {
                LoadType.REFRESH -> "2026-04-29" // @todo: make dynamic
                LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
                LoadType.APPEND -> {
                    val lastItem = state.lastItemOrNull()
                    if (lastItem == null) {
                        "2026-04-29" // @todo: make dynamic
                    }
                    else {
                        (lastItem.timestamp) // @todo: add logic for calculating next page
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
            MediatorResult.Success(endOfPaginationReached = ballingHistoryLogEntries.isEmpty())
        } catch (e: IOException) {
            MediatorResult.Error(e)
        } catch (e: HttpException) {
            MediatorResult.Error(e)
        }
    }
}