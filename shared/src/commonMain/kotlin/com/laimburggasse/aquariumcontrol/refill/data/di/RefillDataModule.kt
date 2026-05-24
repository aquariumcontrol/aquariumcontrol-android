package com.laimburggasse.aquariumcontrol.refill.data.di

import androidx.paging.ExperimentalPagingApi
import androidx.paging.*

import com.laimburggasse.aquariumcontrol.common.data.local.AquariumDatabase
import com.laimburggasse.aquariumcontrol.common.data.remote.HistoryRemoteMediator
import com.laimburggasse.aquariumcontrol.common.data.remote.WebApiUrls
import com.laimburggasse.aquariumcontrol.common.di.ApplicationScope
import com.laimburggasse.aquariumcontrol.common.domain.SessionManager
import com.laimburggasse.aquariumcontrol.refill.data.local.RefillHistoryDao
import com.laimburggasse.aquariumcontrol.refill.data.remote.KtorRefillHistoryApiService
import com.laimburggasse.aquariumcontrol.refill.data.remote.KtorRefillStateApiService
import com.laimburggasse.aquariumcontrol.refill.data.remote.RefillHistoryApiService
import com.laimburggasse.aquariumcontrol.refill.data.remote.RefillStateApiService
import com.laimburggasse.aquariumcontrol.refill.data.repository.RefillHistoryRepositoryImpl
import com.laimburggasse.aquariumcontrol.refill.data.repository.RefillRepositoryImpl
import com.laimburggasse.aquariumcontrol.refill.domain.model.RefillHistoryLogEntry
import com.laimburggasse.aquariumcontrol.refill.domain.repository.RefillHistoryRepository
import com.laimburggasse.aquariumcontrol.refill.domain.repository.RefillRepository
import io.ktor.client.HttpClient
import me.tatarka.inject.annotations.Provides

interface RefillDataModule {
    @Provides
    @ApplicationScope
    fun provideRefillStateApiService(httpClient: HttpClient): RefillStateApiService = KtorRefillStateApiService(httpClient)

    @Provides
    @ApplicationScope
    fun provideRefillHistoryApiService(httpClient: HttpClient): RefillHistoryApiService = KtorRefillHistoryApiService(httpClient)

    @Provides
    @ApplicationScope
    fun bindRefillRepository(impl: RefillRepositoryImpl): RefillRepository = impl

    @Provides
    @ApplicationScope
    fun bindRefillHistoryRepository(impl: RefillHistoryRepositoryImpl): RefillHistoryRepository = impl

    @Provides
    @ApplicationScope
    fun provideRefillHistoryDao(database: AquariumDatabase): RefillHistoryDao = database.refillHistoryDao()

    @Provides
    @ApplicationScope
    fun provideRefillHistoryRemoteMediator(
        database: AquariumDatabase,
        apiService: RefillHistoryApiService,
        sessionManager: SessionManager
    ): HistoryRemoteMediator<RefillHistoryLogEntry> {
        return HistoryRemoteMediator(
            database = database,
            dao = database.refillHistoryDao(),
            apiService = apiService,
            sessionManager = sessionManager,
            loadEventsUrlPath = WebApiUrls.REFILL_LOAD_EVENTS,
            loadFirstEventUrlPath = WebApiUrls.REFILL_LOAD_FIRST_EVENT
        )
    }

    @OptIn(ExperimentalPagingApi::class)
    @Provides
    @ApplicationScope
    fun provideRefillHistoryPager(
        database: AquariumDatabase,
        remoteMediator: HistoryRemoteMediator<RefillHistoryLogEntry>
    ): Pager<Int, RefillHistoryLogEntry> {
        return Pager(
            config = PagingConfig(pageSize = 20, prefetchDistance = 10, initialLoadSize = 40),
            remoteMediator = remoteMediator,
            pagingSourceFactory = { database.refillHistoryDao().pagingSource() }
        )
    }
}
