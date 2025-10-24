package com.laimburggasse.aquariumcontrol.feed.entry

import com.laimburggasse.aquariumcontrol.feed.common.FeedProfile

sealed class FeedProfilesUiState {
    object Empty : FeedProfilesUiState()
    object Loading : FeedProfilesUiState()
    data class Success(val feedProfiles: List<FeedProfile>) : FeedProfilesUiState()
    data class Error(val errorMessage: String) : FeedProfilesUiState()
}