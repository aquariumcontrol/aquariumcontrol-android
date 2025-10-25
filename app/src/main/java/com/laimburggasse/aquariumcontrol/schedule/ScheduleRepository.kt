package com.laimburggasse.aquariumcontrol.schedule

import com.laimburggasse.aquariumcontrol.MainViewModel
import com.laimburggasse.aquariumcontrol.webapi.DataFetchResult
import com.laimburggasse.aquariumcontrol.webapi.WebApiPostRequest
import com.laimburggasse.aquariumcontrol.webapi.WebApiUrls
import com.laimburggasse.aquariumcontrol.webapi.webApiPostRequestExecution
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext

open class ScheduleRepository(
    private val mainViewModel: MainViewModel
) {
    private val _scheduleFlow = MutableStateFlow<DataFetchResult<ScheduleEntries>>(
        value = DataFetchResult.Success(data = ScheduleEntries())
    )
    open val scheduleFlow = _scheduleFlow.asStateFlow()

    /**
     * Updates the local state of the schedule entries.
     * This allows for instant UI updates without waiting for a network call.
     * The ViewModel calls this function whenever the user changes a slider or toggle.
     */
    open fun updateLocalSchedule(newScheduleEntries: ScheduleEntries) {
        // Check if the current state is Success before updating, to preserve Error states.
        if (_scheduleFlow.value is DataFetchResult.Success) {
            _scheduleFlow.value = DataFetchResult.Success(data = newScheduleEntries)
        }
    }

    open suspend fun updateSchedule() {
        if (scheduleFlow.value is DataFetchResult.Success) {
            val scheduleEntries: ScheduleEntries = (scheduleFlow.value as DataFetchResult.Success).data
            webApiPostRequestExecution(
                mainViewModel = mainViewModel,
                apiUrl = WebApiUrls.ACTUATOR_SCHEDULE_POST_UPDATE,
                webApiPostRequest = WebApiPostRequest(),
                optionalGetParams = scheduleEntries.getApiParams(),
            )
        }
    }

    open suspend fun fetchScheduleSetVals() {
        return withContext(context = Dispatchers.IO) {
            val url: String =
                "http://" + mainViewModel.getControllerAddress() + WebApiUrls.ACTUATOR_SCHEDULE_GET_VALS

            val scheduleEntryReader = ScheduleEntryReader(url)
            val result: DataFetchResult<List<ScheduleStringEntry>> = scheduleEntryReader.readScheduleEntryList()
            when (result) {
                is DataFetchResult.Success -> {
                    val scheduleStringEntryList: List<ScheduleStringEntry> = result.data
                    if (scheduleStringEntryList.isNotEmpty()) {
                        // Convert the list of TimeDataStringSample to a list of TimeDataSample
                        val scheduleEntryList : List<ScheduleEntry> =
                            scheduleStringEntryList.map { scheduleStringEntry ->
                                scheduleStringEntry.transformToSetVal()
                            }
                        val scheduleEntries: ScheduleEntries = ScheduleEntriesFactory(scheduleEntryList).create()
                        _scheduleFlow.value = DataFetchResult.Success(data = scheduleEntries)
                    }
                    else {
                        val scheduleEntriesEmpty: ScheduleEntries = ScheduleEntriesFactory(
                            scheduleEntryList = emptyList()
                        ).create()
                        _scheduleFlow.value = DataFetchResult.Success(data = scheduleEntriesEmpty)
                    }
                }
                is DataFetchResult.Error -> {
                    _scheduleFlow.value = DataFetchResult.Error(
                        exception = result.exception,
                        errorMessage = result.errorMessage
                    )
                }
            }
        }
    }
}