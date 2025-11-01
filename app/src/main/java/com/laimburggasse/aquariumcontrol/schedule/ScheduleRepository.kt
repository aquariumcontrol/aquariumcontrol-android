package com.laimburggasse.aquariumcontrol.schedule

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
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
open class ScheduleRepository @Inject constructor(
    private val sessionManager: SessionManager,
) {
    // @todo: unify repository scopes
    // A dedicated scope for the repository ensures the StateFlow lives as long as the app.
    private val repositoryScope = CoroutineScope(context = SupervisorJob() + Dispatchers.Default)

    // Triggers for manual refreshes and a holder for optimistic local updates.
    private val refreshTrigger = MutableSharedFlow<Unit>(replay = 1)
    private val pendingUpdate = MutableStateFlow<ScheduleEntries?>(value = null)

    // The manual fetchScheduleSetVals() function is no longer needed for automatic loading.

    @OptIn(ExperimentalCoroutinesApi::class)
    open val networkFlow: StateFlow<DataFetchResult<ScheduleEntries>> = combine(
        flow = sessionManager.selectedControllerProfile,
        flow2 = refreshTrigger.onStart { emit(value = Unit) } // Triggers the first fetch
    ) { controller, _ ->
        controller
    }.flatMapLatest { controller ->
        flow {
            // @todo: implement loading state
            if (controller == null) {
                // If no controller is selected, emit a valid empty state
                emit(value = DataFetchResult.Error(exception = null, errorMessage = "No Controller Selected"))
                return@flow
            }

            // A controller is present, so start fetching
            emit(value = DataFetchResult.Error(exception = null, errorMessage = "Loading"))
            val networkResult: DataFetchResult<ScheduleEntries> = fetchScheduleSetVals()
            emit(value = networkResult)
        }
    }.stateIn(
        scope = repositoryScope,
        started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000L),
        initialValue = DataFetchResult.Error(exception = null, errorMessage = "Initial Load")
    )

    // The final UI state combines the network data with any local (optimistic) updates.
    val scheduleFlow: StateFlow<DataFetchResult<ScheduleEntries>> = combine(
        flow = networkFlow,
        flow2 = pendingUpdate
    ) { networkResult, pending ->
        if (pending != null) {
            // If there's a pending local update, show it instantly.
            DataFetchResult.Success(data = pending)
        } else {
            // Otherwise, show the latest result from the network.
            networkResult
        }
    }.stateIn(
        scope = repositoryScope,
        started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000L),
        initialValue = DataFetchResult.Error(exception = null, errorMessage = "Initial Load")
    )

    /**
     * Triggers a manual refresh of the schedule data.
     */
    suspend fun triggerRefresh() {
        refreshTrigger.emit(Unit)
    }

    /**
     * Applies an optimistic update to the local state without committing to the server.
     */
    fun updateLocalSchedule(newScheduleEntries: ScheduleEntries) {
        pendingUpdate.value = newScheduleEntries
    }

    /**
     * Commits the current local values to the server.
     */
    open suspend fun updateSchedule() {
        if (scheduleFlow.value is DataFetchResult.Success) {
            val scheduleEntries: ScheduleEntries = (scheduleFlow.value as DataFetchResult.Success).data
            webApiPostRequestExecution(
                sessionManager = sessionManager,
                apiUrl = WebApiUrls.ACTUATOR_SCHEDULE_POST_UPDATE,
                webApiPostRequest = WebApiPostRequest(),
                optionalGetParams = scheduleEntries.getApiParams(),
            )
        }
    }

    /**
     * Reverts any uncommitted local changes by clearing the pending update.
     * The public flow will automatically revert to the last successfully fetched state.
     */
    fun reset() {
        pendingUpdate.value = null
    }

    // helper function for the flow
    open suspend fun fetchScheduleSetVals(): DataFetchResult<ScheduleEntries> {
        return withContext(context = Dispatchers.IO) {
            val url: String =
                "http://" + sessionManager.getControllerAddress() + WebApiUrls.ACTUATOR_SCHEDULE_GET_VALS

            val scheduleEntryReader = ScheduleEntryReader(url)
            val result: DataFetchResult<List<ScheduleStringEntry>> =
                scheduleEntryReader.readScheduleEntryList()
            val processedResult: DataFetchResult<ScheduleEntries> = when (result) {
                is DataFetchResult.Success -> {
                    val scheduleStringEntryList: List<ScheduleStringEntry> = result.data
                    if (scheduleStringEntryList.isNotEmpty()) {
                        // Convert the list of ScheduleStringEntry to a list of ScheduleEntry
                        val scheduleEntryList: List<ScheduleEntry> =
                            scheduleStringEntryList.map { scheduleStringEntry ->
                                scheduleStringEntry.transformToSetVal()
                            }
                        val scheduleEntries: ScheduleEntries =
                            ScheduleEntriesFactory(scheduleEntryList).create()
                        DataFetchResult.Success(data = scheduleEntries)
                    } else {
                        val scheduleEntriesEmpty: ScheduleEntries = ScheduleEntriesFactory(
                            scheduleEntryList = emptyList()
                        ).create()
                        DataFetchResult.Success(data = scheduleEntriesEmpty)
                    }
                }
                is DataFetchResult.Error -> {
                    DataFetchResult.Error(
                        exception = result.exception,
                        errorMessage = result.errorMessage
                    )
                }
            }
            return@withContext processedResult
        }
    }
}