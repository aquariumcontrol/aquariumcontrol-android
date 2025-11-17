package com.laimburggasse.aquariumcontrol.refill.presentation.history

import com.laimburggasse.aquariumcontrol.refill.domain.model.RefillHistoryLogEntry

sealed class RefillHistoryUiState {
    object Loading : RefillHistoryUiState()
    object Empty : RefillHistoryUiState()
    data class Success(val refillHistoryLogEntries: List<RefillHistoryLogEntry>) : RefillHistoryUiState()
    data class Error(val errorMessage: String) : RefillHistoryUiState()
}