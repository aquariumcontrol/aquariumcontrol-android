package com.laimburggasse.aquariumcontrol.timedata

import com.laimburggasse.aquariumcontrol.MockMainViewModel
import com.laimburggasse.aquariumcontrol.webapi.DataFetchResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class MockTimeDataRepository(
    private val mockTimeDataFetchResult: DataFetchResult<List<TimeDataSample>>
): TimeDataRepository(mainViewModel = MockMainViewModel()) {
    private val _timeDataSamples = MutableStateFlow(
        value = mockTimeDataFetchResult
    )
    override val timeDataSamples = _timeDataSamples.asStateFlow()

    override suspend fun fetchTimeData() {
        // do nothing
    }
}