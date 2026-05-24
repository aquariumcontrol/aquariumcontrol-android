package com.laimburggasse.aquariumcontrol.schedule.data.di

import com.laimburggasse.aquariumcontrol.common.di.ApplicationScope
import com.laimburggasse.aquariumcontrol.schedule.data.remote.KtorScheduleApiService
import com.laimburggasse.aquariumcontrol.schedule.data.remote.ScheduleApiService
import com.laimburggasse.aquariumcontrol.schedule.data.repository.ScheduleRepositoryImpl
import com.laimburggasse.aquariumcontrol.schedule.domain.repository.ScheduleRepository
import io.ktor.client.HttpClient
import me.tatarka.inject.annotations.Provides

interface ScheduleDataModule {
    @Provides
    @ApplicationScope
    fun provideScheduleApiService(httpClient: HttpClient): ScheduleApiService = KtorScheduleApiService(httpClient)

    @Provides
    @ApplicationScope
    fun bindScheduleRepository(impl: ScheduleRepositoryImpl): ScheduleRepository = impl
}
