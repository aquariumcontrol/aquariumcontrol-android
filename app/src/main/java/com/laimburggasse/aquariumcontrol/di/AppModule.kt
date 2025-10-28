package com.laimburggasse.aquariumcontrol.di

import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import com.laimburggasse.aquariumcontrol.controller.common.ControllerProfileDao
import com.laimburggasse.aquariumcontrol.controller.common.ControllerProfileDatabase
import com.laimburggasse.aquariumcontrol.SessionManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext

import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): ControllerProfileDatabase {
        return Room.databaseBuilder(
            context,
            ControllerProfileDatabase::class.java,
            "aquarium_control.db"
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
    fun provideSessionManager(
        sharedPreferences: SharedPreferences,
        dao: ControllerProfileDao
    ): SessionManager {
        return SessionManager(sharedPreferences, dao)
    }
}




