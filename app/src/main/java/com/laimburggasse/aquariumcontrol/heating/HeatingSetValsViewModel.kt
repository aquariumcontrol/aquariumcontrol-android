package com.laimburggasse.aquariumcontrol.heating

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.laimburggasse.aquariumcontrol.webapi.DataFetchResult
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HeatingSetValsViewModel(
    private var heatingSetValsRepository: HeatingSetValsRepository,
) : ViewModel() {
    val heatingSetValsUiState: StateFlow<HeatingSetValsUiState> = heatingSetValsRepository.heatingSetValsFlow
        .map { result ->
            // @todo: add functionality for sanity check on values
            when (result) {
                is DataFetchResult.Error -> HeatingSetValsUiState.Error(errorMessage = result.errorMessage)
                is DataFetchResult.Success -> {
                    HeatingSetValsUiState.Success(heatingSetVals = result.data)
                }
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = HeatingSetValsUiState.Loading // The UI starts in a loading state
        )

    val onRangeChangedBySlider: (Float, Float) -> Unit = { start, end ->
        heatingSetValsRepository.updateLocalValues(
            HeatingSetVals(
                heatingSwitchOnTemp = start.toDouble(),
                heatingSwitchOffTemp = end.toDouble()
            )
        )

    }

    fun onLowerValueChanged(value: Float) {
        // @todo: implement range limitation
        val uiState: HeatingSetValsUiState = heatingSetValsUiState.value
        if (uiState is HeatingSetValsUiState.Success) {
            val updatedSetVals: HeatingSetVals = uiState.heatingSetVals.copy(heatingSwitchOnTemp = value.toDouble())
            heatingSetValsRepository.updateLocalValues(heatingSetVals = updatedSetVals)
        }
    }

    fun onUpperValueChanged(value: Float) {
        // @todo: implement range limitation
        val uiState: HeatingSetValsUiState = heatingSetValsUiState.value
        if (uiState is HeatingSetValsUiState.Success) {
            val updatedSetVals: HeatingSetVals = uiState.heatingSetVals.copy(heatingSwitchOffTemp = value.toDouble())
            heatingSetValsRepository.updateLocalValues(heatingSetVals = updatedSetVals)
        }
    }

    fun resetValues() {
        heatingSetValsRepository.reset()
    }

    fun loadData() {
        viewModelScope.launch {
            heatingSetValsRepository.fetchHeatingSetVals()
        }
    }

    fun updateValues() {
        viewModelScope.launch {
            heatingSetValsRepository.updateValues()
        }
    }
}