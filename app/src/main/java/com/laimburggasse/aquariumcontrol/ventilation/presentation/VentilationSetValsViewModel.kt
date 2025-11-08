package com.laimburggasse.aquariumcontrol.ventilation.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.laimburggasse.aquariumcontrol.ventilation.data.VentilationSetValsRepository
import com.laimburggasse.aquariumcontrol.ventilation.domain.model.VentilationSetVals
import com.laimburggasse.aquariumcontrol.ventilation.domain.ventilationSetValsSanityCheck
import com.laimburggasse.aquariumcontrol.webapi.DataFetchResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VentilationSetValsViewModel @Inject constructor(
    private val ventilationSetValsRepository: VentilationSetValsRepository
) : ViewModel() {
    val ventilationSetValsUiState: StateFlow<VentilationSetValsUiState> =
        ventilationSetValsRepository.ventilationSetValsFlow
        .map { result ->
            when (result) {
                is DataFetchResult.Error -> VentilationSetValsUiState.Error(errorMessage = result.errorMessage)
                is DataFetchResult.Success -> {
                    ventilationSetValsSanityCheck(ventilationSetVals = result.data)
                }
                DataFetchResult.Loading -> VentilationSetValsUiState.Loading
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
        }
    }
}