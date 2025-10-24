package com.laimburggasse.aquariumcontrol.feed.history

sealed class FeedHistoryUiState {
    object Loading : FeedHistoryUiState()
    object Empty : FeedHistoryUiState()
    data class Success(val feedHistoryLogEntries: List<FeedHistoryLogEntry>) : FeedHistoryUiState()
    data class Error(val errorMessage: String) : FeedHistoryUiState()
}