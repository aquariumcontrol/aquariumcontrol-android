package com.laimburggasse.aquariumcontrol.schedule

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.laimburggasse.aquariumcontrol.webapi.DataFetchResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ScheduleViewModel @Inject constructor(
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
        val currentState: ScheduleUiState = scheduleUiState.value
        if (currentState is ScheduleUiState.Success) {
            val currentEntries: ScheduleEntries = currentState.scheduleEntries
            val updatedEntries: ScheduleEntries = when (scheduleType) {
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
        val currentState: ScheduleUiState = scheduleUiState.value
        if (currentState is ScheduleUiState.Success) {
            val currentEntries: ScheduleEntries = currentState.scheduleEntries
            val updatedEntries: ScheduleEntries = when (scheduleType) {
                ScheduleType.BALLING -> currentEntries.copy(ballingLimitationActive = isActive)
                ScheduleType.REFILL -> currentEntries.copy(refillLimitationActive = isActive)
                ScheduleType.VENTILATION -> currentEntries.copy(ventilationLimitationActive = isActive)
                ScheduleType.HEATING -> currentEntries.copy(heatingLimitationActive = isActive)
            }
            scheduleRepository.updateLocalSchedule(newScheduleEntries = updatedEntries)
        }
    }

    fun updateSchedule() {
        viewModelScope.launch {
            scheduleRepository.updateSchedule()
        }
    }

    fun resetSchedule() {
        viewModelScope.launch {
            scheduleRepository.fetchScheduleSetVals()
        }
    }
    init {
        viewModelScope.launch {
            scheduleRepository.fetchScheduleSetVals()
        }
    }

    fun onScheduleEvent(scheduleEvent: ScheduleEvent) {
        when (scheduleEvent) {
            is ScheduleEvent.ChangeLimitationActive -> {
                onLimitationActiveChange(
                    scheduleType = scheduleEvent.scheduleType,
                    isActive = scheduleEvent.isActive
                )
            }
            is ScheduleEvent.ChangeTime -> {
                onTimeChange(
                    scheduleType = scheduleEvent.scheduleType,
                    timeType = scheduleEvent.timeType,
                    newTime = scheduleEvent.newTime
                )
            }
            ScheduleEvent.Update -> {
                updateSchedule()
            }
            ScheduleEvent.Reset -> {
                resetSchedule()
            }
        }
    }
}
