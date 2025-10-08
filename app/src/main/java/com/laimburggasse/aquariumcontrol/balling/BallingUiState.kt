package com.laimburggasse.aquariumcontrol.balling

sealed class BallingUiState {
    object Empty : BallingUiState()
    object Loading : BallingUiState()
    data class Success(val ballingProfiles: List<BallingProfile>) : BallingUiState()
    data class Error(val errorMessage: String) : BallingUiState()
}