package com.laimburggasse.aquariumcontrol.overview.domain.repository

import com.laimburggasse.aquariumcontrol.lib.data.remote.DataFetchResult
import com.laimburggasse.aquariumcontrol.overview.domain.model.OverviewData
import kotlinx.coroutines.flow.Flow

interface OverviewRepository {
    val overviewData: Flow<DataFetchResult<OverviewData>>
    suspend fun fetchOverviewData()
}