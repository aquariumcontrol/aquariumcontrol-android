package com.laimburggasse.aquariumcontrol.common.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.laimburggasse.aquariumcontrol.common.data.local.AquariumDatabase
import com.laimburggasse.aquariumcontrol.common.util.Logger
import io.ktor.client.engine.HttpClientEngine

interface PlatformModule {
    val logger: Logger
    fun provideHttpClientEngine(): HttpClientEngine
    fun provideDataStore(): DataStore<Preferences>
    fun provideAquariumDatabase(): AquariumDatabase
}
