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

package com.laimburggasse.aquariumcontrol.feed.data.remote

import com.laimburggasse.aquariumcontrol.common.util.Logger
import com.laimburggasse.aquariumcontrol.common.data.remote.DataFetchResult
import com.laimburggasse.aquariumcontrol.common.domain.GlobalConstants
import com.laimburggasse.aquariumcontrol.common.util.Base64
import com.laimburggasse.aquariumcontrol.feed.data.remote.dto.FeedProfileImport

/**
 * Reader class responsible for fetching feed profiles from the remote server.
 * Handles authentication and basic validation of the received data.
 *
 * @property logger A utility for recording exceptions.
 * @property apiService The Ktor service for making network requests.
 * @property url The fully qualified URL endpoint for profile retrieval.
 * @param username The username for basic authentication.
 * @param password The password for basic authentication.
 */
class FeedProfileReader (
    private val logger: Logger,
    private val apiService: FeedProfileApiService,
    private val url: String,
    username: String,
    password: String
) {
    // This class reads the Feed profiles from the server using a GET request.
    private val authHeaderValue: String = "Basic " + Base64.encode("$username:$password".encodeToByteArray())

        /**
     * Executes the network request to fetch feed profiles.
     * Decodes the raw response into a list of [FeedProfileImport] objects.
     *
     * @return A [DataFetchResult] containing the list of imported profiles or an error state.
     */
    suspend fun readFeedProfiles(): DataFetchResult<List<FeedProfileImport>> {
        return try {
            val decodedData = apiService.getFeedProfiles(
                url = url, authHeader = authHeaderValue
            )

            if (decodedData.isEmpty()) {
                DataFetchResult.Success(data = emptyList())
            } else if (decodedData.size > GlobalConstants.MAX_PROFILE_ENTRIES) {
                DataFetchResult.Error(
                    exception = null,
                    errorMessage = GlobalConstants.ERR_MSG_SERVER_RESPONDED_TOO_MANY_ENTRIES
                )
            } else {
                DataFetchResult.Success(decodedData)
            }
        } catch (e: Exception) { // recording exception in crashlytics
            logger.recordException(e)
            DataFetchResult.Error(
                exception = e,
                errorMessage = GlobalConstants.ERR_MSG_UNEXPECTED_ERR_DURING_PREP + e.message
            )
        }
    }
}
