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
import androidx.room.useWriterConnection
import com.laimburggasse.aquariumcontrol.common.data.local.BaseHistoryDao
import com.laimburggasse.aquariumcontrol.common.domain.BaseHistoryLogEntry
import com.laimburggasse.aquariumcontrol.common.domain.ControllerProfile
import com.laimburggasse.aquariumcontrol.common.domain.SessionManager
import com.laimburggasse.aquariumcontrol.common.util.Base64
import kotlinx.datetime.*
import kotlin.time.Clock

/**
 * A Paging Library [RemoteMediator] that orchestrates the loading of history data from
 * the network into a local Room database. It handles incremental loading of historical
 * events by day.
 *
 * @param T The type of the history log entry, extending [BaseHistoryLogEntry].
 * @property database The Room database instance.
 * @property dao The Data Access Object for history logs.
 * @property apiService The network service for fetching history.
 * @property sessionManager The manager for active session state.
 * @property loadEventsUrlPath The endpoint path for loading history events.
 * @property loadFirstEventUrlPath The endpoint path for identifying the start of the history log.
 */
@OptIn(ExperimentalPagingApi::class)
class HistoryRemoteMediator<T : BaseHistoryLogEntry>(
    private val database: RoomDatabase,
    private val dao: BaseHistoryDao<T>,
    private val apiService: BaseHistoryApiService,
    private val sessionManager: SessionManager,
    private val loadEventsUrlPath: String,
    private val loadFirstEventUrlPath: String
) : RemoteMediator<Int, T>() {
    /** Cached timestamp of the first ever log entry on the server. */
    private var historyLogStart: Long? = null

    /** The controller profile used for the current loading session. */
    private var serverUsed: ControllerProfile? = null

    /** Constant for milliseconds in a single day. */
    private val msInDay = 86_400_000L // milliseconds in one day

    /**
     * Calculates the current date normalized to the start of the day.
     * Uses server time if available, otherwise falls back to system local time.
     *
     * @return The normalized start-of-day timestamp in epoch milliseconds.
     */
    private fun getCurrentDate(): Long {
        val serverTime = sessionManager.serverTimestamp.value

        return if (serverTime != null) {
            serverTime - serverTime % msInDay
        } else {
            // use local time as backup
            val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
            today.atStartOfDayIn(TimeZone.currentSystemDefault()).toEpochMilliseconds()
        }
    }

    /**
     * Subtracts 24 hours from the given timestamp.
     *
     * @param current The reference timestamp.
     * @return The timestamp exactly one day prior.
     */
    private fun getYesterdaysDate(current: Long): Long {
        return current - msInDay
    }

    /**
     * Main entry point for the Paging library to request more data.
     * Fetches historical events from the API and persists them to the local database.
     *
     * @param loadType The type of load (REFRESH, APPEND, PREPEND).
     * @param state The current paging state.
     * @return A [MediatorResult] indicating success or error.
     */
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

            val authHeader = "Basic " + Base64.encode("$user:$password".encodeToByteArray())
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

            var currentLoadKey: Long = when (loadType) {
                LoadType.REFRESH -> getCurrentDate()
                LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
                LoadType.APPEND -> {
                    val lastItem = state.lastItemOrNull()
                    if (lastItem == null) {
                        getCurrentDate()
                    } else {
                        getYesterdaysDate(lastItem.timestamp)
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
                    currentLoadKey <= (historyLogStart!!)
                } else {
                    entries.isEmpty()
                }

                if (reachedAbsoluteStart) {
                    break
                }

                currentLoadKey -= msInDay
            }

            database.useWriterConnection {
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
