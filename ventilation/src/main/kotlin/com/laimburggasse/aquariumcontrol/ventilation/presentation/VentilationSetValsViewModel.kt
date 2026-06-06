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

package com.laimburggasse.aquariumcontrol.ventilation.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.laimburggasse.aquariumcontrol.common.data.remote.DataFetchResult
import com.laimburggasse.aquariumcontrol.common.domain.model.SetValsSanityCheckResult
import com.laimburggasse.aquariumcontrol.ventilation.domain.model.VentilationSetVals
import com.laimburggasse.aquariumcontrol.ventilation.domain.repository.VentilationSetValsRepository
import com.laimburggasse.aquariumcontrol.ventilation.domain.ventilationSetValsSanityCheck
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Inject

/**
 * ViewModel for the Ventilation control screen.
 * Handles the state, validation, and updating of the ventilation temperature thresholds.
 *
 * @property ventilationSetValsRepository The repository for managing ventilation setpoint values.
 */
class VentilationSetValsViewModel @Inject constructor(
    private val ventilationSetValsRepository: VentilationSetValsRepository
) : ViewModel() {
    /** Reactive state indicating if the currently entered ventilation thresholds are mathematically valid. */
    val ventilationSetValsValid = MutableStateFlow<Boolean?>(value = true)

    /** The reactive UI state for the ventilation controls, mapped from the repository flow. */
    val ventilationSetValsUiState: StateFlow<VentilationSetValsUiState> =
        ventilationSetValsRepository.ventilationSetValsFlow
        .map { result ->
            when (result) {
                is DataFetchResult.Error -> VentilationSetValsUiState.Error(errorMessage = result.errorMessage)
                is DataFetchResult.Success -> {
                    val ventilationSetVals: VentilationSetVals = result.data
                    val checkResult: SetValsSanityCheckResult<Unit> = ventilationSetValsSanityCheck(ventilationSetVals)
                    when (checkResult) {
                        is SetValsSanityCheckResult.Error -> ventilationSetValsValid.value = false
                        is SetValsSanityCheckResult.Success<*> -> ventilationSetValsValid.value = true
                    }
                    VentilationSetValsUiState.Success(ventilationSetVals)
                }
                DataFetchResult.Loading -> VentilationSetValsUiState.Loading
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = VentilationSetValsUiState.Loading // The UI starts in a loading state
        )

    
    /**
     * Updates the local optimistic state when the entire temperature range changes.
     *
     * @param start The new switch-off temperature (lower bound).
     * @param end The new switch-on temperature (upper bound).
     */
    fun onRangeChanged(start: Float, end: Float) {
        val ventilationSetVals = VentilationSetVals(
            ventilationSwitchOffTemp = start.toDouble(),
            ventilationSwitchOnTemp = end.toDouble()
        )
        ventilationSetValsRepository.updateLocalValues(ventilationSetVals)
    }

    
    /**
     * Updates the local optimistic state for the lower (switch-off) temperature.
     *
     * @param value The new switch-off temperature.
     */
    fun onLowerValueChanged(value: Float) {
        val uiState: VentilationSetValsUiState = ventilationSetValsUiState.value
        if (uiState is VentilationSetValsUiState.Success) {
            val ventilationSetVals: VentilationSetVals = uiState.ventilationSetVals.copy(ventilationSwitchOffTemp = value.toDouble())
            ventilationSetValsRepository.updateLocalValues(ventilationSetVals)
        }
    }

    
    /**
     * Updates the local optimistic state for the upper (switch-on) temperature.
     *
     * @param value The new switch-on temperature.
     */
    fun onUpperValueChanged(value: Float) {
        val uiState: VentilationSetValsUiState = ventilationSetValsUiState.value
        if (uiState is VentilationSetValsUiState.Success) {
            val ventilationSetVals: VentilationSetVals = uiState.ventilationSetVals.copy(ventilationSwitchOnTemp = value.toDouble())
            ventilationSetValsRepository.updateLocalValues(ventilationSetVals)
        }
    }

    
    /**
     * Discards any unsaved local modifications and reverts the UI to the last known server state.
     */
    fun resetValues() {
        ventilationSetValsRepository.reset()
    }

    
    /**
     * Commits the current local optimistic modifications to the remote server via the repository.
     */
    fun updateValues() {
        viewModelScope.launch {
            ventilationSetValsRepository.updateValues()
        }
    }

    
    /**
     * Central event handler for all UI interactions on the ventilation control screen.
     *
     * @param event The specific [VentilationSetValsUiEvent] to process.
     */
    fun onVentilationSetValsEvent(event: VentilationSetValsUiEvent) {
        when (event) {
            is VentilationSetValsUiEvent.ChangeLowerValue -> {
                onLowerValueChanged(event.lowerValue)
            }
            is VentilationSetValsUiEvent.ChangeRange -> {
                onRangeChanged(event.lowerValue, event.upperValue)
            }
            is VentilationSetValsUiEvent.ChangeUpperValue -> {
                onUpperValueChanged(event.upperValue)
            }
            VentilationSetValsUiEvent.Reset -> {
                resetValues()
            }
            VentilationSetValsUiEvent.Update -> {
                updateValues()
            }
            VentilationSetValsUiEvent.Refresh -> {
                viewModelScope.launch {
                    ventilationSetValsRepository.triggerRefresh()
                }
            }
        }
    }
}