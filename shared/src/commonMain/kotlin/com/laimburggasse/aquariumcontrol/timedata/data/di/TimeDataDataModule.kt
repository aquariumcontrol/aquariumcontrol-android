package com.laimburggasse.aquariumcontrol.timedata.data.di

import com.laimburggasse.aquariumcontrol.common.di.ApplicationScope
import com.laimburggasse.aquariumcontrol.timedata.data.remote.KtorTimeDataApiService
import com.laimburggasse.aquariumcontrol.timedata.data.remote.TimeDataApiService
import com.laimburggasse.aquariumcontrol.timedata.data.repository.TimeDataRepositoryImpl
import com.laimburggasse.aquariumcontrol.timedata.domain.repository.TimeDataRepository
import io.ktor.client.HttpClient
import me.tatarka.inject.annotations.Provides

interface TimeDataDataModule {
    @Provides
    @ApplicationScope
    fun provideTimeDataApiService(httpClient: HttpClient): TimeDataApiService = KtorTimeDataApiService(httpClient)

    @Provides
    @ApplicationScope
    fun bindTimeDataRepository(impl: TimeDataRepositoryImpl): TimeDataRepository = impl
}
