/*
 *  Copyright 2026 Uwe Martin
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.laimburggasse.aquariumcontrol.refill.data.di

import android.content.Context
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.room.Room
import com.laimburggasse.aquariumcontrol.common.data.remote.HistoryRemoteMediator
import com.laimburggasse.aquariumcontrol.common.data.remote.WebApiUrls
import com.laimburggasse.aquariumcontrol.common.domain.SessionManager
import com.laimburggasse.aquariumcontrol.refill.data.local.RefillHistoryDao
import com.laimburggasse.aquariumcontrol.refill.data.local.RefillHistoryDatabase
import com.laimburggasse.aquariumcontrol.refill.data.remote.RefillHistoryApiService
import com.laimburggasse.aquariumcontrol.refill.domain.model.RefillHistoryLogEntry
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RefillDatabaseModule {

    @Provides
    @Singleton
    fun provideRefillHistoryDatabase(@ApplicationContext context: Context): RefillHistoryDatabase {
        return Room.databaseBuilder(
            context,
            RefillHistoryDatabase::class.java,
            "refill_history.db"
        ).fallbackToDestructiveMigration(dropAllTables = true).build()
    }

    @Provides
    @Singleton
    fun provideRefillHistoryDao(database: RefillHistoryDatabase): RefillHistoryDao {
        return database.refillHistoryDao()
    }

    @Provides
    @Singleton
    fun provideRefillHistoryRemoteMediator(
        database: RefillHistoryDatabase,
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
    @Singleton
    fun provideRefillHistoryPager(
        database: RefillHistoryDatabase,
        remoteMediator: HistoryRemoteMediator<RefillHistoryLogEntry>
    ): Pager<Int, RefillHistoryLogEntry> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                prefetchDistance = 10,
                initialLoadSize = 40
            ),
            remoteMediator = remoteMediator,
            pagingSourceFactory = {
                database.refillHistoryDao().pagingSource()
            }
        )
    }
}
