package com.laimburggasse.aquariumcontrol.controller.common

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface ControllerProfileDao {
    // This interface defines the data access object for the controller profile database.
    @Upsert
    suspend fun upsertControllerProfile(controllerProfile: ControllerProfile): Long

    @Query("SELECT * FROM ${ControllerProfileDatabase.TABLE_NAME_CONTROLLER_LIST}")
    fun getAllControllerProfiles(): Flow<List<ControllerProfile>>

    @Query("SELECT * FROM ${ControllerProfileDatabase.TABLE_NAME_CONTROLLER_LIST} WHERE id = :id")
    suspend fun getControllerProfileById(id: Long): ControllerProfile?

    @Delete
    suspend fun deleteControllerProfile(controllerProfile: ControllerProfile)
}