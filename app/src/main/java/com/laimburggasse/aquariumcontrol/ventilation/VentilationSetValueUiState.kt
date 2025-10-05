package com.laimburggasse.aquariumcontrol.ventilation

sealed class VentilationSetValueUiState {
    object Loading : VentilationSetValueUiState()
    data class Success(val ventilationSetVals: VentilationSetVals) : VentilationSetValueUiState()
    data class Error(val errorMessage: String) : VentilationSetValueUiState()
}