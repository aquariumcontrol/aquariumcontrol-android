package com.laimburggasse.aquariumcontrol.heating.data.di

import com.laimburggasse.aquariumcontrol.overview.data.OverviewRepositoryImpl
import com.laimburggasse.aquariumcontrol.overview.domain.repository.OverviewRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Suppress("unused", "kotlin:S1144")
@Module
@InstallIn(SingletonComponent::class)
abstract class OverviewDataModule {

    @Binds
    @Singleton
    abstract fun bindOverviewRepository(
        overviewRepositoryImpl: OverviewRepositoryImpl
    ): OverviewRepository
}