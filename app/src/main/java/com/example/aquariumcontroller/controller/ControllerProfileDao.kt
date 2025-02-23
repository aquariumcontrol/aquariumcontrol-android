package com.example.aquariumcontroller.controller

import kotlinx.coroutines.flow.Flow

@androidx.room.Dao
interface ControllerProfileDao {
    // This interface defines the data access object for the controller profile database.
    @androidx.room.Upsert
    suspend fun upsertControllerProfile(controllerProfile: ControllerProfile): Long

    @androidx.room.Query("SELECT * FROM ${ControllerProfileDatabase.TABLE_NAME_CONTROLLER_LIST}")
    fun getAllControllerProfiles(): Flow<List<ControllerProfile>>

    @androidx.room.Delete
    suspend fun deleteControllerProfile(controllerProfile: ControllerProfile)
}