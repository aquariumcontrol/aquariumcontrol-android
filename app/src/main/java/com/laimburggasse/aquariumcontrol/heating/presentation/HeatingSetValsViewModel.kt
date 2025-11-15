package com.laimburggasse.aquariumcontrol.heating.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.laimburggasse.aquariumcontrol.heating.data.HeatingSetValsRepository
import com.laimburggasse.aquariumcontrol.heating.domain.heatingSetValsSanityCheck
import com.laimburggasse.aquariumcontrol.heating.domain.model.HeatingSetVals
import com.laimburggasse.aquariumcontrol.lib.domain.model.SetValsSanityCheckResult
import com.laimburggasse.aquariumcontrol.lib.data.remote.DataFetchResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HeatingSetValsViewModel @Inject constructor(
    private var heatingSetValsRepository: HeatingSetValsRepository,
) : ViewModel() {
    val heatingSetValsValid = MutableStateFlow<Boolean?>(value = true)
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
            started = SharingStarted.Companion.Eagerly,
            initialValue = HeatingSetValsUiState.Loading // The UI starts in a loading state
        )

    private val onRangeChanged: (Float, Float) -> Unit = { start, end ->
        heatingSetValsRepository.updateLocalValues(
            HeatingSetVals(
                heatingSwitchOnTemp = start.toDouble(),
                heatingSwitchOffTemp = end.toDouble()
            )
        )

    }

    private fun onLowerValueChanged(value: Float) {
        val uiState: HeatingSetValsUiState = heatingSetValsUiState.value
        if (uiState is HeatingSetValsUiState.Success) {
            val updatedSetVals: HeatingSetVals = uiState.heatingSetVals.copy(heatingSwitchOnTemp = value.toDouble())
            heatingSetValsRepository.updateLocalValues(heatingSetVals = updatedSetVals)
        }
    }

    private fun onUpperValueChanged(value: Float) {
        val uiState: HeatingSetValsUiState = heatingSetValsUiState.value
        if (uiState is HeatingSetValsUiState.Success) {
            val updatedSetVals: HeatingSetVals = uiState.heatingSetVals.copy(heatingSwitchOffTemp = value.toDouble())
            heatingSetValsRepository.updateLocalValues(heatingSetVals = updatedSetVals)
        }
    }

    private fun resetValues() {
        heatingSetValsRepository.reset()
    }

    private fun updateValues() {
        viewModelScope.launch {
            heatingSetValsRepository.updateValues()
        }
    }

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