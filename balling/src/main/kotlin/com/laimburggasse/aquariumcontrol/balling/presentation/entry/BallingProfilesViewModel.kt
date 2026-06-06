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

package com.laimburggasse.aquariumcontrol.balling.presentation.entry

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.laimburggasse.aquariumcontrol.balling.domain.repository.BallingProfilesRepository
import com.laimburggasse.aquariumcontrol.common.data.remote.DataFetchResult
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Inject

/**
 * ViewModel for the Balling profiles overview screen.
 * Responsible for observing the dosing profiles and exposing the corresponding UI state.
 *
 * @property ballingProfilesRepository The repository for fetching and refreshing Balling profiles.
 */
class BallingProfilesViewModel @Inject constructor(
    private val ballingProfilesRepository: BallingProfilesRepository
): ViewModel() {

    /** The reactive UI state for the Balling profiles list, derived from the repository flow. */
    val ballingProfilesUiState: StateFlow<BallingProfilesUiState> = ballingProfilesRepository.ballingProfiles
        .map { result ->
            when (result) {
                is DataFetchResult.Error -> BallingProfilesUiState.Error(errorMessage = result.errorMessage)
                is DataFetchResult.Success -> {
                    if (result.data.isEmpty()) {
                        BallingProfilesUiState.Empty
                    } else {
                        BallingProfilesUiState.Success(ballingProfiles = result.data)
                    }
                }
                is DataFetchResult.Loading -> BallingProfilesUiState.Loading
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = BallingProfilesUiState.Loading // The UI starts in a loading state
        )

   
    /**
     * Triggers a manual refresh of the Balling profiles from the remote source.
     */
   fun onRefresh() {
        viewModelScope.launch {
            ballingProfilesRepository.triggerRefresh()
        }
    }
}