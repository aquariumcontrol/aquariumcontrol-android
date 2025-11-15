package com.laimburggasse.aquariumcontrol.timedata.domain.repository

import com.laimburggasse.aquariumcontrol.lib.data.remote.DataFetchResult
import com.laimburggasse.aquariumcontrol.timedata.domain.model.TimeDataSample
import kotlinx.coroutines.flow.Flow

interface TimeDataRepository {
    val timeDataSamples: Flow<DataFetchResult<List<TimeDataSample>>>
    suspend fun triggerRefresh()
}