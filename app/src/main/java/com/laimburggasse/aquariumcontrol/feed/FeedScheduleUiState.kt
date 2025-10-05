package com.laimburggasse.aquariumcontrol.feed

import com.laimburggasse.aquariumcontrol.feed.schedule.FeedScheduleEntry

sealed class FeedScheduleUiState {
    object Loading : FeedScheduleUiState()
    data class Success(val controllerSchedule: List<FeedScheduleEntry>) : FeedScheduleUiState()
    data class Error(val errorMessage: String) : FeedScheduleUiState()
}