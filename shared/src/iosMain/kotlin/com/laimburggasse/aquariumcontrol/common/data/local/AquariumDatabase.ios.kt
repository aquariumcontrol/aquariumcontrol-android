package com.laimburggasse.aquariumcontrol.common.data.local

import androidx.room.Room
import androidx.room.RoomDatabase
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask
import kotlinx.cinterop.ExperimentalForeignApi

fun getDatabaseBuilder(): RoomDatabase.Builder<AquariumDatabase> {
    val dbFilePath = documentDirectory() + "/aquarium.db"
    return Room.databaseBuilder<AquariumDatabase>(
        name = dbFilePath
    )
}

@OptIn(ExperimentalForeignApi::class)
private fun documentDirectory(): String {
    val documentDirectory = NSFileManager.defaultManager.URLsForDirectory(
        directory = NSDocumentDirectory,
        inDomains = NSUserDomainMask,
    ).firstOrNull() as? platform.Foundation.NSURL
    val pathStr = documentDirectory?.path ?: ""
    println("DEBUG: AquariumDatabase: Document Directory: ")
    return pathStr
}
