package com.laimburggasse.aquariumcontrol.feed.history

sealed class FeedHistoryUiState {
    object Loading : FeedHistoryUiState()
    data class Success(val ballingHistory: List<FeedHistoryEvent>) : FeedHistoryUiState()
    data class Error(val errorMessage: String) : FeedHistoryUiState()
}