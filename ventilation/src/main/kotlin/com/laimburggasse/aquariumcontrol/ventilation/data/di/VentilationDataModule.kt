package com.laimburggasse.aquariumcontrol.ventilation.data.di

import com.laimburggasse.aquariumcontrol.ventilation.data.VentilationSetValsRepositoryImpl
import com.laimburggasse.aquariumcontrol.ventilation.domain.repository.VentilationSetValsRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Suppress("unused", "kotlin:S1144")
@Module
@InstallIn(SingletonComponent::class)
abstract class VentilationDataModule {

    @Binds
    @Singleton
    abstract fun bindVentilationSetValsRepository(
        ventilationSetValsRepositoryImpl: VentilationSetValsRepositoryImpl
    ): VentilationSetValsRepository
}