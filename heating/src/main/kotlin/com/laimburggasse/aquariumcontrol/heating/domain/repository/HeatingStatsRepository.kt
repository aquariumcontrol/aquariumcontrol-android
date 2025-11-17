package com.laimburggasse.aquariumcontrol.heating.domain.repository

import com.laimburggasse.aquariumcontrol.heating.domain.model.HeatingStatsEntry
import com.laimburggasse.aquariumcontrol.common.data.remote.DataFetchResult
import kotlinx.coroutines.flow.StateFlow

interface HeatingStatsRepository {
    val heatingStatsFlow: StateFlow<DataFetchResult<List<HeatingStatsEntry>>>
    suspend fun triggerRefresh()
}