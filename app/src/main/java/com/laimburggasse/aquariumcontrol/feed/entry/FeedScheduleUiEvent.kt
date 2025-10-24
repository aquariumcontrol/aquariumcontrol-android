package com.laimburggasse.aquariumcontrol.feed.entry

import com.laimburggasse.aquariumcontrol.feed.schedule.FeedScheduleEntry

sealed interface FeedScheduleUiEvent {
    data class GetFeedProfileIdByName(val feedProfileName: String) : FeedScheduleUiEvent
    data class DeleteFeedScheduleEntry(val feedScheduleEntry: FeedScheduleEntry) : FeedScheduleUiEvent
}