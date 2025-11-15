package com.laimburggasse.aquariumcontrol.ventilation.domain.repository

import com.laimburggasse.aquariumcontrol.lib.data.remote.DataFetchResult
import com.laimburggasse.aquariumcontrol.ventilation.domain.model.VentilationSetVals
import kotlinx.coroutines.flow.StateFlow

interface VentilationSetValsRepository {
    val ventilationSetValsFlow: StateFlow<DataFetchResult<VentilationSetVals>>
    fun updateLocalValues(ventilationSetVals: VentilationSetVals)
    suspend fun updateValues()
    fun reset()
    suspend fun triggerRefresh()
}