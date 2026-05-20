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

package com.laimburggasse.aquariumcontrol.common.data.remote

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.RoomDatabase
import androidx.room.withTransaction
import com.laimburggasse.aquariumcontrol.common.data.local.BaseHistoryDao
import com.laimburggasse.aquariumcontrol.common.domain.BaseHistoryLogEntry
import com.laimburggasse.aquariumcontrol.common.domain.ControllerProfile
import com.laimburggasse.aquariumcontrol.common.domain.SessionManager
import io.ktor.util.encodeBase64
import io.ktor.utils.io.core.toByteArray
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalPagingApi::class)
class HistoryRemoteMediator<T : BaseHistoryLogEntry>(
    private val database: RoomDatabase,
    private val dao: BaseHistoryDao<T>,
    private val apiService: BaseHistoryApiService,
    private val sessionManager: SessionManager,
    private val loadEventsUrlPath: String,
    private val loadFirstEventUrlPath: String
) : RemoteMediator<Int, T>() {
    private var historyLogStart: Timestamp? = null
    private var serverUsed: ControllerProfile? = null

    private fun getCurrentDate(): String {
        val serverTime = sessionManager.serverTimestamp.value
        return if (serverTime != null) {
            serverTime.substringBefore(' ')
        } else {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            dateFormat.format(Date())
        }
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, T>
    ): MediatorResult {
        if (serverUsed == null || serverUsed != sessionManager.selectedControllerProfile.value) {
            historyLogStart = null
        }

        return try {
            val user = sessionManager.getControllerUser()
            val password = sessionManager.getControllerPassword()
            val address = sessionManager.getControllerAddress()
            val port = sessionManager.getControllerPort()

            if (user == null || password == null || address == null || port == null) {
                return MediatorResult.Success(endOfPaginationReached = true)
            }

            val authHeader = "Basic " + "$user:$password".toByteArray(Charsets.ISO_8859_1).encodeBase64()
            val baseUrl = "http://$address:$port"

            if (historyLogStart == null) {
                val dynamicUrl = if (loadFirstEventUrlPath.startsWith("/")) {
                    "$baseUrl$loadFirstEventUrlPath"
                } else {
                    "$baseUrl/$loadFirstEventUrlPath"
                }
                historyLogStart = apiService.getHistoryStart(dynamicUrl, authHeader).transformToTimestamp()
                serverUsed = sessionManager.selectedControllerProfile.value
            }

            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

            var currentLoadKey = when (loadType) {
                LoadType.REFRESH -> getCurrentDate()
                LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
                LoadType.APPEND -> {
                    val lastItem = state.lastItemOrNull()
                    if (lastItem == null) {
                        getCurrentDate()
                    } else {
                        val lastDateStr = lastItem.timestamp.substringBefore(' ')
                        val calendar = Calendar.getInstance()
                        calendar.time = dateFormat.parse(lastDateStr) ?: Date()
                        calendar.add(Calendar.DAY_OF_YEAR, -1)
                        dateFormat.format(calendar.time)
                    }
                }
            }

            val dynamicUrl = if (loadEventsUrlPath.startsWith("/")) {
                "$baseUrl$loadEventsUrlPath"
            } else {
                "$baseUrl/$loadEventsUrlPath"
            }

            val allEntries = mutableListOf<T>()
            var reachedAbsoluteStart = false

            while (allEntries.size < state.config.pageSize) {
                val entries = apiService.getHistory(dynamicUrl, authHeader, currentLoadKey)
                @Suppress("UNCHECKED_CAST")
                allEntries.addAll(entries as List<T>)

                reachedAbsoluteStart = if (historyLogStart != null) {
                    try {
                        val startOfLoadDay = Timestamp.valueOf("$currentLoadKey 00:00:00")
                        startOfLoadDay.before(historyLogStart) || startOfLoadDay == historyLogStart
                    } catch (_: Exception) {
                        false
                    }
                } else {
                    entries.isEmpty()
                }

                if (reachedAbsoluteStart) {
                    break
                }

                val calendar = Calendar.getInstance()
                calendar.time = dateFormat.parse(currentLoadKey) ?: Date()
                calendar.add(Calendar.DAY_OF_YEAR, -1)
                currentLoadKey = dateFormat.format(calendar.time)
            }

            database.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    dao.clearAll()
                }
                dao.upsertAll(allEntries)
            }

            MediatorResult.Success(endOfPaginationReached = reachedAbsoluteStart)
        } catch (e: Exception) {
            MediatorResult.Error(e)
        }
    }
}
