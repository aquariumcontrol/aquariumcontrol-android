package com.laimburggasse.aquariumcontrol.heating

sealed class HeatingUiState {
    object Loading : HeatingUiState()
    data class Success(val ventilationSetVals: HeatingSetVals) : HeatingUiState()
    data class Error(val errorMessage: String) : HeatingUiState()
}