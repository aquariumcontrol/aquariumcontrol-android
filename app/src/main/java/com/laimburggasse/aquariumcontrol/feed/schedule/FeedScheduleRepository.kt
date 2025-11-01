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
    private val pendingDeletions = MutableStateFlow<Set<Int>>(emptySet())
    private val pendingAdditions = MutableStateFlow<Set<FeedScheduleEntry>>(emptySet())
    private val pendingUpdates = MutableStateFlow<Map<Int, FeedScheduleEntry>>(emptyMap())

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
        flow2 =pendingDeletions,
        flow3 = pendingAdditions,
        flow4 = pendingUpdates
    ) { networkResult, deletions, additions, updates ->
        // This transformation logic runs whenever the network data OR any pending update changes.
        when (networkResult) {
            is DataFetchResult.Success -> {
                var profiles: List<FeedScheduleEntry> = networkResult.data
                // Apply optimistic updates on top of the latest network data
                if (deletions.isNotEmpty()) {
                    profiles = profiles.filterIndexed { index, _ -> !deletions.contains(index) }
                }
                if (additions.isNotEmpty()) {
                    profiles = profiles + additions
                }
                if (updates.isNotEmpty()) {
                    profiles = profiles.mapIndexed { index, entry -> updates[index] ?: entry }
                }
                DataFetchResult.Success(data = profiles) // Emit the final, merged list
            }
            // If the network is loading or has an error, just pass that state through.
            else -> networkResult
        }
    }.stateIn(
        scope = repositoryScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = DataFetchResult.Error(exception = null, errorMessage = "Initial")
    )

    open suspend fun triggerRefresh() {
        refreshTrigger.emit(Unit)
    }

    open suspend fun deleteFeedScheduleEntry(feedScheduleEntry: FeedScheduleEntry) {
        val originalIndex: Int = (networkFlow.value as? DataFetchResult.Success)?.data?.indexOf(feedScheduleEntry) ?: -1
        if (originalIndex == -1) return

        pendingDeletions.value += originalIndex

        repositoryScope.launch(context = Dispatchers.IO) {
            webApiPostRequestExecution(
                apiUrl = WebApiUrls.FEED_POST_SCHEDULE_REMOVE_ENTRY,
                sessionManager = sessionManager,
                webApiPostRequest = WebApiPostRequest(),
                optionalGetParams = feedScheduleEntry.getApiParameters()
            )
        }
    }

    suspend fun addFeedScheduleEntry(feedProfileName: String, repeatDaily: Boolean, selectedCalendar: Calendar) {
        // @todo: check api call
        val timestamp = Timestamp(selectedCalendar.timeInMillis)
        val tempEntry = FeedScheduleEntry(
            profileName = "$feedProfileName (Saving...)",
            repeatDaily = repeatDaily,
            timestamp = timestamp
        )
        pendingAdditions.value += tempEntry

        val apiParams: Map<String, String> = tempEntry.getApiParameters()
        repositoryScope.launch(context = Dispatchers.IO) {
            webApiPostRequestExecution(
                sessionManager = sessionManager,
                webApiPostRequest = WebApiPostRequest(),
                optionalGetParams = apiParams,
                apiUrl = WebApiUrls.FEED_POST_PROFILE_SCHEDULE
            )
        }
    }

    suspend fun modifyFeedScheduleEntry(originalEntry: FeedScheduleEntry, modifiedEntry: FeedScheduleEntry) {
        // @todo: check API call
        val originalIndex: Int = (networkFlow.value as? DataFetchResult.Success)?.data?.indexOf(originalEntry) ?: -1
        if (originalIndex == -1) return

        pendingUpdates.value += (originalIndex to modifiedEntry.copy(profileName = "${modifiedEntry.profileName} (Saving...)"))

        repositoryScope.launch(context = Dispatchers.IO) {
            webApiPostRequestExecution(
                sessionManager = sessionManager,
                webApiPostRequest = WebApiPostRequest(),
                optionalGetParams = modifiedEntry.getApiParameters(),
                apiUrl = WebApiUrls.FEED_POST_PROFILE_UPDATE_SCHEDULE
            )
        }
    }
}
