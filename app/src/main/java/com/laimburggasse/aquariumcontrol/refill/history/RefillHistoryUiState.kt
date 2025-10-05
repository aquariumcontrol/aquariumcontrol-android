package com.laimburggasse.aquariumcontrol.refill.history

sealed class RefillHistoryUiState {
    object Loading : RefillHistoryUiState()
    object Empty : RefillHistoryUiState()
    data class Success(val refillHistory: List<RefillEvent>) : RefillHistoryUiState()
    data class Error(val errorMessage: String) : RefillHistoryUiState()
}