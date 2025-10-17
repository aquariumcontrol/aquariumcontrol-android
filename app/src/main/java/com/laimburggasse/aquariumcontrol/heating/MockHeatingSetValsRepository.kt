package com.laimburggasse.aquariumcontrol.heating

import com.laimburggasse.aquariumcontrol.MockMainViewModel
import com.laimburggasse.aquariumcontrol.webapi.DataFetchResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

open class MockHeatingSetValsRepository(
    mockHeatingSetValsDataFetchResult: DataFetchResult<HeatingSetVals>
): HeatingSetValsRepository(mainViewModel = MockMainViewModel()) {
    private val _heatingSetValsFlow = MutableStateFlow(value = mockHeatingSetValsDataFetchResult)
    override val heatingSetValsFlow = _heatingSetValsFlow.asStateFlow()

    override fun updateLocalValues(heatingSetVals: HeatingSetVals) {
        // do nothing
    }

    override suspend fun fetchHeatingSetVals() {
        // do nothing
    }

    override suspend fun updateValues() {
        // do nothing
    }

}