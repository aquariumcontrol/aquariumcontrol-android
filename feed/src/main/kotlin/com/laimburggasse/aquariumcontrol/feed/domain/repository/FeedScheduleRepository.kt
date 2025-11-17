package com.laimburggasse.aquariumcontrol.feed.domain.repository

import com.laimburggasse.aquariumcontrol.feed.domain.model.FeedScheduleEntry
import com.laimburggasse.aquariumcontrol.common.data.remote.DataFetchResult
import kotlinx.coroutines.flow.StateFlow
import java.util.Calendar

interface FeedScheduleRepository {
    val scheduleFlow: StateFlow<DataFetchResult<List<FeedScheduleEntry>>>
    suspend fun deleteFeedScheduleEntry(feedScheduleEntry: FeedScheduleEntry)
    fun addFeedScheduleEntry(
        feedProfileId: Int,
        feedProfileName: String,
        repeatDaily: Boolean,
        selectedCalendar: Calendar
    )
    fun updateFeedScheduleEntry(
        feedProfileId: Int,
        feedProfileName: String,
        repeatDaily: Boolean,
        selectedCalendar: Calendar,
        millisSinceEpoch: Long
    )
    suspend fun triggerRefresh()
}