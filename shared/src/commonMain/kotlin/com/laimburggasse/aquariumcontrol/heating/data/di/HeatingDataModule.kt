package com.laimburggasse.aquariumcontrol.heating.data.di

import com.laimburggasse.aquariumcontrol.common.di.ApplicationScope
import com.laimburggasse.aquariumcontrol.heating.data.remote.HeatingSetValsApiService
import com.laimburggasse.aquariumcontrol.heating.data.remote.HeatingStatsApiService
import com.laimburggasse.aquariumcontrol.heating.data.remote.KtorHeatingSetValsApiService
import com.laimburggasse.aquariumcontrol.heating.data.remote.KtorHeatingStatsApiService
import com.laimburggasse.aquariumcontrol.heating.data.repository.HeatingSetValsRepositoryImpl
import com.laimburggasse.aquariumcontrol.heating.data.repository.HeatingStatsRepositoryImpl
import com.laimburggasse.aquariumcontrol.heating.domain.repository.HeatingSetValsRepository
import com.laimburggasse.aquariumcontrol.heating.domain.repository.HeatingStatsRepository
import io.ktor.client.HttpClient
import me.tatarka.inject.annotations.Provides

interface HeatingDataModule {
    @Provides
    @ApplicationScope
    fun provideHeatingSetValsApiService(httpClient: HttpClient): HeatingSetValsApiService = KtorHeatingSetValsApiService(httpClient)

    @Provides
    @ApplicationScope
    fun provideHeatingStatsApiService(httpClient: HttpClient): HeatingStatsApiService = KtorHeatingStatsApiService(httpClient)

    @Provides
    @ApplicationScope
    fun bindHeatingSetValsRepository(impl: HeatingSetValsRepositoryImpl): HeatingSetValsRepository = impl

    @Provides
    @ApplicationScope
    fun bindHeatingStatsRepository(impl: HeatingStatsRepositoryImpl): HeatingStatsRepository = impl
}
