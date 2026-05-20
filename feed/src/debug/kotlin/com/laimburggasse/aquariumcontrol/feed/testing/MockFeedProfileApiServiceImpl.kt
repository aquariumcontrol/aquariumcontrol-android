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

import com.laimburggasse.aquariumcontrol.feed.data.remote.FeedProfileApiService
import com.laimburggasse.aquariumcontrol.feed.data.remote.dto.FeedProfileImport
import com.laimburggasse.aquariumcontrol.feed.domain.model.FeedProfile

class MockFeedProfileApiServiceImpl(
    private var mockFeedProfileImports: List<FeedProfileImport>?,
    private var mockFeedProfiles: List<FeedProfile>? = mockFeedProfileImports?.map {
        FeedProfile.fromFeedProfileImport(it)
    }
): FeedProfileApiService {
    override suspend fun getFeedProfiles(
        url: String,
        authHeader: String
    ): List<FeedProfileImport> {
        return mockFeedProfileImports ?: throw Exception("API error")
    }

    fun updateMockFeedProfile(updatedFeedProfile: FeedProfile) {
        mockFeedProfiles = mockFeedProfiles?.toMutableList()?.apply {
            val index = indexOfFirst { it.profileID == updatedFeedProfile.profileID }
            if (index != -1) {
                set(index, updatedFeedProfile)
            }
        }

        mockFeedProfileImports = mockFeedProfileImports?.toMutableList()?.apply {
            val index = indexOfFirst { it.profileId == updatedFeedProfile.profileID }
            if (index != -1) {
                set(index, updatedFeedProfile.toFeedProfileImport())
            }
        }
    }

    override suspend fun updateFeedProfile(
        url: String,
        authHeader: String,
        params: Map<String, String>
    ): Boolean {
        return mockFeedProfileImports != null
    }

    override suspend fun addFeedProfile(
        url: String,
        authHeader: String,
        params: Map<String, String>
    ): Boolean {
        return true
    }

    override suspend fun deleteFeedProfile(
        url: String,
        authHeader: String
    ): Boolean {
        return mockFeedProfileImports != null
    }

    fun setMockFeedProfiles(mockFeedProfiles: List<FeedProfile>?) {
        this.mockFeedProfiles = mockFeedProfiles
        this.mockFeedProfileImports = mockFeedProfiles?.map {
            it.toFeedProfileImport()
        }
    }
}