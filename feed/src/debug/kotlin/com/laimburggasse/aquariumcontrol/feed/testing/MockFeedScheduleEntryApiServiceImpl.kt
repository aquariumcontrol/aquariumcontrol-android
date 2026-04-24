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

package com.laimburggasse.aquariumcontrol.feed.testing

import com.laimburggasse.aquariumcontrol.feed.data.remote.FeedScheduleEntryApiService
import com.laimburggasse.aquariumcontrol.feed.data.remote.dto.FeedScheduleEntryImport
import com.laimburggasse.aquariumcontrol.feed.domain.model.FeedScheduleEntry
import okhttp3.ResponseBody
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.Response
import kotlin.collections.plus

class MockFeedScheduleEntryApiServiceImpl(
    private var mockFeedScheduleEntryImports: List<FeedScheduleEntryImport>?,
    private var mockFeedScheduleEntries: List<FeedScheduleEntry>? = mockFeedScheduleEntryImports?.map {
        it.transformToFeedScheduleEntry()
    }
): FeedScheduleEntryApiService {
    override suspend fun getFeedScheduleEntries(
        url: String,
        authHeader: String
    ): Response<List<FeedScheduleEntryImport>> {
        return if (mockFeedScheduleEntryImports == null) {
            Response.error(500, "API error".toResponseBody())
        }
        else {
            Response.success(mockFeedScheduleEntryImports)
        }
    }

    fun updateMockFeedScheduleEntry(updatedFeedScheduleEntry: FeedScheduleEntry) {
        mockFeedScheduleEntries = mockFeedScheduleEntries?.toMutableList()?.apply {
            val index = indexOfFirst { it.timestamp == updatedFeedScheduleEntry.timestamp }
            if (index != -1) {
                set(index, updatedFeedScheduleEntry)
            }
        }

        mockFeedScheduleEntryImports = mockFeedScheduleEntryImports?.toMutableList()?.apply {
            val index = indexOfFirst {
                it.timestampString == updatedFeedScheduleEntry.timestamp.toString()
            }
            if (index != -1) {
                set(index, updatedFeedScheduleEntry.toFeedScheduleEntryImport())
            }
        }
    }

    override suspend fun updateFeedScheduleEntry(
        url: String,
        authHeader: String,
        params: Map<String, String>
    ): Response<ResponseBody> {
        return Response.success("OK".toResponseBody())
    }

    fun addMockFeedScheduleEntry(additionalFeedScheduleEntry: FeedScheduleEntry) {
        mockFeedScheduleEntries = (mockFeedScheduleEntries ?: emptyList()) + additionalFeedScheduleEntry
        mockFeedScheduleEntryImports = (mockFeedScheduleEntryImports ?:
            emptyList()) + additionalFeedScheduleEntry.toFeedScheduleEntryImport()
    }

    override suspend fun addFeedScheduleEntry(
        url: String,
        authHeader: String,
        params: Map<String, String>
    ): Response<ResponseBody> {
        return Response.success("OK".toResponseBody())
    }

    fun deleteMockFeedScheduleEntry(toBeDeletedFeedScheduleEntry: FeedScheduleEntry) {
        mockFeedScheduleEntries = mockFeedScheduleEntries?.filter {
            it.timestamp != toBeDeletedFeedScheduleEntry.timestamp
        }
        mockFeedScheduleEntryImports = mockFeedScheduleEntryImports?.filter {
            it.timestampString != toBeDeletedFeedScheduleEntry.timestamp.toString()
        }
    }

    override suspend fun deleteFeedScheduleEntry(
        url: String,
        authHeader: String,
        params: Map<String, String>
    ): Response<ResponseBody> {
        return Response.success("OK".toResponseBody())
    }

    fun setMockFeedScheduleEntryImports(mockFeedScheduleEntryImports: List<FeedScheduleEntryImport>?) {
        this.mockFeedScheduleEntryImports = mockFeedScheduleEntryImports
        this.mockFeedScheduleEntries = mockFeedScheduleEntryImports?.map {
            it.transformToFeedScheduleEntry()
        }
    }

    fun setMockFeedScheduleEntries(mockFeedScheduleEntries: List<FeedScheduleEntry>?) {
        this.mockFeedScheduleEntries = mockFeedScheduleEntries
        this.mockFeedScheduleEntryImports = mockFeedScheduleEntries?.map {
            it.toFeedScheduleEntryImport()
        }
    }
}