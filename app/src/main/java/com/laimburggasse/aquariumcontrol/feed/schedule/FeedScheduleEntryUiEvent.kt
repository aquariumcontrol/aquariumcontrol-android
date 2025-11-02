package com.laimburggasse.aquariumcontrol.feed.schedule

import com.laimburggasse.aquariumcontrol.feed.common.FeedProfile
import java.util.Calendar

sealed interface FeedScheduleEntryUiEvent {
    data class UpdateFeedScheduleEntry(
        val feedProfile: FeedProfile,
        val feedScheduleEntry: FeedScheduleEntry,
        val selectedCalendar: Calendar,
        val millisSinceEpoch: Long,
        val isNewEntry: Boolean
    ) : FeedScheduleEntryUiEvent
}