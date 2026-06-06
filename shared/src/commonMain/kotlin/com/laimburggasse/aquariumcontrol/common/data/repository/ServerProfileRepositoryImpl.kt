package com.laimburggasse.aquariumcontrol.common.data.repository

import com.laimburggasse.aquariumcontrol.common.data.local.ControllerProfileDao
import com.laimburggasse.aquariumcontrol.common.domain.ControllerProfile
import com.laimburggasse.aquariumcontrol.common.domain.repository.ServerProfileRepository
import kotlinx.coroutines.flow.Flow
import me.tatarka.inject.annotations.Inject
import com.laimburggasse.aquariumcontrol.common.di.ApplicationScope

/**
 * Implementation of [ServerProfileRepository] handling persistence of controller profiles.
 *
 * @property dao The Room DAO for controller profiles.
 */
@ApplicationScope
class ServerProfileRepositoryImpl @Inject constructor(
    private val dao: ControllerProfileDao
) : ServerProfileRepository {
    /**
     * A flow emitting the complete list of all saved controller profiles.
     */
    override val allProfiles: Flow<List<ControllerProfile>> = dao.getAllControllerProfiles()

    /**
     * Adds a new profile to the local database.
     *
     * @param profile The [ControllerProfile] entity to save.
     * @return The unique ID generated for the new profile.
     */
    override suspend fun addProfile(profile: ControllerProfile): Long {
        return dao.upsertControllerProfile(profile)
    }

    /**
     * Updates an existing profile in the local database.
     *
     * @param profile The [ControllerProfile] entity with updated values.
     */
    override suspend fun updateProfile(profile: ControllerProfile) {
        dao.upsertControllerProfile(profile)
    }

    /**
     * Removes a profile from the local database.
     *
     * @param profile The [ControllerProfile] entity to delete.
     */
    override suspend fun deleteProfile(profile: ControllerProfile) {
        dao.deleteControllerProfile(profile)
    }
}
