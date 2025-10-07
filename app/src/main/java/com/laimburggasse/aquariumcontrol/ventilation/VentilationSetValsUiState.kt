package com.laimburggasse.aquariumcontrol.ventilation

sealed class VentilationSetValsUiState {
    object Loading : VentilationSetValsUiState()
    object Empty: VentilationSetValsUiState()
    data class Success(val ventilationSetVals: VentilationSetVals) : VentilationSetValsUiState()
    data class Error(val errorMessage: String) : VentilationSetValsUiState()
}