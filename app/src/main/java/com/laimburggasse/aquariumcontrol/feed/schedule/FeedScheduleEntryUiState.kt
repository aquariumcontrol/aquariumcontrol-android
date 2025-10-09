package com.laimburggasse.aquariumcontrol.feed.schedule

sealed class FeedScheduleEntryUiState {
    data class Success(val feedScheduleEntry: FeedScheduleEntry) : FeedScheduleEntryUiState()
    data class Error(val errorMessage: String) : FeedScheduleEntryUiState()
}