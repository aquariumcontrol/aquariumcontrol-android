package com.laimburggasse.aquariumcontrol.common.data.local

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor

fun getDatabaseBuilder(ctx: Context): RoomDatabase.Builder<AquariumDatabase> {
    val appContext = ctx.applicationContext
    val dbFile = appContext.getDatabasePath("aquarium.db")
    return Room.databaseBuilder<AquariumDatabase>(
        context = appContext,
        name = dbFile.absolutePath
    )
}

