package com.laimburggasse.aquariumcontrol.refill.data.di

import com.laimburggasse.aquariumcontrol.refill.data.RefillHistoryRepositoryImpl
import com.laimburggasse.aquariumcontrol.refill.data.RefillRepositoryImpl
import com.laimburggasse.aquariumcontrol.refill.domain.repository.RefillHistoryRepository
import com.laimburggasse.aquariumcontrol.refill.domain.repository.RefillRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Suppress("unused", "kotlin:S1144")
@Module
@InstallIn(SingletonComponent::class)
abstract class RefillDataModule {

    @Binds
    @Singleton
    abstract fun bindRefillRepository(
        refillRepositoryImpl: RefillRepositoryImpl
    ): RefillRepository

    @Binds
    @Singleton
    abstract fun bindRefillHistoryRepository(
        refillHistoryRepositoryImpl: RefillHistoryRepositoryImpl
    ): RefillHistoryRepository
}