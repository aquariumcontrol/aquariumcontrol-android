package com.laimburggasse.aquariumcontrol.overview

import com.laimburggasse.aquariumcontrol.MockMainViewModel
import com.laimburggasse.aquariumcontrol.webapi.DataFetchResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

open class MockOverviewRepository(
    mockOverviewDataFetchResult: DataFetchResult<OverviewData>
): OverviewRepository(mainViewModel = MockMainViewModel()) {
    private val _overviewData = MutableStateFlow(value = mockOverviewDataFetchResult)
    override val overviewData = _overviewData.asStateFlow()

    override suspend fun fetchOverviewData() {
        // do nothing
    }
}