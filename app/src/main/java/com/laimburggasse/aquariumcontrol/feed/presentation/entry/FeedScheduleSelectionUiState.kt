package com.laimburggasse.aquariumcontrol.feed.presentation.entry

import com.laimburggasse.aquariumcontrol.feed.domain.model.FeedScheduleEntry

sealed class FeedScheduleSelectionUiState {
    object Loading : FeedScheduleSelectionUiState()
    object Empty : FeedScheduleSelectionUiState()
    data class Error(val errorMessage: String) : FeedScheduleSelectionUiState()
    data class Success(val feedScheduleEntries: List<FeedScheduleEntry>) : FeedScheduleSelectionUiState()
}