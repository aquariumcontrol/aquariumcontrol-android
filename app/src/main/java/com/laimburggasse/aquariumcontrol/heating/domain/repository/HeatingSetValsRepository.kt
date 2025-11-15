package com.laimburggasse.aquariumcontrol.heating.domain.repository

import com.laimburggasse.aquariumcontrol.heating.domain.model.HeatingSetVals
import com.laimburggasse.aquariumcontrol.lib.data.remote.DataFetchResult
import kotlinx.coroutines.flow.StateFlow

interface HeatingSetValsRepository {
    val heatingSetValsFlow: StateFlow<DataFetchResult<HeatingSetVals>>
    fun updateLocalValues(heatingSetVals: HeatingSetVals)
    suspend fun updateValues()
    fun reset()
    suspend fun triggerRefresh()}