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

package com.laimburggasse.aquariumcontrol.schedule.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.laimburggasse.aquariumcontrol.common.data.remote.DataFetchResult
import com.laimburggasse.aquariumcontrol.schedule.domain.model.ScheduleEntries
import com.laimburggasse.aquariumcontrol.schedule.domain.repository.ScheduleRepository
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
                DataFetchResult.Loading -> ScheduleUiState.Loading
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
            scheduleRepository.updateLocalSchedule(newScheduleEntries = updatedEntries)
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
            scheduleRepository.reset()
        }
    }

    fun onScheduleEvent(scheduleUiEvent: ScheduleUiEvent) {
        when (scheduleUiEvent) {
            is ScheduleUiEvent.ChangeLimitationActive -> {
                onLimitationActiveChange(
                    scheduleType = scheduleUiEvent.scheduleType,
                    isActive = scheduleUiEvent.isActive
                )
            }
            is ScheduleUiEvent.ChangeTime -> {
                onTimeChange(
                    scheduleType = scheduleUiEvent.scheduleType,
                    timeType = scheduleUiEvent.timeType,
                    newTime = scheduleUiEvent.newTime
                )
            }
            ScheduleUiEvent.Update -> {
                updateSchedule()
            }
            ScheduleUiEvent.Reset -> {
                resetSchedule()
            }
            ScheduleUiEvent.Refresh -> {
                viewModelScope.launch {
                    scheduleRepository.triggerRefresh()
                }
            }
        }
    }
}
