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

package com.laimburggasse.aquariumcontrol.ventilation.data.remote

import com.laimburggasse.aquariumcontrol.common.util.Logger

import com.laimburggasse.aquariumcontrol.common.data.remote.DataFetchResult
import com.laimburggasse.aquariumcontrol.common.domain.GlobalConstants
import com.laimburggasse.aquariumcontrol.ventilation.domain.model.VentilationSetVals
import com.laimburggasse.aquariumcontrol.common.util.Base64

/**
 * Reader utility responsible for fetching ventilation temperature setpoints from the remote API.
 *
 * @property logger Utility for recording non-fatal exceptions.
 * @property apiService The network service used to execute the request.
 * @property url The fully qualified endpoint URL.
 * @param username Authentication username.
 * @param password Authentication password.
 */
class VentilationSetValsReader (
    private val logger: Logger,
    private val apiService: VentilationSetValsApiService,
    private val url: String,
    username: String,
    password: String
) {
    /** The pre-calculated Basic Authentication header value used for API requests. */
    private val authHeaderValue: String = "Basic " + Base64.encode("$username:$password".encodeToByteArray())

    /**
     * Executes the network request to fetch ventilation setpoints.
     *
     * @return A [DataFetchResult] containing the list of [VentilationSetVals] or an error state.
     */
    suspend fun readVentilationSetVals(): DataFetchResult<List<VentilationSetVals>> {
        return try {
            val decodedData = apiService.getVentilationSetVals(
                url = url, authHeader = authHeaderValue
            )

            if (decodedData.isEmpty()) {
                DataFetchResult.Success(data = emptyList())
            } else {
                DataFetchResult.Success(decodedData)
            }
        } catch (exception: Exception) { // recording exception in crashlytics
            logger.recordException(exception)
            DataFetchResult.Error(
                exception,
                errorMessage = GlobalConstants.ERR_MSG_UNEXPECTED_ERR_DURING_PREP + exception.message
            )
        }
    }
}