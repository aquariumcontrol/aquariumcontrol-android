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

import java.util.Locale
import com.laimburggasse.aquariumcontrol.feed.data.remote.FeedScheduleEntryApiService
import com.laimburggasse.aquariumcontrol.feed.data.remote.dto.FeedScheduleEntryImport
import com.laimburggasse.aquariumcontrol.feed.domain.model.FeedScheduleEntry
import kotlinx.coroutines.delay
import java.sql.Timestamp
import java.text.SimpleDateFormat

class MockFeedScheduleEntryApiServiceImpl(
    private var mockFeedScheduleEntryImports: List<FeedScheduleEntryImport>?,
    private var mockFeedScheduleEntries: List<FeedScheduleEntry>? = mockFeedScheduleEntryImports?.map {
        it.transformToFeedScheduleEntry()
    }
): FeedScheduleEntryApiService {
    override suspend fun getFeedScheduleEntries(
        url: String,
        authHeader: String
    ): List<FeedScheduleEntryImport> {
        delay(10) // Small virtual delay to avoid conflation in flow
        return mockFeedScheduleEntryImports ?: throw Exception("API error")
    }

    fun updateMockFeedScheduleEntry(oldTimestamp: Long, updatedFeedScheduleEntry: FeedScheduleEntry) {
        // 1. Update the domain model list
        mockFeedScheduleEntries = mockFeedScheduleEntries?.toMutableList()?.apply {
            val index = indexOfFirst {
                it.profileId == updatedFeedScheduleEntry.profileId && it.timestamp == oldTimestamp
            }
            if (index != -1) {
                set(index, updatedFeedScheduleEntry)
            }
        }

        // 2. Update the Import DTO list
        mockFeedScheduleEntryImports = mockFeedScheduleEntryImports?.toMutableList()?.apply {
            // Using SimpleDateFormat to match the DTO's format: "yyyy-MM-dd HH:mm:ss"
            val dateFormat = SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss",
                Locale.US
            )
            val oldTimestampString = dateFormat.format(java.util.Date(oldTimestamp))
            val index = indexOfFirst {
                it.profileId == updatedFeedScheduleEntry.profileId && it.timestampString == oldTimestampString
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
    ): Boolean {
        return mockFeedScheduleEntryImports != null
    }

    override suspend fun addFeedScheduleEntry(
        url: String,
        authHeader: String,
        params: Map<String, String>
    ): Boolean {
        return true
    }

    override suspend fun deleteFeedScheduleEntry(
        url: String,
        authHeader: String,
        params: Map<String, String>
    ): Boolean {
        return true
    }

    fun setMockFeedScheduleEntryImports(mockFeedScheduleEntryImports: List<FeedScheduleEntryImport>?) {
        this.mockFeedScheduleEntryImports = mockFeedScheduleEntryImports
        this.mockFeedScheduleEntries = mockFeedScheduleEntryImports?.map {
            it.transformToFeedScheduleEntry()
        }
    }

}