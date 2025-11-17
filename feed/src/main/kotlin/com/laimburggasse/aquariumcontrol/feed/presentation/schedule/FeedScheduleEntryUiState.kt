package com.laimburggasse.aquariumcontrol.feed.presentation.schedule

import com.laimburggasse.aquariumcontrol.feed.domain.model.FeedScheduleEntry

sealed class FeedScheduleEntryUiState {
    data class Success(val feedScheduleEntry: FeedScheduleEntry) : FeedScheduleEntryUiState()
    data class Error(val errorMessage: String) : FeedScheduleEntryUiState()
}