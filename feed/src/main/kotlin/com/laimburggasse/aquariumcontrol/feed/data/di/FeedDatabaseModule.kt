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

package com.laimburggasse.aquariumcontrol.feed.data.di

import android.content.Context
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.room.Room
import com.laimburggasse.aquariumcontrol.feed.data.local.FeedHistoryDao
import com.laimburggasse.aquariumcontrol.feed.data.local.FeedHistoryDatabase
import com.laimburggasse.aquariumcontrol.feed.data.remote.FeedHistoryApiService
import com.laimburggasse.aquariumcontrol.feed.data.remote.FeedHistoryRemoteMediator
import com.laimburggasse.aquariumcontrol.feed.domain.model.FeedHistoryLogEntry
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

import com.laimburggasse.aquariumcontrol.common.domain.SessionManager

@Module
@InstallIn(SingletonComponent::class)
object FeedDatabaseModule {

    @Provides
    @Singleton
    fun provideFeedHistoryDatabase(@ApplicationContext context: Context): FeedHistoryDatabase {
        return Room.databaseBuilder(
            context,
            FeedHistoryDatabase::class.java,
            "feed_history.db"
        ).build()
    }

    @Provides
    @Singleton
    fun provideFeedHistoryDao(database: FeedHistoryDatabase): FeedHistoryDao {
        return database.feedHistoryDao()
    }

    @Provides
    @Singleton
    fun provideFeedHistoryRemoteMediator(
        database: FeedHistoryDatabase,
        apiService: FeedHistoryApiService,
        sessionManager: SessionManager
    ): FeedHistoryRemoteMediator {
        return FeedHistoryRemoteMediator(
            feedHistoryDatabase = database,
            feedHistoryApiService = apiService,
            sessionManager = sessionManager,
            url = "http://placeholder.com"
        )
    }

    @OptIn(ExperimentalPagingApi::class)
    @Provides
    @Singleton
    fun provideFeedHistoryPager(
        database: FeedHistoryDatabase,
        remoteMediator: FeedHistoryRemoteMediator
    ): Pager<Int, FeedHistoryLogEntry> {
        return Pager(
            config = PagingConfig(pageSize = 20),
            remoteMediator = remoteMediator,
            pagingSourceFactory = {
                database.feedHistoryDao().pagingSource()
            }
        )
    }
}
