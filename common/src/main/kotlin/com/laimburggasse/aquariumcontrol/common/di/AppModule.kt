/*
 * Copyright 2025 Uwe Martin
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.laimburggasse.aquariumcontrol.common.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import androidx.room.Room
import com.laimburggasse.aquariumcontrol.common.data.local.ControllerProfileDao
import com.laimburggasse.aquariumcontrol.common.data.local.ControllerProfileDatabase
import com.laimburggasse.aquariumcontrol.common.domain.SessionManager
import com.laimburggasse.aquariumcontrol.common.domain.SessionManagerImpl
import me.tatarka.inject.annotations.Provides
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

interface AppModule {
    @Provides
    @ApplicationScope
    fun provideAppDatabase(context: Context): ControllerProfileDatabase {
        return Room.databaseBuilder(
            context,
            klass = ControllerProfileDatabase::class.java,
            name = "controller_profiles.db"
        ).build()
    }

    @Provides
    @ApplicationScope
    fun provideControllerProfileDao(db: ControllerProfileDatabase): ControllerProfileDao {
        return db.controllerProfileDao()
    }

    @Provides
    @ApplicationScope
    fun provideDataStore(context: Context): DataStore<Preferences> {
        return PreferenceDataStoreFactory.create(
            produceFile = { context.preferencesDataStoreFile("aquarium_control_prefs") }
        )
    }

    @Provides
    @ApplicationScope
    fun provideApplicationScope(): CoroutineScope {
        return CoroutineScope(context = SupervisorJob() + Dispatchers.Default)
    }

    @Provides
    @ApplicationScope
    fun provideSessionManager(
        dataStore: DataStore<Preferences>,
        dao: ControllerProfileDao,
        externalScope: CoroutineScope
    ): SessionManager {
        return SessionManagerImpl(dataStore, dao, externalScope)
    }
}