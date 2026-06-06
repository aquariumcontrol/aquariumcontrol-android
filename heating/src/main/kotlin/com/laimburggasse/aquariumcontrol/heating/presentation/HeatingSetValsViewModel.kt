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
import com.laimburggasse.aquariumcontrol.heating.domain.heatingSetValsSanityCheck
import com.laimburggasse.aquariumcontrol.heating.domain.model.HeatingSetVals
import com.laimburggasse.aquariumcontrol.heating.domain.repository.HeatingSetValsRepository
import com.laimburggasse.aquariumcontrol.common.data.remote.DataFetchResult
import com.laimburggasse.aquariumcontrol.common.domain.model.SetValsSanityCheckResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Inject

/**
 * ViewModel for the Heating control screen.
 * Handles the state, validation, and updating of the heating temperature setpoints.
 *
 * @property heatingSetValsRepository The repository for managing heating setpoint values.
 */
class HeatingSetValsViewModel @Inject constructor(
    private var heatingSetValsRepository: HeatingSetValsRepository,
) : ViewModel() {
    /** Reactive state indicating if the currently entered heating setpoints are mathematically valid. */
    val heatingSetValsValid = MutableStateFlow<Boolean?>(value = true)

    /** The reactive UI state for the heating controls, mapped from the repository flow. */
    val heatingSetValsUiState: StateFlow<HeatingSetValsUiState> = heatingSetValsRepository.heatingSetValsFlow
        .map { result ->
            when (result) {
                is DataFetchResult.Error -> HeatingSetValsUiState.Error(errorMessage = result.errorMessage)
                is DataFetchResult.Success -> {
                    val heatingSetVals: HeatingSetVals = result.data
                    val checkResult: SetValsSanityCheckResult<Unit> = heatingSetValsSanityCheck(heatingSetVals)
                    when (checkResult) {
                        is SetValsSanityCheckResult.Error -> heatingSetValsValid.value = false
                        is SetValsSanityCheckResult.Success<*>-> heatingSetValsValid.value = true
                    }
                    HeatingSetValsUiState.Success(heatingSetVals)
                }
                DataFetchResult.Loading -> HeatingSetValsUiState.Loading
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = HeatingSetValsUiState.Loading // The UI starts in a loading state
        )

    
    /**
     * Updates the local optimistic state when the entire temperature range changes.
     *
     * @param start The new switch-on temperature (lower bound).
     * @param end The new switch-off temperature (upper bound).
     */
    private val onRangeChanged: (Float, Float) -> Unit = { start, end ->
        heatingSetValsRepository.updateLocalValues(
            HeatingSetVals(
                heatingSwitchOnTemp = start.toDouble(),
                heatingSwitchOffTemp = end.toDouble()
            )
        )

    }

    
    /**
     * Updates the local optimistic state for the lower (switch-on) temperature.
     *
     * @param value The new switch-on temperature.
     */
    private fun onLowerValueChanged(value: Float) {
        val uiState: HeatingSetValsUiState = heatingSetValsUiState.value
        if (uiState is HeatingSetValsUiState.Success) {
            val updatedSetVals: HeatingSetVals = uiState.heatingSetVals.copy(heatingSwitchOnTemp = value.toDouble())
            heatingSetValsRepository.updateLocalValues(heatingSetVals = updatedSetVals)
        }
    }

    
    /**
     * Updates the local optimistic state for the upper (switch-off) temperature.
     *
     * @param value The new switch-off temperature.
     */
    private fun onUpperValueChanged(value: Float) {
        val uiState: HeatingSetValsUiState = heatingSetValsUiState.value
        if (uiState is HeatingSetValsUiState.Success) {
            val updatedSetVals: HeatingSetVals = uiState.heatingSetVals.copy(heatingSwitchOffTemp = value.toDouble())
            heatingSetValsRepository.updateLocalValues(heatingSetVals = updatedSetVals)
        }
    }

    
    /**
     * Discards any unsaved local modifications and reverts the UI to the last known server state.
     */
    private fun resetValues() {
        heatingSetValsRepository.reset()
    }

    
    /**
     * Commits the current local optimistic modifications to the remote server via the repository.
     */
    private fun updateValues() {
        viewModelScope.launch {
            heatingSetValsRepository.updateValues()
        }
    }

    
    /**
     * Central event handler for all UI interactions on the heating control screen.
     *
     * @param event The specific [HeatingSetValsUiEvent] to process.
     */
    fun onHeatingSetValsEvent(event: HeatingSetValsUiEvent) {
        when (event) {
            is HeatingSetValsUiEvent.ChangeLowerValue -> {
                onLowerValueChanged(event.lowerValue)
            }
            is HeatingSetValsUiEvent.ChangeRange -> {
                onRangeChanged(event.lowerValue, event.upperValue)
            }
            is HeatingSetValsUiEvent.ChangeUpperValue -> {
                onUpperValueChanged(event.upperValue)
            }
            HeatingSetValsUiEvent.Reset -> {
                resetValues()
            }
            HeatingSetValsUiEvent.Update -> {
                updateValues()
            }
            HeatingSetValsUiEvent.Refresh -> {
                viewModelScope.launch {
                    heatingSetValsRepository.triggerRefresh()
                }
            }
        }
    }
}