package com.laimburggasse.aquariumcontrol.feed.presentation.entry

import com.laimburggasse.aquariumcontrol.feed.domain.model.FeedScheduleEntry

sealed interface FeedScheduleUiEvent {
    data class GetFeedProfileIdByName(val feedProfileName: String) : FeedScheduleUiEvent
    data class DeleteFeedScheduleEntry(val feedScheduleEntry: FeedScheduleEntry) : FeedScheduleUiEvent
}