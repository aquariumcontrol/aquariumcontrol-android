package com.laimburggasse.aquariumcontrol.balling.presentation.history

import com.laimburggasse.aquariumcontrol.balling.domain.model.BallingHistoryLogEntry

sealed class BallingHistoryUiState {
    object Loading : BallingHistoryUiState()
    object Empty : BallingHistoryUiState()
    data class Success(val ballingHistoryLogEntries: List<BallingHistoryLogEntry>) : BallingHistoryUiState()
    data class Error(val errorMessage: String) : BallingHistoryUiState()
}