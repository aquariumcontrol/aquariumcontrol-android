package com.laimburggasse.aquariumcontrol.ventilation.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.laimburggasse.aquariumcontrol.common.data.remote.DataFetchResult
import com.laimburggasse.aquariumcontrol.common.domain.model.SetValsSanityCheckResult
import com.laimburggasse.aquariumcontrol.ventilation.domain.model.VentilationSetVals
import com.laimburggasse.aquariumcontrol.ventilation.domain.repository.VentilationSetValsRepository
import com.laimburggasse.aquariumcontrol.ventilation.domain.ventilationSetValsSanityCheck
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
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