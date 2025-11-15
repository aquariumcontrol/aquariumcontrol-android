package com.laimburggasse.aquariumcontrol.feed.domain.repository

import com.laimburggasse.aquariumcontrol.feed.domain.model.FeedHistoryLogEntry
import com.laimburggasse.aquariumcontrol.lib.data.remote.DataFetchResult
import kotlinx.coroutines.flow.Flow

interface FeedHistoryRepository {
    val feedHistoryEventFlow: Flow<DataFetchResult<List<FeedHistoryLogEntry>>>
    suspend fun setHistoryTimeRange(fetchFullWeek: Boolean)
    suspend fun triggerRefresh()
}