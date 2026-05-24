package com.laimburggasse.aquariumcontrol.summary.data.di

import com.laimburggasse.aquariumcontrol.common.di.ApplicationScope
import com.laimburggasse.aquariumcontrol.summary.data.remote.KtorSummaryDataApiService
import com.laimburggasse.aquariumcontrol.summary.data.remote.SummaryDataApiService
import com.laimburggasse.aquariumcontrol.summary.data.repository.SummaryRepositoryImpl
import com.laimburggasse.aquariumcontrol.summary.domain.repository.SummaryRepository
import io.ktor.client.HttpClient
import me.tatarka.inject.annotations.Provides

interface SummaryDataModule {
    @Provides
    @ApplicationScope
    fun provideSummaryDataApiService(
        httpClient: HttpClient
    ): SummaryDataApiService {
        return KtorSummaryDataApiService(httpClient)
    }

    @Provides
    @ApplicationScope
    fun bindSummaryRepository(
        overviewRepositoryImpl: SummaryRepositoryImpl
    ): SummaryRepository = overviewRepositoryImpl
}
