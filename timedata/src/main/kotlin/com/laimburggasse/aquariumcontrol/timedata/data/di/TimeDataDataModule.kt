package com.laimburggasse.aquariumcontrol.timedata.data.di

import com.laimburggasse.aquariumcontrol.timedata.data.TimeDataRepositoryImpl
import com.laimburggasse.aquariumcontrol.timedata.domain.repository.TimeDataRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Suppress("unused", "kotlin:S1144")
@Module
@InstallIn(SingletonComponent::class)
abstract class TimeDataDataModule {

    @Binds
    @Singleton
    abstract fun bindTimeDataRepository(
        timeDataRepositoryImpl: TimeDataRepositoryImpl
    ): TimeDataRepository
}