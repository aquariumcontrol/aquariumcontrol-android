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

package com.laimburggasse.aquariumcontrol.balling.data.di

import android.content.Context
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.room.Room
import com.laimburggasse.aquariumcontrol.balling.data.local.BallingHistoryDao
import com.laimburggasse.aquariumcontrol.balling.data.local.BallingHistoryDatabase
import com.laimburggasse.aquariumcontrol.balling.data.remote.BallingHistoryApiService
import com.laimburggasse.aquariumcontrol.balling.domain.model.BallingHistoryLogEntry
import com.laimburggasse.aquariumcontrol.common.data.remote.HistoryRemoteMediator
import com.laimburggasse.aquariumcontrol.common.data.remote.WebApiUrls
import com.laimburggasse.aquariumcontrol.common.domain.SessionManager
import me.tatarka.inject.annotations.Provides
import com.laimburggasse.aquariumcontrol.common.di.ApplicationScope

interface BallingDatabaseModule {

    @Provides
    @ApplicationScope
    fun provideBallingHistoryDatabase(context: Context): BallingHistoryDatabase {
        return Room.databaseBuilder(
            context,
            BallingHistoryDatabase::class.java,
            "balling_history.db"
        ).fallbackToDestructiveMigration(dropAllTables = true).build()

    }

    @Provides
    @ApplicationScope
    fun provideBallingHistoryDao(database: BallingHistoryDatabase): BallingHistoryDao {
        return database.ballingHistoryDao()
    }

    @Provides
    @ApplicationScope
    fun provideBallingHistoryRemoteMediator(
        database: BallingHistoryDatabase,
        apiService: BallingHistoryApiService,
        sessionManager: SessionManager
    ): HistoryRemoteMediator<BallingHistoryLogEntry> {
        return HistoryRemoteMediator(
            database = database,
            dao = database.ballingHistoryDao(),
            apiService = apiService,
            sessionManager = sessionManager,
            loadEventsUrlPath = WebApiUrls.BALLING_LOAD_EVENTS,
            loadFirstEventUrlPath = WebApiUrls.BALLING_LOAD_FIRST_EVENT
        )
    }

    @OptIn(ExperimentalPagingApi::class)
    @Provides
    @ApplicationScope
    fun provideBallingHistoryPager(
        database: BallingHistoryDatabase,
        remoteMediator: HistoryRemoteMediator<BallingHistoryLogEntry>
    ): Pager<Int, BallingHistoryLogEntry> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                prefetchDistance = 10,
                initialLoadSize = 40
            ),
            remoteMediator = remoteMediator,
            pagingSourceFactory = {
                database.ballingHistoryDao().pagingSource()
            }
        )
    }
}
