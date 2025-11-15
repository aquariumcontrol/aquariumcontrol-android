package com.laimburggasse.aquariumcontrol.heating.data.di

import com.laimburggasse.aquariumcontrol.heating.data.HeatingSetValsRepositoryImpl
import com.laimburggasse.aquariumcontrol.heating.data.HeatingStatsRepositoryImpl
import com.laimburggasse.aquariumcontrol.heating.domain.repository.HeatingSetValsRepository
import com.laimburggasse.aquariumcontrol.heating.domain.repository.HeatingStatsRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Suppress("unused", "kotlin:S1144")
@Module
@InstallIn(SingletonComponent::class)
abstract class HeatingDataModule {

    @Binds
    @Singleton
    abstract fun bindHeatingSetValsRepository(
        heatingSetValsRepositoryImpl: HeatingSetValsRepositoryImpl
    ): HeatingSetValsRepository

    @Binds
    @Singleton
    abstract fun bindHeatingStatsRepository(
        heatingStatsRepositoryImpl: HeatingStatsRepositoryImpl
    ): HeatingStatsRepository
}