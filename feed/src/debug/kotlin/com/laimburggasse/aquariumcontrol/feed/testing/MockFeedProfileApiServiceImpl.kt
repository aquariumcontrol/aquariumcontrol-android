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
import okhttp3.ResponseBody
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.Response

class MockFeedProfileApiServiceImpl(
    private var mockFeedProfileImports: List<FeedProfileImport>?,
    private var mockFeedProfiles: List<FeedProfile>? = mockFeedProfileImports?.map {
        FeedProfile.fromFeedProfileImport(it)
    }
): FeedProfileApiService {
    override suspend fun getFeedProfiles(
        url: String,
        authHeader: String
    ): Response<List<FeedProfileImport>> {
        return if (mockFeedProfileImports == null) {
            Response.error(500, "API error".toResponseBody())
        }
        else {
            Response.success(mockFeedProfileImports)
        }
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
    ): Response<ResponseBody> {
        return Response.success("OK".toResponseBody())
    }

    fun addMockFeedProfile(additionalFeedProfile: FeedProfile) {
        mockFeedProfiles = (mockFeedProfiles ?: emptyList()) + additionalFeedProfile
        mockFeedProfileImports = (mockFeedProfileImports ?: emptyList()) + additionalFeedProfile.toFeedProfileImport()
    }
    
    override suspend fun addFeedProfile(
        url: String,
        authHeader: String,
        params: Map<String, String>
    ): Response<ResponseBody> {
        return Response.success("OK".toResponseBody())
    }

    fun deleteMockFeedProfile(toBeDeletedFeedProfile: FeedProfile) {
        mockFeedProfiles = mockFeedProfiles?.filter { it.profileID != toBeDeletedFeedProfile.profileID }
        mockFeedProfileImports = mockFeedProfileImports?.filter {
            it.profileId != toBeDeletedFeedProfile.profileID 
        }
    }

    override suspend fun deleteFeedProfile(
        url: String,
        authHeader: String
    ): Response<ResponseBody> {
        return Response.success("OK".toResponseBody())
    }

    fun setMockFeedProfileImports(mockFeedProfileImports: List<FeedProfileImport>?) {
        this.mockFeedProfileImports = mockFeedProfileImports
        this.mockFeedProfiles = mockFeedProfileImports?.map {
            FeedProfile.fromFeedProfileImport(it)
        }
    }

    fun setMockFeedProfiles(mockFeedProfiles: List<FeedProfile>?) {
        this.mockFeedProfiles = mockFeedProfiles
        this.mockFeedProfileImports = mockFeedProfiles?.map {
            it.toFeedProfileImport()
        }
    }
}