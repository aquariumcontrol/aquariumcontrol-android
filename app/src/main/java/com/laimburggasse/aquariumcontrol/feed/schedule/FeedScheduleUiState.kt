package com.laimburggasse.aquariumcontrol.feed.schedule

import com.laimburggasse.aquariumcontrol.feed.FeedProfile

sealed class FeedScheduleUiState {
    data class Error(val errorMessage: String) : FeedScheduleUiState()
    data class Success(val feedProfile: FeedProfile, val feedScheduleEntry: FeedScheduleEntry) : FeedScheduleUiState()
}