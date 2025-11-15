package com.laimburggasse.aquariumcontrol.schedule.data.di

import com.laimburggasse.aquariumcontrol.schedule.data.ScheduleRepositoryImpl
import com.laimburggasse.aquariumcontrol.schedule.domain.repository.ScheduleRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Suppress("unused", "kotlin:S1144")
@Module
@InstallIn(SingletonComponent::class)
abstract class ScheduleDataModule {

    @Binds
    @Singleton
    abstract fun bindScheduleRepository(
        scheduleRepositoryImpl: ScheduleRepositoryImpl
    ): ScheduleRepository
}