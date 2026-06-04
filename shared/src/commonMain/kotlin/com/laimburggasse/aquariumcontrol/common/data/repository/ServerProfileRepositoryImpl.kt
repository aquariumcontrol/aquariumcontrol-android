package com.laimburggasse.aquariumcontrol.common.data.repository

import com.laimburggasse.aquariumcontrol.common.data.local.ControllerProfileDao
import com.laimburggasse.aquariumcontrol.common.domain.ControllerProfile
import com.laimburggasse.aquariumcontrol.common.domain.repository.ServerProfileRepository
import kotlinx.coroutines.flow.Flow
import me.tatarka.inject.annotations.Inject
import com.laimburggasse.aquariumcontrol.common.di.ApplicationScope

@ApplicationScope
class ServerProfileRepositoryImpl @Inject constructor(
    private val dao: ControllerProfileDao
) : ServerProfileRepository {
    override val allProfiles: Flow<List<ControllerProfile>> = dao.getAllControllerProfiles()

    override suspend fun addProfile(profile: ControllerProfile): Long {
        return dao.upsertControllerProfile(profile)
    }

    override suspend fun updateProfile(profile: ControllerProfile) {
        dao.upsertControllerProfile(profile)
    }

    override suspend fun deleteProfile(profile: ControllerProfile) {
        dao.deleteControllerProfile(profile)
    }
}
