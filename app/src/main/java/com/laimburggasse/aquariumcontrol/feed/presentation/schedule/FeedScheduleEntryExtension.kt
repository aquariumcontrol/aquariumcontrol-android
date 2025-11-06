package com.laimburggasse.aquariumcontrol.feed.presentation.schedule

import com.laimburggasse.aquariumcontrol.feed.data.remote.dto.FeedScheduleEntryImport
import com.laimburggasse.aquariumcontrol.feed.domain.model.FeedScheduleEntry


fun List<FeedScheduleEntryImport>.toFeedScheduleEntryList(): List<FeedScheduleEntry> {
    return this.map { it.transformToFeedScheduleEntry() }
}