package com.laimburggasse.aquariumcontrol.feed.add

sealed interface FeedProfileAddUiState {
    object Idle : FeedProfileAddUiState
    object Loading : FeedProfileAddUiState
    object Success : FeedProfileAddUiState
    data class Error(val message: String) : FeedProfileAddUiState
}