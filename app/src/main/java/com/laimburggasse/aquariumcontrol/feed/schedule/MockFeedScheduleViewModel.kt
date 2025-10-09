package com.laimburggasse.aquariumcontrol.feed.schedule

import com.laimburggasse.aquariumcontrol.MainViewModel
import com.laimburggasse.aquariumcontrol.feed.FeedProfile
import java.util.Calendar

class MockFeedScheduleViewModel(
    override val feedScheduleUiState: FeedScheduleUiState,
    override val isRefreshing: Boolean
) : FeedScheduleViewModelInterface {
    override fun getFeedScheduleEntries(): List<FeedScheduleEntry> {
        return emptyList()
    }

    override fun loadData(mainViewModel: MainViewModel) {
        // do nothing
    }

    override fun deleteFeedScheduleEntry(
        feedScheduleEntry: FeedScheduleEntry,
        mainViewModel: MainViewModel
    ) {
        // do nothing
    }

    override fun getFeedScheduleEntry(profileName: String, milliSecondsSinceEpoch: Long): FeedScheduleEntry? {
        return null
    }

    override fun addFeedScheduleEntry(
        feedScheduleEntry: FeedScheduleEntry,
        feedProfile: FeedProfile,
        repeatDaily: Boolean,
        mainViewModel: MainViewModel,
        selectedCalendar: Calendar
    ) {
        // do nothing
    }
}