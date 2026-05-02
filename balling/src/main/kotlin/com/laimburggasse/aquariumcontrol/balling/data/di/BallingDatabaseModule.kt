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
import com.laimburggasse.aquariumcontrol.balling.data.remote.BallingHistoryRemoteMediator
import com.laimburggasse.aquariumcontrol.balling.domain.model.BallingHistoryLogEntry
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

import com.laimburggasse.aquariumcontrol.common.domain.SessionManager

@Module
@InstallIn(SingletonComponent::class)
object BallingDatabaseModule {

    @Provides
    @Singleton
    fun provideBallingHistoryDatabase(@ApplicationContext context: Context): BallingHistoryDatabase {
        return Room.databaseBuilder(
            context,
            BallingHistoryDatabase::class.java,
            "balling_history.db"
        ).fallbackToDestructiveMigration(dropAllTables = true).build()

    }

    @Provides
    @Singleton
    fun provideBallingHistoryDao(database: BallingHistoryDatabase): BallingHistoryDao {
        return database.ballingHistoryDao()
    }

    @Provides
    @Singleton
    fun provideBallingHistoryRemoteMediator(
        database: BallingHistoryDatabase,
        apiService: BallingHistoryApiService,
        sessionManager: SessionManager
    ): BallingHistoryRemoteMediator {
        return BallingHistoryRemoteMediator(
            ballingHistoryDatabase = database,
            ballingHistoryApiService = apiService,
            sessionManager = sessionManager,
        )
    }

    @OptIn(ExperimentalPagingApi::class)
    @Provides
    @Singleton
    fun provideBallingHistoryPager(
        database: BallingHistoryDatabase,
        remoteMediator: BallingHistoryRemoteMediator
    ): Pager<Int, BallingHistoryLogEntry> {
        return Pager(
            config = PagingConfig(pageSize = 20),
            remoteMediator = remoteMediator,
            pagingSourceFactory = {
                database.ballingHistoryDao().pagingSource()
            }
        )
    }
}
