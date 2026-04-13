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

package com.laimburggasse.aquariumcontrol.common.data.remote

import com.laimburggasse.aquariumcontrol.common.domain.GlobalConstants
import okhttp3.Credentials

class HistoryLogEntryReader<T>(
    private val apiService: HistoryLogApiService,
    private val url: String,
    username: String,
    password: String,
    private val decode: (String) -> List<T>
) {
    private val authHeaderValue: String = Credentials.basic(username, password)

    suspend fun readHistoryLogEvents(): DataFetchResult<List<T>> {
        return try {
            val response = apiService.getHistoryLogEvents(
                url = url, authHeader = authHeaderValue
            )

            if (response.isSuccessful) {
                // Read the raw string from the response body
                val bodyString = response.body()?.string() ?: ""

                if (bodyString.isEmpty()) {
                    return DataFetchResult.Success(emptyList())
                }

                val decodedData = decode(bodyString)

                if (decodedData.isEmpty()) {
                    return DataFetchResult.Success(data = emptyList())
                }

                if (decodedData.size > GlobalConstants.MAX_HISTORY_LOG_ENTRIES) {
                    return DataFetchResult.Error(
                        exception = null,
                        errorMessage = GlobalConstants.ERR_MSG_SERVER_RESPONDED_TOO_MANY_ENTRIES
                    )
                }

                DataFetchResult.Success(decodedData)
            } else {
                DataFetchResult.Error(
                    exception = null,
                    errorMessage = GlobalConstants.ERR_MSG_SERVER_RESPONDED_ERR_CODE + response.code()
                )
            }
        } catch (exception: Exception) {
            // Retrofit routes JSON mapping errors and network timeouts here
            DataFetchResult.Error(
                exception = exception,
                errorMessage = GlobalConstants.ERR_MSG_UNEXPECTED_ERR_DURING_PREP + exception.message
            )
        }
    }
}