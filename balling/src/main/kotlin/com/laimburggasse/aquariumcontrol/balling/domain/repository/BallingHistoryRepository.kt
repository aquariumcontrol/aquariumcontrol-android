package com.laimburggasse.aquariumcontrol.balling.domain.repository

import com.laimburggasse.aquariumcontrol.balling.domain.model.BallingHistoryLogEntry
import com.laimburggasse.aquariumcontrol.common.data.remote.DataFetchResult
import kotlinx.coroutines.flow.Flow

interface BallingHistoryRepository {
    val ballingHistoryDosingEventFlow: Flow<DataFetchResult<List<BallingHistoryLogEntry>>>
    suspend fun setHistoryTimeRange(fetchFullWeek: Boolean)
    suspend fun triggerRefresh()
}