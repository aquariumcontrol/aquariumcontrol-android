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

package com.laimburggasse.aquariumcontrol.timedata.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.laimburggasse.aquariumcontrol.common.data.remote.DataFetchResult
import com.laimburggasse.aquariumcontrol.timedata.domain.repository.TimeDataRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Inject

/**
 * ViewModel for the Time Data (Telemetry) screen.
 * Responsible for observing real-time sensor and actuator telemetry and exposing it as UI state for charting.
 *
 * @property timeDataRepository The repository for fetching and refreshing time data samples.
 */
class TimeDataViewModel @Inject constructor(
    private val timeDataRepository: TimeDataRepository
) : ViewModel() {
    /** The reactive UI state for the telemetry charts, derived from the repository flow. */
    val timeDataUiState: StateFlow<TimeDataUiState> = timeDataRepository.timeDataSamples
        .map { result ->
            when (result) {
                is DataFetchResult.Error -> TimeDataUiState.Error(errorMessage = result.errorMessage)
                is DataFetchResult.Success -> {
                    if (result.data.isEmpty()) {
                        TimeDataUiState.Empty
                    } else {
                        TimeDataUiState.Success(timeDataSets = TimeDataSets(timeData = result.data))
                    }
                }
                DataFetchResult.Loading -> TimeDataUiState.Loading
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = TimeDataUiState.Loading
        )


    /**
     * Central event handler for UI interactions on the time data screen.
     *
     * @param event The specific [TimeDataUiEvent] to process (e.g., Refresh).
     */
    fun onTimeDataEvent(event: TimeDataUiEvent) {
        when (event) {
            is TimeDataUiEvent.Refresh -> {
                viewModelScope.launch {
                    timeDataRepository.triggerRefresh()
                }
            }
        }
    }
}