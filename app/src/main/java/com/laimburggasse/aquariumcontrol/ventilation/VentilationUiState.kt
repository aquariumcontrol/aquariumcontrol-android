package com.laimburggasse.aquariumcontrol.ventilation

sealed class VentilationUiState {
    object Loading : VentilationUiState()
    data class Success(val ventilationSetVals: VentilationSetVals) : VentilationUiState()
    data class Error(val errorMessage: String) : VentilationUiState()
}