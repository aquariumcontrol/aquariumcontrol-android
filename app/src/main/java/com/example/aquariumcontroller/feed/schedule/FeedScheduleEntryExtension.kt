package com.example.aquariumcontroller.feed.schedule


fun List<FeedScheduleEntryImport>.toFeedScheduleEntryList(): List<FeedScheduleEntry> {
    return this.map { it.transformToFeedScheduleEntry() }
}