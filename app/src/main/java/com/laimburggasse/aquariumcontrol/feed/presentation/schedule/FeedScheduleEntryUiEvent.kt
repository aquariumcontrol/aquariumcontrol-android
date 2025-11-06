package com.laimburggasse.aquariumcontrol.feed.presentation.schedule

import com.laimburggasse.aquariumcontrol.feed.domain.model.FeedProfile
import com.laimburggasse.aquariumcontrol.feed.domain.model.FeedScheduleEntry
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