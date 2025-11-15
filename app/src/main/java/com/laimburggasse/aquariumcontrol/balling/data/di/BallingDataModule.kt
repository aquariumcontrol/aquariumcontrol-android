package com.laimburggasse.aquariumcontrol.balling.data.di

import com.laimburggasse.aquariumcontrol.balling.data.BallingHistoryRepositoryImpl
import com.laimburggasse.aquariumcontrol.balling.data.BallingProfilesRepositoryImpl
import com.laimburggasse.aquariumcontrol.balling.domain.repository.BallingHistoryRepository
import com.laimburggasse.aquariumcontrol.balling.domain.repository.BallingProfilesRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Suppress("unused", "kotlin:S1144")
@Module
@InstallIn(SingletonComponent::class)
abstract class BallingDataModule {

    @Binds
    @Singleton
    abstract fun bindBallingProfilesRepository(
        ballingProfilesRepositoryImpl: BallingProfilesRepositoryImpl
    ): BallingProfilesRepository

    @Binds
    @Singleton
    abstract fun bindBallingHistoryRepository(
        ballingHistoryRepositoryImpl: BallingHistoryRepositoryImpl
    ): BallingHistoryRepository
}