package com.laimburggasse.aquariumcontrol.refill.domain.repository

import com.laimburggasse.aquariumcontrol.lib.data.remote.DataFetchResult
import com.laimburggasse.aquariumcontrol.refill.domain.model.RefillHistoryLogEntry
import kotlinx.coroutines.flow.Flow

interface RefillHistoryRepository {
    val refillHistoryEventFlow: Flow<DataFetchResult<List<RefillHistoryLogEntry>>>
    suspend fun setHistoryTimeRange(fetchFullWeek: Boolean)
    suspend fun triggerRefresh()
}