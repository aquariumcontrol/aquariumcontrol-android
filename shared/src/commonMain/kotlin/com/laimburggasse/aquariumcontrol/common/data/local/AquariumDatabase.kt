package com.laimburggasse.aquariumcontrol.common.data.local

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import com.laimburggasse.aquariumcontrol.common.domain.ControllerProfile
import com.laimburggasse.aquariumcontrol.refill.domain.model.RefillHistoryLogEntry
import com.laimburggasse.aquariumcontrol.balling.domain.model.BallingHistoryLogEntry
import com.laimburggasse.aquariumcontrol.feed.domain.model.FeedHistoryLogEntry
import com.laimburggasse.aquariumcontrol.refill.data.local.RefillHistoryDao
import com.laimburggasse.aquariumcontrol.balling.data.local.BallingHistoryDao
import com.laimburggasse.aquariumcontrol.feed.data.local.FeedHistoryDao

@Database(
    entities = [
        ControllerProfile::class,
        RefillHistoryLogEntry::class,
        BallingHistoryLogEntry::class,
        FeedHistoryLogEntry::class
    ],
    version = 5,
    exportSchema = false
)
@ConstructedBy(AquariumDatabaseConstructor::class)
abstract class AquariumDatabase : RoomDatabase() {
    abstract fun controllerProfileDao(): ControllerProfileDao
    abstract fun refillHistoryDao(): RefillHistoryDao
    abstract fun ballingHistoryDao(): BallingHistoryDao
    abstract fun feedHistoryDao(): FeedHistoryDao
}


fun getRoomDatabase(
    builder: RoomDatabase.Builder<AquariumDatabase>
): AquariumDatabase {
    return builder
        .setDriver(androidx.sqlite.driver.bundled.BundledSQLiteDriver())
        .setQueryCoroutineContext(kotlinx.coroutines.Dispatchers.IO)
        .build()
}

@Suppress("NO_ACTUAL_FOR_EXPECT")
expect object AquariumDatabaseConstructor : RoomDatabaseConstructor<AquariumDatabase>
