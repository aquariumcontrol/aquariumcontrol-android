/*
 *  Copyright 2026 Uwe Martin
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.laimburggasse.aquariumcontrol.summary.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.laimburggasse.aquariumcontrol.common.data.remote.DataFetchResult
import com.laimburggasse.aquariumcontrol.summary.domain.repository.SummaryRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Inject

/**
 * ViewModel for the Summary overview screen.
 * Responsible for managing the UI state of the dashboard by observing the latest sensor and actuator data.
 *
 * @property summaryRepository The repository for fetching and refreshing summary data.
 */
class SummaryViewModel @Inject constructor(
    private val summaryRepository: SummaryRepository
): ViewModel() {
    /** The reactive UI state for the summary dashboard, derived from the repository flow. */
    val summaryUiState: StateFlow<SummaryUiState> = summaryRepository.summaryData
        .map { result ->
            when (result) {
                is DataFetchResult.Error -> SummaryUiState.Error(errorMessage = result.errorMessage)
                is DataFetchResult.Success -> {
                    SummaryUiState.Success(summaryData = result.data)
                }
                DataFetchResult.Loading -> SummaryUiState.Loading
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = SummaryUiState.Loading
        )

    /**
     * Triggers a fetch of the latest summary data from the remote source.
     */
    fun loadData() {
        viewModelScope.launch {
            summaryRepository.fetchSummaryData()
        }
    }

    init {
        loadData()
    }

    /**
     * Central event handler for UI interactions on the summary screen.
     *
     * @param event The specific [SummaryUiEvent] to process (e.g., Refresh).
     */
    fun onSummaryEvent(event: SummaryUiEvent) {
        when (event) {
            is SummaryUiEvent.Refresh -> {
                loadData()
            }
        }
    }
}