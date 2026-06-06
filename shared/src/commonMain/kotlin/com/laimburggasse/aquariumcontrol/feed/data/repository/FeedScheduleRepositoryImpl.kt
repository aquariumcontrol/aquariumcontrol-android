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

import com.laimburggasse.aquariumcontrol.common.data.remote.DataFetchResult
import com.laimburggasse.aquariumcontrol.common.data.remote.WebApiUrls
import com.laimburggasse.aquariumcontrol.common.di.ApplicationScope
import com.laimburggasse.aquariumcontrol.common.di.IoDispatcher
import com.laimburggasse.aquariumcontrol.common.domain.ControllerProfile
import com.laimburggasse.aquariumcontrol.common.domain.GlobalConstants
import com.laimburggasse.aquariumcontrol.common.domain.SessionManager
import com.laimburggasse.aquariumcontrol.common.util.Base64
import com.laimburggasse.aquariumcontrol.common.util.DateTimeUtil
import com.laimburggasse.aquariumcontrol.common.util.Logger
import com.laimburggasse.aquariumcontrol.feed.data.remote.FeedScheduleEntryApiService
import com.laimburggasse.aquariumcontrol.feed.data.remote.FeedScheduleEntryReader
import com.laimburggasse.aquariumcontrol.feed.data.remote.toFeedScheduleEntryList
import com.laimburggasse.aquariumcontrol.feed.domain.model.FeedScheduleEntry
import com.laimburggasse.aquariumcontrol.feed.domain.model.FeedScheduleEntryKey
import com.laimburggasse.aquariumcontrol.feed.domain.repository.FeedScheduleRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.withContext
import me.tatarka.inject.annotations.Inject

/**
 * Implementation of [FeedScheduleRepository] for managing scheduled feeding events.
 * Handles fetching, optimistic adding/updating/deleting of schedule entries.
 *
 * @property logger A utility for recording exceptions.
 * @property sessionManager The session manager providing the currently selected controller.
 * @property apiService The network service for feed schedule entries.
 * @property repositoryScope The coroutine scope tied to the repository\'s lifecycle.
 * @property ioDispatcher The dispatcher for blocking IO operations.
 */
@ApplicationScope
open class FeedScheduleRepositoryImpl @Inject constructor(
    private val logger: Logger,
    private val sessionManager: SessionManager,
    private val apiService: FeedScheduleEntryApiService,
    private val repositoryScope: CoroutineScope,
    private val ioDispatcher: IoDispatcher
): FeedScheduleRepository {
    private val refreshTrigger = MutableSharedFlow<Unit>(replay = 1)
    private val pendingDeletions = MutableStateFlow<Set<FeedScheduleEntryKey>>(value = emptySet())
    private val pendingUpdates = MutableStateFlow<Map<FeedScheduleEntryKey, FeedScheduleEntry>>(value = emptyMap())
    private val failureResult = MutableStateFlow<DataFetchResult.Error?>(value = null)

    @OptIn(ExperimentalCoroutinesApi::class)
    private val networkFlow: StateFlow<DataFetchResult<List<FeedScheduleEntry>>> = combine(
        flow = sessionManager.selectedControllerProfile,
        flow2 = refreshTrigger.onStart { emit(value = Unit) },
        flow3 = failureResult
    ) { controller, _, failure ->
        Pair(first = controller, second = failure)
    }
    .filter { it.first != null }
    .flatMapLatest { (controller: ControllerProfile?, failure: DataFetchResult.Error?) ->
        flow {
            if (failure != null) {
                emit(value = failure)
                return@flow
            }
            if (controller == null) {
                emit(value = DataFetchResult.Error(exception = null, errorMessage = GlobalConstants.ERR_MSG_NO_CONTROLLER_SELECTED))
                return@flow
            }
            emit(value = DataFetchResult.Loading)

            val networkResult: DataFetchResult<List<FeedScheduleEntry>> = withContext(ioDispatcher.dispatcher) {
                val url = "http://" + controller.address + ":" + controller.port + WebApiUrls.FEED_LOAD_SCHEDULE
                val scheduleReader = FeedScheduleEntryReader(
                    logger, apiService, url, username = controller.user, password = controller.password
                )
                when (val importResult = scheduleReader.readFeedScheduleEntries()) {
                    is DataFetchResult.Success -> DataFetchResult.Success(data = importResult.data.toFeedScheduleEntryList())
                    is DataFetchResult.Error -> importResult
                    is DataFetchResult.Loading -> DataFetchResult.Error(exception = null, errorMessage = GlobalConstants.ERR_MSG_LOADING_FEED_SCHEDULE_ENTRIES_FAILED)
                }
            }
            if (networkResult is DataFetchResult.Success) {
                pendingDeletions.value = emptySet()
                pendingUpdates.value = emptyMap()
            }
            emit(value = networkResult)
        }
    }.stateIn(scope = repositoryScope, started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000L), initialValue = DataFetchResult.Loading)

        /**
     * The reactive state flow emitting the current fetch state and list of [FeedScheduleEntry] items.
     * Combines network results with optimistic additions, updates, and deletions.
     */
    override val scheduleFlow: StateFlow<DataFetchResult<List<FeedScheduleEntry>>> = combine(
        flow = networkFlow,
        flow2 = pendingDeletions,
        flow3 = pendingUpdates
    ) { networkResult, deletions, updates ->
        when (networkResult) {
            is DataFetchResult.Success -> {
                var profiles = networkResult.data
                if (deletions.isNotEmpty()) {
                    profiles = profiles.filterNot { entry ->
                        deletions.contains(FeedScheduleEntryKey(profileId = entry.profileId, timestamp = entry.timestamp))
                    }
                }
                if (updates.isNotEmpty()) {
                    profiles = profiles.map { entry ->
                        val key = FeedScheduleEntryKey(profileId = entry.profileId, timestamp = entry.timestamp)
                        updates[key] ?: entry
                    }
                }
                DataFetchResult.Success(data = profiles)
            }
            else -> networkResult
        }
    }.stateIn(scope = repositoryScope, started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000L), initialValue = DataFetchResult.Loading)

    private fun formatTimestamp(millis: Long): String = DateTimeUtil.formatToApiTimestamp(millis)

        /**
     * Sends a request to delete a scheduled feed entry from the server.
     *
     * @param feedScheduleEntry The entry to delete.
     */
    override suspend fun deleteFeedScheduleEntry(feedScheduleEntry: FeedScheduleEntry) {
        val keyToDelete = FeedScheduleEntryKey(profileId = feedScheduleEntry.profileId, timestamp = feedScheduleEntry.timestamp)
        if (pendingDeletions.value.contains(keyToDelete)) return
        pendingDeletions.value += keyToDelete

        val user = sessionManager.getControllerUser() ?: ""
        val password = sessionManager.getControllerPassword() ?: ""
        val port = sessionManager.getControllerPort()
        val controllerAddress = sessionManager.getControllerAddress()
        if (controllerAddress.isNullOrBlank()) {
            failureResult.value = DataFetchResult.Error(exception = null, errorMessage = GlobalConstants.ERR_MSG_INVALID_CONTROLLER_ADDRESS)
            return
        }
        val url = "http://" + controllerAddress + ":" + port + WebApiUrls.FEED_SCHEDULE_DELETE_ENTRY
        val authHeader = "Basic " + Base64.encode(("$user:$password").encodeToByteArray())
        val postParams = feedScheduleEntry.getApiParameters(formatTimestamp(feedScheduleEntry.timestamp))

        val requestSuccess = withContext(ioDispatcher.dispatcher) {
            try {
                apiService.deleteFeedScheduleEntry(url, authHeader, postParams)
            } catch (e: Exception) {
                logger.recordException(e)
                false
            }
        }
        if (requestSuccess) {
            delay(timeMillis = 3000L)
            triggerRefresh()
        } else {
            pendingDeletions.value -= keyToDelete
            failureResult.value = DataFetchResult.Error(exception = null, errorMessage = GlobalConstants.ERR_MSG_DELETING_FEED_SCHEDULE_ENTRY_FAILED)
        }
    }

        /**
     * Sends a request to create a new scheduled feed entry on the server.
     *
     * @param feedProfileId The ID of the profile to schedule.
     * @param feedProfileName The name of the profile.
     * @param repeatDaily Whether the schedule should repeat every day.
     * @param timestampMillis The scheduled execution time in epoch milliseconds.
     */
    override suspend fun addFeedScheduleEntry(
        feedProfileId: Int,
        feedProfileName: String,
        repeatDaily: Boolean,
        timestampMillis: Long
    ) {
        val keyToFind = FeedScheduleEntryKey(feedProfileId, timestampMillis)
        if (pendingDeletions.value.contains(keyToFind)) {
            pendingDeletions.value -= keyToFind
        }

        val tempEntry = FeedScheduleEntry(timestamp = timestampMillis, profileId = feedProfileId, profileName = feedProfileName, isDaily = repeatDaily)
        val user = sessionManager.getControllerUser() ?: ""
        val password = sessionManager.getControllerPassword() ?: ""
        val port = sessionManager.getControllerPort()
        val controllerAddress = sessionManager.getControllerAddress()
        if (controllerAddress.isNullOrBlank()) {
            failureResult.value = DataFetchResult.Error(exception = null, errorMessage = GlobalConstants.ERR_MSG_INVALID_CONTROLLER_ADDRESS)
            return
        }
        val url = "http://" + controllerAddress + ":" + port + WebApiUrls.FEED_PROFILE_SCHEDULE_NEW
        val authHeader = "Basic " + Base64.encode(("$user:$password").encodeToByteArray())
        val apiParams = tempEntry.getApiParameters(formatTimestamp(timestampMillis))

        val requestSuccess = withContext(ioDispatcher.dispatcher) {
            try {
                apiService.addFeedScheduleEntry(url, authHeader, apiParams)
            } catch (e: Exception) {
                logger.recordException(e)
                false
            }
        }
        if (requestSuccess) {
            delay(timeMillis = 3000L)
            triggerRefresh()
        } else {
            failureResult.value = DataFetchResult.Error(exception = null, errorMessage = GlobalConstants.ERR_MSG_ADDING_FEED_SCHEDULE_ENTRY_FAILED)
        }
    }

        /**
     * Sends a request to modify an existing scheduled feed entry on the server.
     *
     * @param feedProfileId The new profile ID to assign.
     * @param feedProfileName The new profile name.
     * @param repeatDaily The new daily repetition flag.
     * @param timestampMillis The new scheduled execution time.
     * @param millisSinceEpoch The original timestamp of the entry being updated (used for server-side identification).
     */
    override suspend fun updateFeedScheduleEntry(
        feedProfileId: Int,
        feedProfileName: String,
        repeatDaily: Boolean,
        timestampMillis: Long,
        millisSinceEpoch: Long
    ) {
        val originalKey = FeedScheduleEntryKey(feedProfileId, timestamp = millisSinceEpoch)
        val modifiedEntry = FeedScheduleEntry(timestamp = timestampMillis, profileId = feedProfileId, profileName = feedProfileName, isDaily = repeatDaily)
        pendingUpdates.value += (originalKey to modifiedEntry.copy(profileName = " (Saving...)"))

        val user = sessionManager.getControllerUser() ?: ""
        val password = sessionManager.getControllerPassword() ?: ""
        val port = sessionManager.getControllerPort()
        val controllerAddress = sessionManager.getControllerAddress()
        if (controllerAddress.isNullOrBlank()) {
            failureResult.value = DataFetchResult.Error(exception = null, errorMessage = GlobalConstants.ERR_MSG_INVALID_CONTROLLER_ADDRESS)
            return
        }
        val url = "http://" + controllerAddress + ":" + port + WebApiUrls.FEED_UPDATE_SCHEDULE
        val authHeader = "Basic " + Base64.encode(("$user:$password").encodeToByteArray())
        val postParams = modifiedEntry.getApiParametersForUpdating(
            timestampOldString = formatTimestamp(millisSinceEpoch),
            timestampNewString = formatTimestamp(timestampMillis)
        )

        val requestSuccess = withContext(ioDispatcher.dispatcher) {
            try {
                apiService.updateFeedScheduleEntry(url, authHeader, postParams)
            } catch (e: Exception) {
                logger.recordException(e)
                false
            }
        }
        if (requestSuccess) {
            delay(timeMillis = 3000L)
            triggerRefresh()
        } else {
            pendingUpdates.value -= originalKey
            failureResult.value = DataFetchResult.Error(exception = null, errorMessage = GlobalConstants.ERR_MSG_UPDATING_FEED_SCHEDULE_ENTRY_FAILED)
        }
    }

        /**
     * Forces a refresh of the scheduled feed entries from the network.
     * Clears any existing error state before triggering the refresh.
     */
    override suspend fun triggerRefresh() {
        if (failureResult.value != null) {
            failureResult.value = null
        } else {
            refreshTrigger.emit(value = Unit)
        }
    }
}
