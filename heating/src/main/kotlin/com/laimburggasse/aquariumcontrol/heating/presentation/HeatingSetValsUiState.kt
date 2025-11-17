package com.laimburggasse.aquariumcontrol.heating.presentation

import com.laimburggasse.aquariumcontrol.heating.domain.model.HeatingSetVals

sealed class HeatingSetValsUiState {
    object Loading : HeatingSetValsUiState()
    object Empty: HeatingSetValsUiState()
    data class Success(val heatingSetVals: HeatingSetVals) : HeatingSetValsUiState()
    data class Error(val errorMessage: String) : HeatingSetValsUiState()
}