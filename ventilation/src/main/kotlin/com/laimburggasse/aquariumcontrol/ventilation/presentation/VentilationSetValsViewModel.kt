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

class VentilationSetValsViewModel @Inject constructor(
    private val ventilationSetValsRepository: VentilationSetValsRepository
) : ViewModel() {
    val ventilationSetValsValid = MutableStateFlow<Boolean?>(value = true)

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

    fun onRangeChanged(start: Float, end: Float) {
        val ventilationSetVals = VentilationSetVals(
            ventilationSwitchOffTemp = start.toDouble(),
            ventilationSwitchOnTemp = end.toDouble()
        )
        ventilationSetValsRepository.updateLocalValues(ventilationSetVals)
    }

    fun onLowerValueChanged(value: Float) {
        val uiState: VentilationSetValsUiState = ventilationSetValsUiState.value
        if (uiState is VentilationSetValsUiState.Success) {
            val ventilationSetVals: VentilationSetVals = uiState.ventilationSetVals.copy(ventilationSwitchOffTemp = value.toDouble())
            ventilationSetValsRepository.updateLocalValues(ventilationSetVals)
        }
    }

    fun onUpperValueChanged(value: Float) {
        val uiState: VentilationSetValsUiState = ventilationSetValsUiState.value
        if (uiState is VentilationSetValsUiState.Success) {
            val ventilationSetVals: VentilationSetVals = uiState.ventilationSetVals.copy(ventilationSwitchOnTemp = value.toDouble())
            ventilationSetValsRepository.updateLocalValues(ventilationSetVals)
        }
    }

    fun resetValues() {
        ventilationSetValsRepository.reset()
    }

    fun updateValues() {
        viewModelScope.launch {
            ventilationSetValsRepository.updateValues()
        }
    }

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