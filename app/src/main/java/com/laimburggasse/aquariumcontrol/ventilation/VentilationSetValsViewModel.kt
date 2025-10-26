package com.laimburggasse.aquariumcontrol.ventilation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.laimburggasse.aquariumcontrol.webapi.DataFetchResult
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class VentilationSetValsViewModel(
    private val ventilationSetValsRepository: VentilationSetValsRepository
) : ViewModel() {

    val ventilationSetValsUiState: StateFlow<VentilationSetValsUiState> = ventilationSetValsRepository.ventilationSetValsFlow
        .map { result ->
            // @todo: add functionality for sanity check on values
            when (result) {
                is DataFetchResult.Error -> VentilationSetValsUiState.Error(errorMessage = result.errorMessage)
                is DataFetchResult.Success -> {
                    VentilationSetValsUiState.Success(ventilationSetVals = result.data)
                }
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = VentilationSetValsUiState.Loading // The UI starts in a loading state
        )

    fun onRangeChanged(start: Float, end: Float) {
        ventilationSetValsRepository.updateLocalValues(
            VentilationSetVals(
                ventilationSwitchOffTemp = start.toDouble(),
                ventilationSwitchOnTemp = end.toDouble()
            )
        )
    }

    fun onLowerValueChanged(value: Float) {
        // @todo: implement range limitation
        val uiState: VentilationSetValsUiState = ventilationSetValsUiState.value
        if (uiState is VentilationSetValsUiState.Success) {
            val updatedSetVals: VentilationSetVals = uiState.ventilationSetVals.copy(ventilationSwitchOffTemp = value.toDouble())
            ventilationSetValsRepository.updateLocalValues(ventilationSetVals = updatedSetVals)
        }
    }

    fun onUpperValueChanged(value: Float) {
        // @todo: implement range limitation
        val uiState: VentilationSetValsUiState = ventilationSetValsUiState.value
        if (uiState is VentilationSetValsUiState.Success) {
            val updatedSetVals: VentilationSetVals = uiState.ventilationSetVals.copy(ventilationSwitchOnTemp = value.toDouble())
            ventilationSetValsRepository.updateLocalValues(ventilationSetVals = updatedSetVals)
        }
    }

    fun resetValues() {
        ventilationSetValsRepository.reset()
    }

    fun loadData() {
        viewModelScope.launch {
            ventilationSetValsRepository.fetchVentilationSetVals()
        }
    }

    fun updateValues() {
        viewModelScope.launch {
            ventilationSetValsRepository.updateValues()
        }
    }

    fun onVentilationSetValsEvent(event: VentilationSetValsEvent) {
        when (event) {
            is VentilationSetValsEvent.ChangeLowerValue -> {
                onLowerValueChanged(event.lowerValue)
            }
            is VentilationSetValsEvent.ChangeRange -> {
                onRangeChanged(event.lowerValue, event.upperValue)
            }
            is VentilationSetValsEvent.ChangeUpperValue -> {
                onUpperValueChanged(event.upperValue)
            }
            VentilationSetValsEvent.Reset -> {
                resetValues()
            }
            VentilationSetValsEvent.Update -> {
                updateValues()
            }
        }
    }
}