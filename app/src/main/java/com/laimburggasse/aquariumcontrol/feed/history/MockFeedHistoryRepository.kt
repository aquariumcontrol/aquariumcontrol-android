package com.laimburggasse.aquariumcontrol.feed.history

import com.laimburggasse.aquariumcontrol.MockMainViewModel
import com.laimburggasse.aquariumcontrol.webapi.DataFetchResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class MockFeedHistoryRepository(
    mockData: DataFetchResult<List<FeedHistoryEvent>>
): FeedHistoryRepository(mainViewModel = MockMainViewModel()) {

    private val _feedHistoryEventFlow = MutableStateFlow(
        value = mockData
    )

    override val feedHistoryEventFlow = _feedHistoryEventFlow.asStateFlow()

    override suspend fun fetchFeedHistoryEvents(showFullWeek: Boolean) {
        // do nothing
    }
}