package com.laimburggasse.aquariumcontrol.feed.data.di

import com.laimburggasse.aquariumcontrol.feed.data.FeedHistoryRepositoryImpl
import com.laimburggasse.aquariumcontrol.feed.data.FeedProfilesRepositoryImpl
import com.laimburggasse.aquariumcontrol.feed.data.FeedScheduleRepositoryImpl
import com.laimburggasse.aquariumcontrol.feed.domain.repository.FeedHistoryRepository
import com.laimburggasse.aquariumcontrol.feed.domain.repository.FeedProfilesRepository
import com.laimburggasse.aquariumcontrol.feed.domain.repository.FeedScheduleRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Suppress("unused", "kotlin:S1144")
@Module
@InstallIn(SingletonComponent::class)
abstract class FeedDataModule {

    @Binds
    @Singleton
    abstract fun bindFeedProfilesRepository(
        feedProfilesRepositoryImpl: FeedProfilesRepositoryImpl
    ): FeedProfilesRepository

    @Binds
    @Singleton
    abstract fun bindFeedHistoryRepository(
        feedHistoryRepositoryImpl: FeedHistoryRepositoryImpl
    ): FeedHistoryRepository

    @Binds
    @Singleton
    abstract fun bindFeedScheduleRepository(
        feedScheduleRepositoryImpl: FeedScheduleRepositoryImpl
    ): FeedScheduleRepository

}