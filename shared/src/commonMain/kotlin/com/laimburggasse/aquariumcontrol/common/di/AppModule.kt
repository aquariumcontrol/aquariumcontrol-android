package com.laimburggasse.aquariumcontrol.common.di

import com.laimburggasse.aquariumcontrol.common.data.local.AquariumDatabase
import com.laimburggasse.aquariumcontrol.common.data.local.ControllerProfileDao
import com.laimburggasse.aquariumcontrol.common.domain.SessionManager
import com.laimburggasse.aquariumcontrol.common.domain.SessionManagerImpl
import com.laimburggasse.aquariumcontrol.common.domain.repository.ServerProfileRepository
import com.laimburggasse.aquariumcontrol.common.data.repository.ServerProfileRepositoryImpl
import me.tatarka.inject.annotations.Provides
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences

interface AppModule {
    @Provides
    @ApplicationScope
    fun provideControllerProfileDao(db: AquariumDatabase): ControllerProfileDao {
        return db.controllerProfileDao()
    }

    @Provides
    @ApplicationScope
    fun provideApplicationScope(): CoroutineScope {
        return CoroutineScope(context = SupervisorJob() + Dispatchers.Default)
    }

    @Provides
    @ApplicationScope
    fun provideSessionManager(
        dataStore: DataStore<Preferences>,
        dao: ControllerProfileDao,
        externalScope: CoroutineScope,
        database: AquariumDatabase
    ): SessionManager {
        return SessionManagerImpl(dataStore, dao, externalScope, database)
    }

    @Provides
    @ApplicationScope
    fun provideServerProfileRepository(dao: ControllerProfileDao): ServerProfileRepository {
        return ServerProfileRepositoryImpl(dao)
    }
}
