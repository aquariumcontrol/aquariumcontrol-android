package com.laimburggasse.aquariumcontrol.feed.schedule

import com.laimburggasse.aquariumcontrol.MockMainViewModel
import com.laimburggasse.aquariumcontrol.webapi.DataFetchResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class MockFeedScheduleRepository(
    mockFeedScheduleEntries: DataFetchResult<List<FeedScheduleEntry>>
): FeedScheduleRepository(mainViewModel = MockMainViewModel()) {

    private val _scheduleFlow = MutableStateFlow(value = mockFeedScheduleEntries)

    override val scheduleFlow = _scheduleFlow.asStateFlow()

    override suspend fun fetchFeedScheduleEntries() {
        // do nothing
    }
}