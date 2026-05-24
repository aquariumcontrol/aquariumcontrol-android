package com.laimburggasse.aquariumcontrol.ventilation.data.di

import com.laimburggasse.aquariumcontrol.common.di.ApplicationScope
import com.laimburggasse.aquariumcontrol.ventilation.data.remote.KtorVentilationSetValsApiService
import com.laimburggasse.aquariumcontrol.ventilation.data.remote.VentilationSetValsApiService
import com.laimburggasse.aquariumcontrol.ventilation.data.repository.VentilationSetValsRepositoryImpl
import com.laimburggasse.aquariumcontrol.ventilation.domain.repository.VentilationSetValsRepository
import io.ktor.client.HttpClient
import me.tatarka.inject.annotations.Provides

interface VentilationDataModule {
    @Provides
    @ApplicationScope
    fun provideVentilationSetValsApiService(httpClient: HttpClient): VentilationSetValsApiService = KtorVentilationSetValsApiService(httpClient)

    @Provides
    @ApplicationScope
    fun bindVentilationSetValsRepository(impl: VentilationSetValsRepositoryImpl): VentilationSetValsRepository = impl
}
