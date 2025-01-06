package com.example.aquariumcontroller.serverprofile

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.aquariumcontroller.ControllerProfile
import com.example.aquariumcontroller.ControllerProfileDao

@Database(entities = [ControllerProfile::class], version = 1, exportSchema = false)
abstract class ControllerProfileDatabase : RoomDatabase() {

    abstract fun controllerProfileDao(): ControllerProfileDao

    companion object {
        private const val DB_NAME = "app_db_ControllerList"
        const val TABLE_NAME_CONTROLLER_LIST = "CONTROLLER_LIST"

        @Volatile
        private var INSTANCE: ControllerProfileDatabase? = null

        fun getDatabase(context: Context): ControllerProfileDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    ControllerProfileDatabase::class.java,
                    DB_NAME
                )
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}