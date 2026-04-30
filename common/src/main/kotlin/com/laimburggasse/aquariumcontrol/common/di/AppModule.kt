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
import android.content.SharedPreferences
import androidx.room.Room
import com.laimburggasse.aquariumcontrol.common.data.local.ControllerProfileDao
import com.laimburggasse.aquariumcontrol.common.data.local.ControllerProfileDatabase
import com.laimburggasse.aquariumcontrol.common.domain.SessionManager
import com.laimburggasse.aquariumcontrol.common.domain.SessionManagerImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): ControllerProfileDatabase {
        return Room.databaseBuilder(
            context,
            klass = ControllerProfileDatabase::class.java,
            name = "controller_profiles.db"
        ).build()
    }

    @Provides
    @Singleton
    fun provideControllerProfileDao(db: ControllerProfileDatabase): ControllerProfileDao {
        return db.controllerProfileDao()
    }

    @Provides
    @Singleton
    fun provideSharedPreferences(@ApplicationContext context: Context): SharedPreferences {
        return context.getSharedPreferences("aquarium_control_prefs", Context.MODE_PRIVATE)
    }

    @Provides
    @Singleton
    @ApplicationScope
    fun provideApplicationScope(): CoroutineScope {
        return CoroutineScope(context = SupervisorJob() + Dispatchers.Default)
    }

    @Provides
    @Singleton
    fun provideSessionManager(
        sharedPreferences: SharedPreferences,
        dao: ControllerProfileDao,
        @ApplicationScope externalScope: CoroutineScope // Hilt gets this from the provider above
    ): SessionManager {
        return SessionManagerImpl(sharedPreferences, dao, externalScope)
    }
}