package com.laimburggasse.aquariumcontrol.schedule

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.laimburggasse.aquariumcontrol.MainViewModel
import com.laimburggasse.aquariumcontrol.webapi.DataFetchResult
import com.laimburggasse.aquariumcontrol.webapi.WebApiPostRequest
import com.laimburggasse.aquariumcontrol.webapi.WebApiUrls
import com.laimburggasse.aquariumcontrol.webapi.webApiPostRequestExecution
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ScheduleViewModel(): ViewModel(), ScheduleViewModelInterface {
    override var scheduleUiState by mutableStateOf<ScheduleUiState>(value = ScheduleUiState.Loading)
        private set

    override fun updateSchedule(
        mainViewModel: MainViewModel,
        scheduleEntries: ScheduleEntries
    ) {
        viewModelScope.launch {
            webApiPostRequestExecution(
                mainViewModel = mainViewModel,
                apiUrl = WebApiUrls.ACTUATOR_SCHEDULE_POST_UPDATE,
                webApiPostRequest = WebApiPostRequest(),
                optionalGetParams = scheduleEntries.getApiParams(),
            )
        }
    }

    override fun loadData(
        mainViewModel: MainViewModel,
        ballingScheduleEntryViewModel: ScheduleEntryViewModel,
        refillScheduleEntryViewModel: ScheduleEntryViewModel,
        ventilationScheduleEntryViewModel: ScheduleEntryViewModel,
        heatingScheduleEntryViewModel: ScheduleEntryViewModel,
    ) {
        viewModelScope.launch {
            val result: DataFetchResult<ScheduleEntries> = fetchScheduleSetVals(mainViewModel)
            when (result) {
                is DataFetchResult.Success -> {
                    // Update the view models with the data
                    val scheduleEntries: ScheduleEntries = result.data
                    ballingScheduleEntryViewModel.setValues(
                        newSetValStartTime = scheduleEntries.ballingStartTime,
                        newSetValStopTime = scheduleEntries.ballingStopTime,
                        newSetValLimitationActive = scheduleEntries.ballingLimitationActive,
                    )
                    refillScheduleEntryViewModel.setValues(
                        newSetValStartTime = scheduleEntries.refillStartTime,
                        newSetValStopTime = scheduleEntries.refillStopTime,
                        newSetValLimitationActive = scheduleEntries.refillLimitationActive
                    )
                    ventilationScheduleEntryViewModel.setValues(
                        newSetValStartTime = scheduleEntries.ventilationStartTime,
                        newSetValStopTime = scheduleEntries.ventilationStopTime,
                        newSetValLimitationActive = scheduleEntries.ventilationLimitationActive
                    )
                    heatingScheduleEntryViewModel.setValues(
                        newSetValStartTime = scheduleEntries.heatingStartTime,
                        newSetValStopTime = scheduleEntries.heatingStopTime,
                        newSetValLimitationActive = scheduleEntries.heatingLimitationActive
                    )
                    scheduleUiState = ScheduleUiState.Success(scheduleEntries)
                }
                is DataFetchResult.Error -> {
                    scheduleUiState = ScheduleUiState.Error(errorMessage = result.errorMessage)
                }
            }
        }
    }
    private suspend fun fetchScheduleSetVals(
        mainViewModel: MainViewModel,
    ): DataFetchResult<ScheduleEntries> {
        return withContext(Dispatchers.IO) {
            val url =
                "http://" + mainViewModel.getControllerAddress() + WebApiUrls.ACTUATOR_SCHEDULE_GET_VALS

            val scheduleEntryReader = ScheduleEntryReader(url)
            val result: DataFetchResult<List<ScheduleStringEntry>> = scheduleEntryReader.readScheduleEntryList()
            when (result) {
                is DataFetchResult.Success -> {
                    val scheduleStringEntryList: List<ScheduleStringEntry> = result.data
                    if (!scheduleStringEntryList.isNotEmpty()) {
                        // Convert the list of TimeDataStringSample to a list of TimeDataSample
                        val scheduleEntryList =
                            scheduleStringEntryList.map { scheduleStringEntry ->
                                scheduleStringEntry.transformToSetVal()
                            }
                        val scheduleEntries: ScheduleEntries = ScheduleEntriesFactory(scheduleEntryList).create()
                        DataFetchResult.Success(data = scheduleEntries)
                    }
                    else {
                        val scheduleEntriesEmpty: ScheduleEntries = ScheduleEntriesFactory(scheduleEntryList = emptyList()).create()
                        DataFetchResult.Success(data = scheduleEntriesEmpty)
                    }
                }
                is DataFetchResult.Error -> {
                    DataFetchResult.Error(exception = result.exception, errorMessage = result.errorMessage)
                }
            }
        }
    }
}
