package com.laimburggasse.aquariumcontrol.controller

@androidx.room.Dao
interface ControllerProfileDao {
    // This interface defines the data access object for the controller profile database.
    @androidx.room.Upsert
    suspend fun upsertControllerProfile(controllerProfile: ControllerProfile): Long

    @androidx.room.Query("SELECT * FROM ${ControllerProfileDatabase.TABLE_NAME_CONTROLLER_LIST}")
    suspend fun getAllControllerProfiles(): List<ControllerProfile>

    @androidx.room.Delete
    suspend fun deleteControllerProfile(controllerProfile: ControllerProfile)
}