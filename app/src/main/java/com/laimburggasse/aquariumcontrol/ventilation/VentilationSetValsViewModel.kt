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

    fun onRangeChangedBySlider(start: Float, end: Float) {
        ventilationSetValsRepository.updateLocalValues(
            VentilationSetVals(
                ventilationSwitchOffTemp = start.toDouble(),
                ventilationSwitchOnTemp = end.toDouble()
            )
        )
    }

    fun onLowerValueChanged(value: Float) {
        if (ventilationSetValsUiState.value is VentilationSetValsUiState.Success) {
            val currentSetVals: VentilationSetVals = (ventilationSetValsUiState.value as VentilationSetValsUiState.Success).ventilationSetVals
            currentSetVals.ventilationSwitchOffTemp = value.toDouble()
            ventilationSetValsRepository.updateLocalValues(ventilationSetVals = currentSetVals)
        }
    }

    fun onUpperValueChanged(value: Float) {
        if (ventilationSetValsUiState.value is VentilationSetValsUiState.Success) {
            val currentSetVals = (ventilationSetValsUiState.value as VentilationSetValsUiState.Success).ventilationSetVals
            currentSetVals.ventilationSwitchOffTemp = value.toDouble()
            ventilationSetValsRepository.updateLocalValues(ventilationSetVals = currentSetVals)
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
}