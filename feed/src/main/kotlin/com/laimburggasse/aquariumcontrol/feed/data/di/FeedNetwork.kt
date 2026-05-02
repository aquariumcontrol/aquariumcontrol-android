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
 */

package com.laimburggasse.aquariumcontrol.feed.data.di

import com.laimburggasse.aquariumcontrol.feed.data.remote.FeedHistoryApiService
import com.laimburggasse.aquariumcontrol.feed.data.remote.FeedProfileApiService
import com.laimburggasse.aquariumcontrol.feed.data.remote.FeedScheduleEntryApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)

object FeedNetwork {
    @Provides
    @Singleton
    fun provideFeedHistoryLogApiService(
        okHttpClient: OkHttpClient
    ): FeedHistoryApiService {
        return createService< FeedHistoryApiService>(okHttpClient)
    }

    @Provides
    @Singleton
    fun provideFeedProfileApiService(okHttpClient: OkHttpClient): FeedProfileApiService {
        val contentType = "application/json".toMediaType()
        val json = Json {
            ignoreUnknownKeys = true
            coerceInputValues = true
        }

        return Retrofit.Builder()
            // The project uses @Url (which overrides this).
            .baseUrl("https://placeholder.com/")
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory(contentType))
            .build()
            .create(FeedProfileApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideFeedScheduleEntryApiService(okHttpClient: OkHttpClient): FeedScheduleEntryApiService {
        val contentType = "application/json".toMediaType()
        val json = Json {
            ignoreUnknownKeys = true
            coerceInputValues = true
        }

        return Retrofit.Builder()
            // The project uses @Url (which overrides this).
            .baseUrl("https://placeholder.com/")
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory(contentType))
            .build()
            .create(FeedScheduleEntryApiService::class.java)
    }

    // A generic internal helper function to create a Retrofit service
    private inline fun <reified T: Any> createService(okHttpClient: OkHttpClient): T {
        val contentType = "application/json".toMediaType()
        val json = Json {
            ignoreUnknownKeys = true
            coerceInputValues = true
        }

        return Retrofit.Builder()
            .baseUrl("https://placeholder.com/")
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory(contentType))
            .build()
            .create(T::class.java)
    }
}