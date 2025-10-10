package com.laimburggasse.aquariumcontrol.balling.history

import com.laimburggasse.aquariumcontrol.MockMainViewModel
import com.laimburggasse.aquariumcontrol.webapi.DataFetchResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class MockBallingHistoryRepository(
    mockData: DataFetchResult<List<BallingHistoryDosingEvent>>
): BallingHistoryRepository(mainViewModel = MockMainViewModel()) {

    private val _ballingHistoryDosingEventFlow = MutableStateFlow(
        value = mockData
    )

    override val ballingHistoryDosingEventFlow = _ballingHistoryDosingEventFlow.asStateFlow()

    override suspend fun fetchBallingHistoryDosingEvents(showFullWeek: Boolean) {
        // do nothing
    }
}