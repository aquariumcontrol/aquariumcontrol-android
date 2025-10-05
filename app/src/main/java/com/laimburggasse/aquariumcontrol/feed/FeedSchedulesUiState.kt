package com.laimburggasse.aquariumcontrol.feed

import com.laimburggasse.aquariumcontrol.feed.schedule.FeedScheduleEntry

sealed class FeedSchedulesUiState {
    object Loading : FeedSchedulesUiState()
    data class Success(val controllerSchedule: List<FeedScheduleEntry>) : FeedSchedulesUiState()
    data class Error(val errorMessage: String) : FeedSchedulesUiState()
}