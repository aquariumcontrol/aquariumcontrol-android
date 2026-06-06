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

package com.laimburggasse.aquariumcontrol.timedata.data.remote



import com.laimburggasse.aquariumcontrol.common.util.Logger

import com.laimburggasse.aquariumcontrol.common.data.remote.DataFetchResult
import com.laimburggasse.aquariumcontrol.common.domain.GlobalConstants
import com.laimburggasse.aquariumcontrol.timedata.data.remote.dto.TimeDataStringSample
import com.laimburggasse.aquariumcontrol.common.util.Base64


/**
 * Reader utility responsible for fetching historical telemetry samples from the remote API.
 *
 * @property logger Utility for recording exceptions.
 * @property apiService The network service for telemetry data.
 * @property url The fully qualified endpoint URL.
 * @param username Authentication username.
 * @param password Authentication password.
 */
class TimeDataReader (
    private val logger: Logger,
    private val apiService: TimeDataApiService,
    private val url: String,
    username: String,
    password: String
) {
    /** The pre-calculated Basic Authentication header value. */
    private val authHeaderValue: String = "Basic " + Base64.encode("$username:$password".encodeToByteArray())

    /**
     * Executes the network request to fetch the telemetry time series.
     * Validates that the response size is within allowed limits.
     *
     * @return A [DataFetchResult] containing the list of parsed telemetry DTOs.
     */
    suspend fun readTimeDataSeries(): DataFetchResult<List<TimeDataStringSample>> {
        return try {
            val decodedData = apiService.getTimeData(
                url = url, authHeader = authHeaderValue
            )

            if (decodedData.isEmpty()) {
                DataFetchResult.Success(data = emptyList())
            } else if (decodedData.size > GlobalConstants.MAX_TIME_DATA_SAMPLES) {
                DataFetchResult.Error(
                    exception = null,
                    errorMessage = GlobalConstants.ERR_MSG_SERVER_RESPONDED_TOO_MANY_ENTRIES
                )
            } else {
                DataFetchResult.Success(decodedData)
            }
        } catch (exception: Exception) { // recording exception in crashlytics
            logger.recordException(exception)
            DataFetchResult.Error(
                exception = exception,
                errorMessage = GlobalConstants.ERR_MSG_UNEXPECTED_ERR_DURING_PREP + exception.message
            )
        }
    }
}