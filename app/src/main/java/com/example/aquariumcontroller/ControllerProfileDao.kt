package com.example.aquariumcontroller

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.aquariumcontroller.serverprofile.ControllerProfileDatabase

@androidx.room.Dao
interface ControllerProfileDao {
    @androidx.room.Insert(onConflict = androidx.room.OnConflictStrategy.REPLACE)
    suspend fun insertControllerProfile(ControllerProfile: ControllerProfile): Long

    @androidx.room.Query("SELECT * FROM ${ControllerProfileDatabase.TABLE_NAME_CONTROLLER_LIST}")
    fun getAllControllerProfiles(): LiveData<List<ControllerProfile>>

    @androidx.room.Query("SELECT * FROM ${ControllerProfileDatabase.TABLE_NAME_CONTROLLER_LIST} WHERE isdefault = :isDefault LIMIT 1")
    fun getDefaultControllerProfile(isDefault: Boolean): LiveData<ControllerProfile>

    @androidx.room.Query("UPDATE ${ControllerProfileDatabase.TABLE_NAME_CONTROLLER_LIST} SET isdefault = :value")
    suspend fun updateDefaultFlags(value: Boolean)

    @androidx.room.Query("UPDATE ${ControllerProfileDatabase.TABLE_NAME_CONTROLLER_LIST} SET isdefault = :value WHERE id <> :id")
    suspend fun updateDefaultFlagsExcept(value: Boolean, id: Long)

    @androidx.room.Update
    suspend fun updateControllerProfile(ControllerProfile: ControllerProfile)

    @androidx.room.Delete
    suspend fun deleteControllerProfile(ControllerProfile: ControllerProfile)
}