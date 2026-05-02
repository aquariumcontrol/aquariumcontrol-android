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

package com.laimburggasse.aquariumcontrol.refill.data.remote

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.laimburggasse.aquariumcontrol.refill.data.local.RefillHistoryDatabase
import com.laimburggasse.aquariumcontrol.refill.domain.model.RefillHistoryLogEntry
import com.laimburggasse.aquariumcontrol.common.data.remote.WebApiUrls
import com.laimburggasse.aquariumcontrol.common.domain.ControllerProfile
import com.laimburggasse.aquariumcontrol.common.domain.SessionManager
import okhttp3.Credentials
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalPagingApi::class)
class RefillHistoryRemoteMediator(
    private val refillHistoryDatabase: RefillHistoryDatabase,
    private val refillHistoryApiService: RefillHistoryApiService,
    private val sessionManager: SessionManager, // Injected to access server timestamp
): RemoteMediator<Int, RefillHistoryLogEntry>() {
    private var refillLogStart: Timestamp? = null
    private var serverUsed: ControllerProfile? = null
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
        state: PagingState<Int, RefillHistoryLogEntry>
    ): MediatorResult {
        // Logic to recognize that server has changed and log start needs to be recalculated
        if (serverUsed == null || serverUsed != sessionManager.selectedControllerProfile.value) {
            refillLogStart = null
        }
        return try {
            // load the timestamp of the earliest entry in the database
            if (refillLogStart == null) {
                val apiPath = WebApiUrls.REFILL_LOAD_FIRST_EVENT

                val user = sessionManager.getControllerUser()
                val password = sessionManager.getControllerPassword()
                val address = sessionManager.getControllerAddress()
                val port = sessionManager.getControllerPort()
                if (user != null && password != null && address != null && port != null) {
                    // Construct the URL ensuring no double slashes if apiPath already starts with /
                    val baseUrl = "http://$address:$port"
                    val dynamicUrl = if (apiPath.startsWith("/")) {
                        "$baseUrl$apiPath"
                    } else {
                        "$baseUrl/$apiPath"
                    }
                    val authHeader = Credentials.basic(user, password)
                    refillLogStart =
                        refillHistoryApiService.getRefillHistoryStart(
                            dynamicUrl,
                            authHeader
                        ).transformToTimestamp()
                    serverUsed = sessionManager.selectedControllerProfile.value
                }
            }

            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

            var currentLoadKey = when(loadType) {
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

            val user = sessionManager.getControllerUser()
            val password = sessionManager.getControllerPassword()
            val address = sessionManager.getControllerAddress()
            val port = sessionManager.getControllerPort()

            if (user != null && password != null && address != null && port != null) {
                val apiPath = WebApiUrls.REFILL_LOAD_EVENTS
                val baseUrl = "http://$address:$port"
                val dynamicUrl = if (apiPath.startsWith("/")) {
                    "$baseUrl$apiPath"
                } else {
                    "$baseUrl/$apiPath"
                }
                val authHeader = Credentials.basic(user, password)

                val allEntries = mutableListOf<RefillHistoryLogEntry>()
                var reachedAbsoluteStart: Boolean

                // Loop until we find at least one entry, or we reach the absolute start of the log
                while (true) {
                    val refillHistoryLogEntries =
                        refillHistoryApiService.getRefillHistory(
                            dynamicUrl,
                            authHeader,
                            date = currentLoadKey
                        )
                    allEntries.addAll(refillHistoryLogEntries)

                    reachedAbsoluteStart = if (refillLogStart != null) {
                        try {
                            val startOfLoadDay = Timestamp.valueOf("$currentLoadKey 00:00:00")
                            val reached = startOfLoadDay.before(refillLogStart) || startOfLoadDay == refillLogStart
                            reached
                        } catch (_: Exception) {
                            false
                        }
                    } else {
                        val reached = refillHistoryLogEntries.isEmpty()
                        reached
                    }

                    if (allEntries.isNotEmpty() || reachedAbsoluteStart) {
                        break
                    }

                    // No entries found for this day, try the previous day
                    val calendar = Calendar.getInstance()
                    calendar.time = dateFormat.parse(currentLoadKey) ?: Date()
                    calendar.add(Calendar.DAY_OF_YEAR, -1)
                    currentLoadKey = dateFormat.format(calendar.time)
                }

                refillHistoryDatabase.withTransaction {
                    if (loadType == LoadType.REFRESH) {
                        refillHistoryDatabase.refillHistoryDao().clearAll()
                    }
                    refillHistoryDatabase.refillHistoryDao().upsertAll(allEntries)
                }

                MediatorResult.Success(endOfPaginationReached = reachedAbsoluteStart)
            }
            else {
                MediatorResult.Success(endOfPaginationReached = true)
            }
        } catch (e: Exception) {
            MediatorResult.Error(e)
        }
    }
}