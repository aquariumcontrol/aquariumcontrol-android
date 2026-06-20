package com.laimburggasse.aquariumcontrol.balling.data.di

import androidx.paging.ExperimentalPagingApi
import androidx.paging.*

import com.laimburggasse.aquariumcontrol.balling.data.remote.BallingHistoryApiService
import com.laimburggasse.aquariumcontrol.balling.data.remote.BallingProfileApiService
import com.laimburggasse.aquariumcontrol.balling.data.remote.KtorBallingHistoryApiService
import com.laimburggasse.aquariumcontrol.balling.data.remote.KtorBallingProfileApiService
import com.laimburggasse.aquariumcontrol.balling.data.repository.BallingHistoryRepositoryImpl
import com.laimburggasse.aquariumcontrol.balling.data.repository.BallingProfilesRepositoryImpl
import com.laimburggasse.aquariumcontrol.balling.domain.model.BallingHistoryLogEntry
import com.laimburggasse.aquariumcontrol.balling.domain.repository.BallingHistoryRepository
import com.laimburggasse.aquariumcontrol.balling.domain.repository.BallingProfilesRepository
import com.laimburggasse.aquariumcontrol.common.data.local.AquariumDatabase
import com.laimburggasse.aquariumcontrol.common.data.remote.HistoryRemoteMediator
import com.laimburggasse.aquariumcontrol.common.data.remote.WebApiUrls
import com.laimburggasse.aquariumcontrol.common.di.ApplicationScope
import com.laimburggasse.aquariumcontrol.common.domain.SessionManager
import com.laimburggasse.aquariumcontrol.common.util.Logger
import io.ktor.client.HttpClient
import me.tatarka.inject.annotations.Provides

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences

interface BallingDataModule {
    @Provides
    @ApplicationScope
    fun provideBallingProfileApiService(httpClient: HttpClient): BallingProfileApiService = KtorBallingProfileApiService(httpClient)

    @Provides
    @ApplicationScope
    fun provideBallingHistoryApiService(httpClient: HttpClient): BallingHistoryApiService = KtorBallingHistoryApiService(httpClient)

    @Provides
    @ApplicationScope
    fun bindBallingProfilesRepository(impl: BallingProfilesRepositoryImpl): BallingProfilesRepository = impl

    @Provides
    @ApplicationScope
    fun bindBallingHistoryRepository(impl: BallingHistoryRepositoryImpl): BallingHistoryRepository = impl

    @Provides
    @ApplicationScope
    fun provideBallingHistoryRemoteMediator(
        logger: Logger,
        database: AquariumDatabase,
        apiService: BallingHistoryApiService,
        sessionManager: SessionManager,
        dataStore: DataStore<Preferences>
    ): HistoryRemoteMediator<BallingHistoryLogEntry> {
        return HistoryRemoteMediator(
            database = database,
            dao = database.ballingHistoryDao(),
            apiService = apiService,
            sessionManager = sessionManager,
            loadEventsUrlPath = WebApiUrls.BALLING_LOAD_EVENTS,
            loadFirstEventUrlPath = WebApiUrls.BALLING_LOAD_FIRST_EVENT,
            dataStore = dataStore,
            logType = "balling"
        )
    }

    @OptIn(ExperimentalPagingApi::class)
    @Provides
    @ApplicationScope
    fun provideBallingHistoryPager(
        database: AquariumDatabase,
        remoteMediator: HistoryRemoteMediator<BallingHistoryLogEntry>
    ): Pager<Int, BallingHistoryLogEntry> {
        return Pager(
            config = PagingConfig(pageSize = 20, prefetchDistance = 10, initialLoadSize = 40),
            remoteMediator = remoteMediator,
            pagingSourceFactory = { database.ballingHistoryDao().pagingSource() }
        )
    }
}
