/*
 *  Copyright 2025 Uwe Martin
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
 */package com.laimburggasse.aquariumcontrol.feed.data.di

import com.laimburggasse.aquariumcontrol.feed.data.remote.FeedHistoryApiService
import com.laimburggasse.aquariumcontrol.feed.data.remote.FeedProfileApiService
import com.laimburggasse.aquariumcontrol.feed.data.remote.FeedScheduleEntryApiService
import com.laimburggasse.aquariumcontrol.feed.data.remote.KtorFeedHistoryApiService
import com.laimburggasse.aquariumcontrol.feed.data.remote.KtorFeedProfileApiService
import com.laimburggasse.aquariumcontrol.feed.data.remote.KtorFeedScheduleEntryApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object FeedNetwork {
    @Provides
    @Singleton
    fun provideFeedHistoryApiService(
        httpClient: HttpClient
    ): FeedHistoryApiService {
        return KtorFeedHistoryApiService(httpClient)
    }

    @Provides
    @Singleton
    fun provideFeedProfileApiService(
        httpClient: HttpClient
    ): FeedProfileApiService {
        return KtorFeedProfileApiService(httpClient)
    }

    @Provides
    @Singleton
    fun provideFeedScheduleEntryApiService(
        httpClient: HttpClient
    ): FeedScheduleEntryApiService {
        return KtorFeedScheduleEntryApiService(httpClient)
    }
}