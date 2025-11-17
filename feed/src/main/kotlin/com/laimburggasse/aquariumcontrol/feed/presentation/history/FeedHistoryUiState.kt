package com.laimburggasse.aquariumcontrol.feed.presentation.history

import com.laimburggasse.aquariumcontrol.feed.domain.model.FeedHistoryLogEntry

sealed class FeedHistoryUiState {
    object Loading : FeedHistoryUiState()
    object Empty : FeedHistoryUiState()
    data class Success(val feedHistoryLogEntries: List<FeedHistoryLogEntry>) : FeedHistoryUiState()
    data class Error(val errorMessage: String) : FeedHistoryUiState()
}