package com.laimburggasse.aquariumcontrol.heating

sealed class HeatingSetValueUiState {
    object Loading : HeatingSetValueUiState()
    data class Success(val heatingSetVals: HeatingSetVals) : HeatingSetValueUiState()
    data class Error(val errorMessage: String) : HeatingSetValueUiState()
}