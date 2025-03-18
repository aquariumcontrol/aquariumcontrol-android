package com.laimburggasse.aquariumcontrol.feed.schedule


fun List<FeedScheduleEntryImport>.toFeedScheduleEntryList(): List<FeedScheduleEntry> {
    return this.map { it.transformToFeedScheduleEntry() }
}