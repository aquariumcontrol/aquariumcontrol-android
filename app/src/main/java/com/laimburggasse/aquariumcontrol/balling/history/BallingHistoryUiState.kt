package com.laimburggasse.aquariumcontrol.balling.history

sealed class BallingHistoryUiState {
    object Loading : BallingHistoryUiState()
    object Empty : BallingHistoryUiState()
    data class Success(val ballingHistoryDosingEvents: List<BallingHistoryDosingEvent>) : BallingHistoryUiState()
    data class Error(val errorMessage: String) : BallingHistoryUiState()
}