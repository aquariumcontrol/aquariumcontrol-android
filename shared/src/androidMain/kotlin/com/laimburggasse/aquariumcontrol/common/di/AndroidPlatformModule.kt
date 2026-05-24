package com.laimburggasse.aquariumcontrol.common.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import com.laimburggasse.aquariumcontrol.common.data.local.AquariumDatabase
import com.laimburggasse.aquariumcontrol.common.data.local.getDatabaseBuilder
import com.laimburggasse.aquariumcontrol.common.data.local.getRoomDatabase
import com.laimburggasse.aquariumcontrol.common.util.AndroidLogger
import com.laimburggasse.aquariumcontrol.common.util.Logger
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.android.Android
import me.tatarka.inject.annotations.Provides

interface AndroidPlatformModule : PlatformModule {
    val context: Context

    @Provides
    @ApplicationScope
    override fun provideLogger(): Logger = AndroidLogger()

    @Provides
    @ApplicationScope
    override fun provideHttpClientEngine(): HttpClientEngine = Android.create()

    @Provides
    @ApplicationScope
    override fun provideDataStore(): DataStore<Preferences> {
        return PreferenceDataStoreFactory.create(
            produceFile = { context.preferencesDataStoreFile("aquarium_control_prefs") }
        )
    }

    @Provides
    @ApplicationScope
    override fun provideAquariumDatabase(): AquariumDatabase {
        return getRoomDatabase(getDatabaseBuilder(context))
    }
}
