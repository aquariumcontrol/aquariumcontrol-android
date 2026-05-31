package com.laimburggasse.aquariumcontrol.common.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import com.laimburggasse.aquariumcontrol.common.data.local.AquariumDatabase
import com.laimburggasse.aquariumcontrol.common.data.local.getDatabaseBuilder
import com.laimburggasse.aquariumcontrol.common.data.local.getRoomDatabase
import com.laimburggasse.aquariumcontrol.common.util.IosLogger
import com.laimburggasse.aquariumcontrol.common.util.Logger
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.darwin.Darwin
import me.tatarka.inject.annotations.Provides
import okio.Path.Companion.toPath
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask
import kotlinx.cinterop.ExperimentalForeignApi

interface IosPlatformModule : PlatformModule {
    @Provides
    @ApplicationScope
    override fun provideLogger(): Logger = IosLogger()

    @Provides
    @ApplicationScope
    override fun provideHttpClientEngine(): HttpClientEngine = Darwin.create()

    @Provides
    @ApplicationScope
    override fun provideDataStore(): DataStore<Preferences> {
        return PreferenceDataStoreFactory.createWithPath(
            produceFile = { (documentDirectory() + "/aquarium_control_prefs.preferences_pb").toPath() }
        )
    }

    @Provides
    @ApplicationScope
    override fun provideAquariumDatabase(): AquariumDatabase {
        return getRoomDatabase(getDatabaseBuilder())
    }

        @OptIn(ExperimentalForeignApi::class)
    private fun documentDirectory(): String {
        val documentDirectory = NSFileManager.defaultManager.URLsForDirectory(
            directory = NSDocumentDirectory,
            inDomains = NSUserDomainMask,
        ).firstOrNull() as? platform.Foundation.NSURL
        val pathStr = documentDirectory?.path ?: ""
        println("DEBUG: IosPlatformModule: Document Directory: ")
        return pathStr
    }
}
