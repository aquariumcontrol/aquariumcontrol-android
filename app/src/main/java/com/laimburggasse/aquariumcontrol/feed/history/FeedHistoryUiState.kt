package com.laimburggasse.aquariumcontrol.feed.history

sealed class FeedHistoryUiState {
    object Loading : FeedHistoryUiState()
    object Empty : FeedHistoryUiState()
    data class Success(val feedHistory: List<FeedHistoryEvent>) : FeedHistoryUiState()
    data class Error(val errorMessage: String) : FeedHistoryUiState()
}