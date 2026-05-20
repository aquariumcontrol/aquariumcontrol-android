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

package com.laimburggasse.aquariumcontrol.schedule.data.remote

import android.os.Build
import androidx.annotation.RequiresApi
import com.google.firebase.Firebase
import com.google.firebase.crashlytics.crashlytics
import com.laimburggasse.aquariumcontrol.common.data.remote.DataFetchResult
import com.laimburggasse.aquariumcontrol.common.domain.GlobalConstants
import com.laimburggasse.aquariumcontrol.schedule.data.remote.dto.ScheduleStringEntry
import java.util.Base64

@RequiresApi(Build.VERSION_CODES.O)
class ScheduleEntryReader(
    private val apiService: ScheduleApiService,
    private val url: String,
    username: String,
    password: String
) {
    // This class reads the schedule set values from the server using a GET request
    // These are later converted into one instance of ScheduleSetVals
    private val authHeaderValue: String = "Basic " + Base64.getEncoder().encodeToString("$username:$password".toByteArray())
    suspend fun readScheduleEntryList(): DataFetchResult<List<ScheduleStringEntry>> {
        return try {
            val decodedData = apiService.getScheduleData(
                url = url, authHeader = authHeaderValue
            )

            if (decodedData.isEmpty()) {
                DataFetchResult.Success(data = emptyList())
            } else if (decodedData.size > GlobalConstants.MAX_HISTORY_LOG_ENTRIES) {
                DataFetchResult.Error(
                    exception = null,
                    errorMessage = GlobalConstants.ERR_MSG_SERVER_RESPONDED_TOO_MANY_ENTRIES
                )
            } else {
                DataFetchResult.Success(decodedData)
            }
        } catch (exception: Exception) { // recording exception in crashlytics
            Firebase.crashlytics.recordException(exception)
            // Retrofit routes JSON mapping errors and network timeouts here
            DataFetchResult.Error(
                exception = exception,
                errorMessage = GlobalConstants.ERR_MSG_UNEXPECTED_ERR_DURING_PREP + exception.message
            )
        }
    }
}