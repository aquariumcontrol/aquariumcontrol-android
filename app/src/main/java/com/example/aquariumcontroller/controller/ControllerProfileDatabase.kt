package com.example.aquariumcontroller.controller

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [ControllerProfile::class], version = 1, exportSchema = false)
abstract class ControllerProfileDatabase : RoomDatabase() {
    // This class defines the database for the controller profile.

    abstract fun controllerProfileDao(): ControllerProfileDao

    companion object {
        const val TABLE_NAME_CONTROLLER_LIST = "CONTROLLER_LIST"

    }
}