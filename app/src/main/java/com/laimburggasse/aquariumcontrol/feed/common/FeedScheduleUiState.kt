package com.laimburggasse.aquariumcontrol.feed.common

import com.laimburggasse.aquariumcontrol.feed.schedule.FeedScheduleEntry

sealed class FeedScheduleUiState {
    object Loading : FeedScheduleUiState()
    object Empty : FeedScheduleUiState()
    data class Error(val errorMessage: String) : FeedScheduleUiState()
    data class Success(val feedScheduleEntries: List<FeedScheduleEntry>) : FeedScheduleUiState()
}