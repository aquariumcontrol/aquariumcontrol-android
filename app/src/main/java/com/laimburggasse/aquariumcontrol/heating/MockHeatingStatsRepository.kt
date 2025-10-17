package com.laimburggasse.aquariumcontrol.heating

import com.laimburggasse.aquariumcontrol.MockMainViewModel
import com.laimburggasse.aquariumcontrol.webapi.DataFetchResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

open class MockHeatingStatsRepository(
    mockHeatingStatsDataFetchResult: DataFetchResult<List<HeatingStatsEntry>>
): HeatingStatsRepository(mainViewModel = MockMainViewModel()) {
    private val _heatingStatsFlow = MutableStateFlow(
        value = mockHeatingStatsDataFetchResult
    )
    override val heatingStatsFlow = _heatingStatsFlow.asStateFlow()

    override suspend fun fetchHeatingStats(){
        // do nothing
    }
}