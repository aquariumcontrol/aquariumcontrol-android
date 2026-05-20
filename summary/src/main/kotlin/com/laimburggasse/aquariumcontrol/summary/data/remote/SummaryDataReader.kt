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

package com.laimburggasse.aquariumcontrol.summary.data.remote

import com.google.firebase.Firebase
import com.google.firebase.crashlytics.crashlytics
import com.laimburggasse.aquariumcontrol.common.data.remote.DataFetchResult
import com.laimburggasse.aquariumcontrol.common.domain.GlobalConstants
import com.laimburggasse.aquariumcontrol.summary.domain.model.SummaryData
import okhttp3.Credentials

class SummaryDataReader(
    private val apiService: SummaryDataApiService,
    private val url: String,
    username: String,
    password: String
) {
    // This class reads the overview data from the server using a GET request.
    private val authHeaderValue: String = Credentials.basic(username, password)

    suspend fun readSummaryData(): DataFetchResult<SummaryData> {
        try {
            val response = apiService.getSummaryData(
                url = url, authHeader = authHeaderValue
            )
            if (response.isSuccessful) {
                val decodedData = response.body() ?: return DataFetchResult.Error(
                    exception = null,
                    errorMessage = GlobalConstants.ERR_MSG_EMPTY_RESPONSE
                )
                return DataFetchResult.Success(decodedData)
            } else {
                return DataFetchResult.Error(
                    exception = null,
                    errorMessage = GlobalConstants.ERR_MSG_SERVER_RESPONDED_ERR_CODE + response.code()
                )
            }
        } catch (e: Exception) { // recording exception in crashlytics
            Firebase.crashlytics.recordException(e)
            return DataFetchResult.Error(
                exception = e,
                errorMessage = GlobalConstants.ERR_MSG_UNEXPECTED_ERR_DURING_PREP + e.message
            )
        }
    }
}