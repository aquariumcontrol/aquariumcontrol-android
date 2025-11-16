package com.laimburggasse.aquariumcontrol.common.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.laimburggasse.aquariumcontrol.common.domain.ControllerProfile

@Database(entities = [ControllerProfile::class], version = 4, exportSchema = false)
abstract class ControllerProfileDatabase : RoomDatabase() {
    // This class defines the database for the controller profile.

    abstract fun controllerProfileDao(): ControllerProfileDao

    companion object {
        const val TABLE_NAME_CONTROLLER_LIST = "CONTROLLER_LIST"
    }
}