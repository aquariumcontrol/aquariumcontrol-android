package com.laimburggasse.aquariumcontrol.common.domain.repository

import com.laimburggasse.aquariumcontrol.common.domain.ControllerProfile
import kotlinx.coroutines.flow.Flow

interface ServerProfileRepository {
    val allProfiles: Flow<List<ControllerProfile>>
    suspend fun addProfile(profile: ControllerProfile): Long
    suspend fun updateProfile(profile: ControllerProfile)
    suspend fun deleteProfile(profile: ControllerProfile)
}
