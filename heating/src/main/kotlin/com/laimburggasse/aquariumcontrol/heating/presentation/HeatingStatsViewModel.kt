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

package com.laimburggasse.aquariumcontrol.heating.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.laimburggasse.aquariumcontrol.heating.domain.repository.HeatingStatsRepository
import com.laimburggasse.aquariumcontrol.common.data.remote.DataFetchResult
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Inject

/**
 * ViewModel for the Heating statistical data screen.
 * Responsible for observing heating telemetry logs and exposing the reactive UI state.
 *
 * @property heatingStatsRepository The repository for fetching and refreshing heating statistics.
 */
class HeatingStatsViewModel @Inject constructor(
    private val heatingStatsRepository: HeatingStatsRepository
): ViewModel() {
    /** The reactive UI state for the heating statistics charts, derived from the repository flow. */
    val heatingStatsUiState: StateFlow<HeatingStatsUiState> = heatingStatsRepository.heatingStatsFlow
        .map { result ->
            when(result) {
                is DataFetchResult.Error -> HeatingStatsUiState.Error(errorMessage = result.errorMessage)
                is DataFetchResult.Success -> {
                    HeatingStatsUiState.Success(heatingStats = result.data)
                }
                DataFetchResult.Loading -> HeatingStatsUiState.Loading
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = HeatingStatsUiState.Loading
        )

    
    /**
     * Central event handler for UI interactions on the heating statistics screen.
     *
     * @param event The specific [HeatingStatsUiEvent] to process (e.g., Refresh).
     */
    fun onHeatingStatsEvent(event: HeatingStatsUiEvent) {
        when(event) {
            is HeatingStatsUiEvent.Refresh-> {
                viewModelScope.launch {
                    heatingStatsRepository.triggerRefresh()
                }
            }
        }
    }
}