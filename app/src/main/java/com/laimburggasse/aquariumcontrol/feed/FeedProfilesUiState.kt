package com.laimburggasse.aquariumcontrol.feed

sealed class FeedProfilesUiState {
    object Loading : FeedProfilesUiState()
    data class Success(val controllerProfiles: List<FeedProfile>) : FeedProfilesUiState()
    data class Error(val errorMessage: String) : FeedProfilesUiState()
}