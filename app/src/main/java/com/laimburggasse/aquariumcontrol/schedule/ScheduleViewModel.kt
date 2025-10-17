package com.laimburggasse.aquariumcontrol.schedule

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.laimburggasse.aquariumcontrol.webapi.DataFetchResult
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ScheduleViewModel(
    private val scheduleRepository: ScheduleRepository
): ViewModel() {

    val scheduleUiState: StateFlow<ScheduleUiState> = scheduleRepository.scheduleFlow
        .map { result ->
            when (result) {
                is DataFetchResult.Error -> ScheduleUiState.Error(errorMessage = result.errorMessage)
                is DataFetchResult.Success -> {
                    ScheduleUiState.Success(scheduleEntries = result.data)
                }
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = ScheduleUiState.Loading // The UI starts in a loading state
        )

    /**
     * Updates the start or stop time for a specific schedule type.
     * This function uses the immutable `copy()` pattern to ensure StateFlow updates.
     */
    fun onTimeChange(scheduleType: ScheduleType, timeType: TimeType, newTime: Float) {
        val currentState = scheduleUiState.value
        if (currentState is ScheduleUiState.Success) {
            val currentEntries = currentState.scheduleEntries
            val updatedEntries = when (scheduleType) {
                ScheduleType.BALLING -> when (timeType) {
                    TimeType.START -> currentEntries.copy(ballingStartTime = newTime)
                    TimeType.STOP -> currentEntries.copy(ballingStopTime = newTime)
                }
                ScheduleType.REFILL -> when (timeType) {
                    TimeType.START -> currentEntries.copy(refillStartTime = newTime)
                    TimeType.STOP -> currentEntries.copy(refillStopTime = newTime)
                }
                ScheduleType.VENTILATION -> when (timeType) {
                    TimeType.START -> currentEntries.copy(ventilationStartTime = newTime)
                    TimeType.STOP -> currentEntries.copy(ventilationStopTime = newTime)
                }
                ScheduleType.HEATING -> when (timeType) {
                    TimeType.START -> currentEntries.copy(heatingStartTime = newTime)
                    TimeType.STOP -> currentEntries.copy(heatingStopTime = newTime)
                }
            }
            scheduleRepository.updateLocalSchedule(updatedEntries)
        }
    }

    /**
     * Updates the "limitation active" status for a specific schedule type.
     */
    fun onLimitationActiveChange(scheduleType: ScheduleType, isActive: Boolean) {
        val currentState = scheduleUiState.value
        if (currentState is ScheduleUiState.Success) {
            val currentEntries = currentState.scheduleEntries
            val updatedEntries = when (scheduleType) {
                ScheduleType.BALLING -> currentEntries.copy(ballingLimitationActive = isActive)
                ScheduleType.REFILL -> currentEntries.copy(refillLimitationActive = isActive)
                ScheduleType.VENTILATION -> currentEntries.copy(ventilationLimitationActive = isActive)
                ScheduleType.HEATING -> currentEntries.copy(heatingLimitationActive = isActive)
            }
            scheduleRepository.updateLocalSchedule(updatedEntries)
        }
    }

    fun updateSchedule(
        scheduleEntries: ScheduleEntries
    ) {
        val currentState = scheduleUiState.value
        if (currentState is ScheduleUiState.Success) {
            viewModelScope.launch {
                scheduleRepository.updateSchedule(scheduleEntries)
            }
        }
    }
}
