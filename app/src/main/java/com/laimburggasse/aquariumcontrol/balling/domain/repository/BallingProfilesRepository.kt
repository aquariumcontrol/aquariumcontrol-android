package com.laimburggasse.aquariumcontrol.balling.domain.repository

import com.laimburggasse.aquariumcontrol.balling.domain.model.BallingProfile
import com.laimburggasse.aquariumcontrol.lib.data.remote.DataFetchResult
import kotlinx.coroutines.flow.Flow

interface BallingProfilesRepository {
    val ballingProfiles: Flow<DataFetchResult<List<BallingProfile>>>
    fun updateBallingProfile(ballingProfile: BallingProfile)
    suspend fun triggerRefresh()
}