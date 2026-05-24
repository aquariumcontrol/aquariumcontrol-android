package com.laimburggasse.aquariumcontrol.feed.data.di

import androidx.paging.ExperimentalPagingApi
import androidx.paging.*

import com.laimburggasse.aquariumcontrol.common.data.local.AquariumDatabase
import com.laimburggasse.aquariumcontrol.common.data.remote.HistoryRemoteMediator
import com.laimburggasse.aquariumcontrol.common.data.remote.WebApiUrls
import com.laimburggasse.aquariumcontrol.common.di.ApplicationScope
import com.laimburggasse.aquariumcontrol.common.domain.SessionManager
import com.laimburggasse.aquariumcontrol.feed.data.local.FeedHistoryDao
import com.laimburggasse.aquariumcontrol.feed.data.remote.FeedHistoryApiService
import com.laimburggasse.aquariumcontrol.feed.data.remote.FeedProfileApiService
import com.laimburggasse.aquariumcontrol.feed.data.remote.FeedScheduleEntryApiService
import com.laimburggasse.aquariumcontrol.feed.data.remote.KtorFeedHistoryApiService
import com.laimburggasse.aquariumcontrol.feed.data.remote.KtorFeedProfileApiService
import com.laimburggasse.aquariumcontrol.feed.data.remote.KtorFeedScheduleEntryApiService
import com.laimburggasse.aquariumcontrol.feed.data.repository.FeedHistoryRepositoryImpl
import com.laimburggasse.aquariumcontrol.feed.data.repository.FeedProfilesRepositoryImpl
import com.laimburggasse.aquariumcontrol.feed.data.repository.FeedScheduleRepositoryImpl
import com.laimburggasse.aquariumcontrol.feed.domain.model.FeedHistoryLogEntry
import com.laimburggasse.aquariumcontrol.feed.domain.repository.FeedHistoryRepository
import com.laimburggasse.aquariumcontrol.feed.domain.repository.FeedProfilesRepository
import com.laimburggasse.aquariumcontrol.feed.domain.repository.FeedScheduleRepository
import io.ktor.client.HttpClient
import me.tatarka.inject.annotations.Provides

interface FeedDataModule {
    @Provides
    @ApplicationScope
    fun provideFeedProfileApiService(httpClient: HttpClient): FeedProfileApiService = KtorFeedProfileApiService(httpClient)

    @Provides
    @ApplicationScope
    fun provideFeedScheduleEntryApiService(httpClient: HttpClient): FeedScheduleEntryApiService = KtorFeedScheduleEntryApiService(httpClient)

    @Provides
    @ApplicationScope
    fun provideFeedHistoryApiService(httpClient: HttpClient): FeedHistoryApiService = KtorFeedHistoryApiService(httpClient)

    @Provides
    @ApplicationScope
    fun bindFeedProfilesRepository(impl: FeedProfilesRepositoryImpl): FeedProfilesRepository = impl

    @Provides
    @ApplicationScope
    fun bindFeedScheduleRepository(impl: FeedScheduleRepositoryImpl): FeedScheduleRepository = impl

    @Provides
    @ApplicationScope
    fun bindFeedHistoryRepository(impl: FeedHistoryRepositoryImpl): FeedHistoryRepository = impl

    @Provides
    @ApplicationScope
    fun provideFeedHistoryDao(database: AquariumDatabase): FeedHistoryDao = database.feedHistoryDao()

    @Provides
    @ApplicationScope
    fun provideFeedHistoryRemoteMediator(
        logger: com.laimburggasse.aquariumcontrol.common.util.Logger,
        database: AquariumDatabase,
        apiService: FeedHistoryApiService,
        sessionManager: SessionManager
    ): HistoryRemoteMediator<FeedHistoryLogEntry> {
        return HistoryRemoteMediator(
            database = database,
            dao = database.feedHistoryDao(),
            apiService = apiService,
            sessionManager = sessionManager,
            loadEventsUrlPath = WebApiUrls.FEED_LOAD_EVENTS,
            loadFirstEventUrlPath = WebApiUrls.FEED_LOAD_FIRST_EVENT
        )
    }

    @OptIn(ExperimentalPagingApi::class)
    @Provides
    @ApplicationScope
    fun provideFeedHistoryPager(
        database: AquariumDatabase,
        remoteMediator: HistoryRemoteMediator<FeedHistoryLogEntry>
    ): Pager<Int, FeedHistoryLogEntry> {
        return Pager(
            config = PagingConfig(pageSize = 20, prefetchDistance = 10, initialLoadSize = 40),
            remoteMediator = remoteMediator,
            pagingSourceFactory = { database.feedHistoryDao().pagingSource() }
        )
    }
}
