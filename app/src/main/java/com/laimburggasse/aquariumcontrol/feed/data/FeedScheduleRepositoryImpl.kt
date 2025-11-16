package com.laimburggasse.aquariumcontrol.feed.data

import com.laimburggasse.aquariumcontrol.common.domain.GlobalConstants
import com.laimburggasse.aquariumcontrol.common.domain.SessionManager
import com.laimburggasse.aquariumcontrol.common.domain.ControllerProfile
import com.laimburggasse.aquariumcontrol.common.di.ApplicationScope
import com.laimburggasse.aquariumcontrol.feed.data.remote.FeedScheduleEntryReader
import com.laimburggasse.aquariumcontrol.feed.data.remote.dto.FeedScheduleEntryImport
import com.laimburggasse.aquariumcontrol.feed.domain.model.FeedScheduleEntry
import com.laimburggasse.aquariumcontrol.feed.domain.repository.FeedScheduleRepository
import com.laimburggasse.aquariumcontrol.feed.presentation.schedule.toFeedScheduleEntryList
import com.laimburggasse.aquariumcontrol.common.data.remote.DataFetchResult
import com.laimburggasse.aquariumcontrol.common.data.remote.WebApiPostRequest
import com.laimburggasse.aquariumcontrol.common.data.remote.WebApiUrls
import com.laimburggasse.aquariumcontrol.common.data.remote.webApiPostRequestExecution
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
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
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.sql.Timestamp
import java.util.Calendar
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
open class FeedScheduleRepositoryImpl @Inject constructor(
    private val sessionManager: SessionManager,
    @ApplicationScope private val repositoryScope: CoroutineScope
): FeedScheduleRepository {
    // Triggers and holders for optimistic updates
    private val refreshTrigger = MutableSharedFlow<Unit>(replay = 1)
    private val pendingDeletions = MutableStateFlow<Set<FeedScheduleEntryKey>>(value = emptySet())
    private val pendingUpdates =
        MutableStateFlow<Map<FeedScheduleEntryKey, FeedScheduleEntry>>(value = emptyMap())

    // A flow to hold the failure value
    private val failureResult = MutableStateFlow<DataFetchResult.Error?>(value = null)

    // Private flow that ONLY handles fetching data from the network.
    // It is only triggered by controller changes or manual refreshes.
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
                emit(
                    value = DataFetchResult.Error(
                        exception = null,
                        errorMessage = GlobalConstants.ERR_MSG_NO_CONTROLLER_SELECTED
                    )
                )
                return@flow
            }

            // If there is no failure and we have a controller, proceed with loading.
            emit(value = DataFetchResult.Loading)

            val networkResult: DataFetchResult<List<FeedScheduleEntry>> =
                withContext(context = Dispatchers.IO) {
                    val url = "http://${controller.address}:${controller.port}${WebApiUrls.FEED_GET_SCHEDULE}"
                    val scheduleReader = FeedScheduleEntryReader(url)
                    val importResult: DataFetchResult<List<FeedScheduleEntryImport>> =
                        scheduleReader.readFeedScheduleEntries()
                    when (importResult) {
                        is DataFetchResult.Success -> DataFetchResult.Success(
                            data = importResult.data.toFeedScheduleEntryList()
                        )

                        is DataFetchResult.Error -> importResult
                        is DataFetchResult.Loading -> DataFetchResult.Error(
                            exception = null,
                            errorMessage = GlobalConstants.ERR_MSG_LOADING_FEED_SCHEDULE_ENTRIES_FAILED
                        )
                    }
                }
            // After a successful network fetch, we know the ground truth has been updated.
            // This is the ONLY place where we should clear the pending states.
            if (networkResult is DataFetchResult.Success) {
                pendingDeletions.value = emptySet()
                pendingUpdates.value = emptyMap()
            }
            emit(value = networkResult)
        }
    }.stateIn(
        scope = repositoryScope,
        started = SharingStarted.Companion.WhileSubscribed(stopTimeoutMillis = 5000L),
        initialValue = DataFetchResult.Loading
    )

    // The PUBLIC flow combines the stable network data with fast-changing local updates.
    // This flow will NOT trigger a network fetch when local data changes.
    @OptIn(ExperimentalCoroutinesApi::class)
    override val scheduleFlow: StateFlow<DataFetchResult<List<FeedScheduleEntry>>> = combine(
        flow = networkFlow, // The "slow but truthful" source
        flow2 = pendingDeletions,
        flow3 = pendingUpdates
    ) { networkResult, deletions, updates ->
        // This transformation logic runs whenever the network data OR any pending update changes.
        when (networkResult) {
            is DataFetchResult.Success -> {
                var profiles: List<FeedScheduleEntry> = networkResult.data
                // Apply optimistic updates on top of the latest network data
                if (deletions.isNotEmpty()) {
                    profiles = profiles.filterNot { entry ->
                        deletions.contains(
                            FeedScheduleEntryKey(
                                profileId = entry.profileId,
                                timestamp = entry.timestamp.time
                            )
                        )
                    }
                }
                if (updates.isNotEmpty()) {
                    profiles = profiles.map { entry ->
                        val key = FeedScheduleEntryKey(
                            profileId = entry.profileId,
                            timestamp = entry.timestamp.time
                        )
                        // If a pending update exists for this key, use it, otherwise use the original.
                        updates[key] ?: entry
                    }
                }
                DataFetchResult.Success(data = profiles) // Emit the final, merged list
            }
            // If the network is loading or has an error, just pass that state through.
            else -> networkResult
        }
    }.stateIn(
        scope = repositoryScope,
        started = SharingStarted.Companion.WhileSubscribed(stopTimeoutMillis = 5000L),
        initialValue = DataFetchResult.Loading
    )

    override suspend fun deleteFeedScheduleEntry(feedScheduleEntry: FeedScheduleEntry) {
        val keyToDelete = FeedScheduleEntryKey(
            profileId = feedScheduleEntry.profileId,
            timestamp = feedScheduleEntry.timestamp.time
        )

        // Avoid creating duplicates in the pending deletions
        if (pendingDeletions.value.contains(keyToDelete)) return

        pendingDeletions.value += keyToDelete

        // In both cases, trigger the network request, because the add operation
        // may have committed to the server meanwhile.
        repositoryScope.launch(context = Dispatchers.IO) {
            val result: Boolean = webApiPostRequestExecution(
                apiUrl = WebApiUrls.FEED_POST_SCHEDULE_REMOVE_ENTRY,
                sessionManager = sessionManager,
                webApiPostRequest = WebApiPostRequest(),
                optionalGetParams = feedScheduleEntry.getApiParameters()
            )
            if (result) {
                delay(timeMillis = 3000L)
                triggerRefresh()
            }
            else {
                // On failure, remove the pending deletion to revert the UI change.
                pendingDeletions.value -= keyToDelete
                failureResult.value = DataFetchResult.Error(
                    exception = null,
                    errorMessage = GlobalConstants.ERR_MSG_DELETING_FEED_SCHEDULE_ENTRY_FAILED
                )
            }
        }
    }

    override fun addFeedScheduleEntry(
        feedProfileId: Int,
        feedProfileName: String,
        repeatDaily: Boolean,
        selectedCalendar: Calendar
    ) {
        val timestamp = Timestamp(selectedCalendar.timeInMillis)
        val keyToFind = FeedScheduleEntryKey(feedProfileId, timestamp.time)

        // Entry has been added before and not yet gone through network update
        // Undelete it, but still run the network request
        if (pendingDeletions.value.contains(keyToFind)) {
            pendingDeletions.value -= keyToFind
        }

        val tempEntry = FeedScheduleEntry(
            timestamp = timestamp,
            profileId = feedProfileId,
            profileName = feedProfileName,
            repeatDaily = repeatDaily
        )

        val apiParams: Map<String, String> = tempEntry.getApiParameters()
        repositoryScope.launch(context = Dispatchers.IO) {
            val result: Boolean = webApiPostRequestExecution(
                sessionManager = sessionManager,
                webApiPostRequest = WebApiPostRequest(),
                optionalGetParams = apiParams,
                apiUrl = WebApiUrls.FEED_POST_PROFILE_SCHEDULE_NEW
            )
            if (result) {
                delay(timeMillis = 3000L)
                triggerRefresh()
            }
            else {
                failureResult.value = DataFetchResult.Error(
                    exception = null,
                    errorMessage = GlobalConstants.ERR_MSG_ADDING_FEED_SCHEDULE_ENTRY_FAILED
                )
            }
        }
    }

    override fun updateFeedScheduleEntry(
        feedProfileId: Int,
        feedProfileName: String,
        repeatDaily: Boolean,
        selectedCalendar: Calendar,
        millisSinceEpoch: Long // data from old entry
    ) {
        // Create the key for the ORIGINAL entry that is being modified.
        val originalKey = FeedScheduleEntryKey(feedProfileId, timestamp = millisSinceEpoch)

        // Create the NEW entry object with the modified data.
        val modifiedEntry = FeedScheduleEntry(
            timestamp = Timestamp(selectedCalendar.timeInMillis),
            profileId = feedProfileId,
            profileName = feedProfileName,
            repeatDaily = repeatDaily
        )

        // Optimistic Update: Add the change to the pendingUpdates map.
        // The key is the original entry, the value is the new optimistic entry.
        pendingUpdates.value += (originalKey to modifiedEntry.copy(profileName = "$feedProfileName (Saving...)"))

        // Launch the background network request.
        repositoryScope.launch(context = Dispatchers.IO) {
            val timestampOld = Timestamp(millisSinceEpoch)
            val result: Boolean = webApiPostRequestExecution(
                sessionManager = sessionManager,
                webApiPostRequest = WebApiPostRequest(),
                optionalGetParams = modifiedEntry.getApiParametersForUpdating(timestampOld),
                apiUrl = WebApiUrls.FEED_POST_PROFILE_UPDATE_SCHEDULE
            )
            if (result) {
                delay(timeMillis = 3000L)
                triggerRefresh()
            }
            else {
                // On failure, remove the pending update to revert the UI change.
                pendingUpdates.value -= originalKey
                failureResult.value = DataFetchResult.Error(
                    exception = null,
                    errorMessage = GlobalConstants.ERR_MSG_UPDATING_FEED_SCHEDULE_ENTRY_FAILED
                )
            }
        }
    }

    override suspend fun triggerRefresh() {
        // Check if the current state is a failure.
        if (failureResult.value != null) {
            // If it is, setting it to null is sufficient to trigger the combine/flatMapLatest chain again.
            failureResult.value = null
        } else {
            // If the system is NOT in a failure state, then we must emit the refreshTrigger
            // to force a new fetch, as nothing else has changed.
            refreshTrigger.emit(value = Unit)
        }
    }
}