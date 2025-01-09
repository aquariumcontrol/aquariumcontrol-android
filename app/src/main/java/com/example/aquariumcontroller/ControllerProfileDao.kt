package com.example.aquariumcontroller

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.aquariumcontroller.serverprofile.ControllerProfileDatabase
import kotlinx.coroutines.flow.Flow

@androidx.room.Dao
interface ControllerProfileDao {
    @androidx.room.Upsert()
    suspend fun upsertControllerProfile(ControllerProfile: ControllerProfile): Long

    @androidx.room.Query("SELECT * FROM ${ControllerProfileDatabase.TABLE_NAME_CONTROLLER_LIST} ORDER BY isdefault DESC")
    fun getAllControllerProfiles(): Flow<List<ControllerProfile>>

    @androidx.room.Query("SELECT * FROM ${ControllerProfileDatabase.TABLE_NAME_CONTROLLER_LIST} WHERE isdefault = :isDefault LIMIT 1")
    fun getDefaultControllerProfile(isDefault: Boolean): Flow<ControllerProfile>

    @androidx.room.Delete
    suspend fun deleteControllerProfile(ControllerProfile: ControllerProfile)
}