package com.laimburggasse.aquariumcontrol.feed.schedule

import com.laimburggasse.aquariumcontrol.MainViewModel
import com.laimburggasse.aquariumcontrol.feed.FeedProfile
import java.util.Calendar

interface FeedScheduleViewModelInterface {
    val feedScheduleUiState: FeedScheduleUiState
    val isRefreshing: Boolean
    fun getFeedScheduleEntries(): List<FeedScheduleEntry>

    fun loadData(mainViewModel: MainViewModel)

    fun deleteFeedScheduleEntry(feedScheduleEntry: FeedScheduleEntry, mainViewModel: MainViewModel)

    fun getFeedScheduleEntry(profileName: String, milliSecondsSinceEpoch: Long): FeedScheduleEntry?

    fun addFeedScheduleEntry(
        feedScheduleEntry: FeedScheduleEntry,
        feedProfile: FeedProfile,
        repeatDaily: Boolean,
        mainViewModel: MainViewModel,
        selectedCalendar: Calendar)
}