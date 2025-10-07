package com.laimburggasse.aquariumcontrol.heating

sealed class HeatingSetValsUiState {
    object Loading : HeatingSetValsUiState()
    object Empty: HeatingSetValsUiState()
    data class Success(val heatingSetVals: HeatingSetVals) : HeatingSetValsUiState()
    data class Error(val errorMessage: String) : HeatingSetValsUiState()
}