package com.laimburggasse.aquariumcontrol.balling.history

sealed class BallingHistoryUiState {
    object Loading : BallingHistoryUiState()
    data class Success(val ballingHistory: List<BallingHistoryDosingEvent>) : BallingHistoryUiState()
    data class Error(val errorMessage: String) : BallingHistoryUiState()
}