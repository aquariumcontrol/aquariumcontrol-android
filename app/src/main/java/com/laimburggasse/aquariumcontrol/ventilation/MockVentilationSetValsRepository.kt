package com.laimburggasse.aquariumcontrol.ventilation

import com.laimburggasse.aquariumcontrol.MockMainViewModel
import com.laimburggasse.aquariumcontrol.webapi.DataFetchResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

open class MockVentilationSetValsRepository(
    mockVentilationSetValsDataFetchResult: DataFetchResult<VentilationSetVals>
): VentilationSetValsRepository(mainViewModel = MockMainViewModel()) {
    private val _ventilationSetValsFlow = MutableStateFlow(value = mockVentilationSetValsDataFetchResult)
    override val ventilationSetValsFlow = _ventilationSetValsFlow.asStateFlow()

    override fun updateLocalValues(ventilationSetVals: VentilationSetVals) {
        // do nothing
    }

    override suspend fun fetchVentilationSetVals() {
        // do nothing
    }

    override suspend fun updateValues() {
        // do nothing
    }

}