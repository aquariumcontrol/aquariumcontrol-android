package com.laimburggasse.aquariumcontrol.refill.history

import com.laimburggasse.aquariumcontrol.MockMainViewModel
import com.laimburggasse.aquariumcontrol.webapi.DataFetchResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class MockRefillHistoryRepository(
    mockData: DataFetchResult<List<RefillHistoryEvent>>
): RefillHistoryRepository(mainViewModel = MockMainViewModel()) {

    private val _refillHistoryEventFlow = MutableStateFlow(
        value = mockData
    )

    override val refillHistoryEventFlow = _refillHistoryEventFlow.asStateFlow()

    override suspend fun fetchRefillHistoryEvents(showFullWeek: Boolean) {
        // do nothing
    }
}