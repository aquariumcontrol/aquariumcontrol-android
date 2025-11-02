package com.laimburggasse.aquariumcontrol.feed.schedule

import android.util.Log
import com.laimburggasse.aquariumcontrol.SessionManager
import com.laimburggasse.aquariumcontrol.webapi.DataFetchResult
import com.laimburggasse.aquariumcontrol.webapi.WebApiPostRequest
import com.laimburggasse.aquariumcontrol.webapi.WebApiUrls
import com.laimburggasse.aquariumcontrol.webapi.webApiPostRequestExecution
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
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
open class FeedScheduleRepository @Inject constructor(private val sessionManager: SessionManager) {

    // @todo: unify repository scopes
    private val repositoryScope = CoroutineScope(context = SupervisorJob() + Dispatchers.Default)

    // Triggers and holders for optimistic updates
    private val refreshTrigger = MutableSharedFlow<Unit>(replay = 1)
    private val pendingDeletions = MutableStateFlow<Set<FeedScheduleEntryKey>>(value = emptySet())
    private val pendingAdditions = MutableStateFlow<Set<FeedScheduleEntry>>(value = emptySet())
    private val pendingUpdates = MutableStateFlow<Map<FeedScheduleEntryKey, FeedScheduleEntry>>(value = emptyMap())

    // Private flow that ONLY handles fetching data from the network.
    // It is only triggered by controller changes or manual refreshes.
    @OptIn(ExperimentalCoroutinesApi::class)
    private val networkFlow: StateFlow<DataFetchResult<List<FeedScheduleEntry>>> = combine(
        flow = sessionManager.selectedControllerProfile,
        flow2 = refreshTrigger.onStart { emit(value = Unit) }
    ) { controller, _ ->
        controller
    }.flatMapLatest { controller ->
        flow {
            if (controller == null) {
                emit(value = DataFetchResult.Error(exception = null, errorMessage = "No controller selected."))
                return@flow
            }
            // @todo: emit loading state
            emit(value = DataFetchResult.Error(exception = null, errorMessage = "Loading..."))
            Log.d("FeedScheduleRepository", "Fetching feed schedule from server.")
            val networkResult: DataFetchResult<List<FeedScheduleEntry>> =
                withContext(context = Dispatchers.IO) {
                val url = "http://${controller.address}${WebApiUrls.FEED_GET_SCHEDULE}"
                val scheduleReader = FeedScheduleEntryReader(url)
                val importResult: DataFetchResult<List<FeedScheduleEntryImport>> =
                    scheduleReader.readFeedScheduleEntries()
                when (importResult) {
                    is DataFetchResult.Success -> DataFetchResult.Success(
                        data = importResult.data.toFeedScheduleEntryList()
                    )
                    is DataFetchResult.Error -> importResult
                }
            }
            // After a successful network fetch, we know the ground truth has been updated.
            // This is the ONLY place where we should clear the pending states.
            if (networkResult is DataFetchResult.Success) {
                Log.d("FeedScheduleRepository", "Network fetch successful. Clearing pending states.")
                pendingDeletions.value = emptySet()
                pendingAdditions.value = emptySet()
                pendingUpdates.value = emptyMap()
            }
            emit(value = networkResult)
        }
    }.stateIn(
        scope = repositoryScope,
        started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000L),
        initialValue = DataFetchResult.Error(exception = null, errorMessage = "Initial")
    )

    // The PUBLIC flow combines the stable network data with fast-changing local updates.
    // This flow will NOT trigger a network fetch when local data changes.
    @OptIn(ExperimentalCoroutinesApi::class)
    open val scheduleFlow: StateFlow<DataFetchResult<List<FeedScheduleEntry>>> = combine(
        flow = networkFlow, // The "slow but truthful" source
        flow2 = pendingDeletions,
        flow3 = pendingAdditions,
        flow4 = pendingUpdates
    ) { networkResult, deletions, additions, updates ->
        // This transformation logic runs whenever the network data OR any pending update changes.
        Log.d("FeedScheduleRepository", "Combining flows...")
        when (networkResult) {
            is DataFetchResult.Success -> {
                var profiles: List<FeedScheduleEntry> = networkResult.data
                // Apply optimistic updates on top of the latest network data
                if (deletions.isNotEmpty()) {
                    profiles = profiles.filterNot { entry -> deletions.contains(
                        FeedScheduleEntryKey(
                            profileId = entry.profileId,
                            timestamp = entry.timestamp.time
                        )
                    ) }
                }
                if (additions.isNotEmpty()) {
                    profiles = profiles + additions
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
        started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000L),
        initialValue = DataFetchResult.Error(exception = null, errorMessage = "Initial")
    )

    open suspend fun triggerRefresh() {
        refreshTrigger.emit(value = Unit)
    }

    open suspend fun deleteFeedScheduleEntry(feedScheduleEntry: FeedScheduleEntry) {
        Log.d("FeedScheduleRepository", "Request to delete feed schedule entry: $feedScheduleEntry")

        // 1. Check if the entry to be deleted is a pending addition.
        //    We can check if it exists in the `pendingAdditions` set.
        val isPendingAddition: Boolean = pendingAdditions.value.any {
            it.profileId == feedScheduleEntry.profileId && it.timestamp.time == feedScheduleEntry.timestamp.time
        }

        if (isPendingAddition) {
            // If it's a locally created entry, remove it from the pending additions.
            pendingAdditions.value -= feedScheduleEntry
        }
        val keyToDelete = FeedScheduleEntryKey(
            profileId = feedScheduleEntry.profileId,
            timestamp = feedScheduleEntry.timestamp.time
        )

        // Avoid creating duplicates in the pending deletions
        if (pendingDeletions.value.contains(keyToDelete)) return

        pendingDeletions.value += keyToDelete
        Log.d(
            "FeedScheduleRepository",
            "Deleting a server entry. Pending deletions: $pendingDeletions"
        )

        // In both cases, trigger the network request, because the add operation
        // may have committed to the server meanwhile.
        repositoryScope.launch(context = Dispatchers.IO) {
            Log.d("FeedScheduleRepository", "Coroutine launched. Sending POST request for deletion.")
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
            }
            // @todo: issue UiState Error
        }
    }

    fun addFeedScheduleEntry(
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

        // Avoid creating duplicates in the list of pending additions
        if (pendingAdditions.value.contains(tempEntry)) return

        pendingAdditions.value += tempEntry

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
                // On failure, remove the pending update to revert the UI change.
                pendingAdditions.value -= tempEntry
                // @todo: issue UiState Error
            }
        }
    }

    fun updateFeedScheduleEntry(
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
                // @todo: issue UiState Error
            }
        }
    }
}
