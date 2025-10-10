package com.laimburggasse.aquariumcontrol.refill.history

sealed class RefillHistoryUiState {
    object Loading : RefillHistoryUiState()
    object Empty : RefillHistoryUiState()
    data class Success(val refillHistoryEvents: List<RefillHistoryEvent>) : RefillHistoryUiState()
    data class Error(val errorMessage: String) : RefillHistoryUiState()
}