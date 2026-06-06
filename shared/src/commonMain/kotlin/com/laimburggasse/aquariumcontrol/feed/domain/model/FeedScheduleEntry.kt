/*
 * Copyright 2025 Uwe Martin
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.laimburggasse.aquariumcontrol.feed.domain.model

import com.laimburggasse.aquariumcontrol.common.data.remote.WebApiParams

/**
 * Domain model representing a single entry in the feed schedule.
 *
 * @property timestamp The scheduled execution time in epoch milliseconds.
 * @property profileId The ID of the feed profile to be executed.
 * @property profileName The name of the feed profile to be executed.
 * @property isDaily True if this event should repeat every day at the same time.
 */
data class FeedScheduleEntry (
    var timestamp: Long, // Millis since epoch
    val profileId: Int,
    val profileName: String,
    val isDaily: Boolean
) {
        /**
     * Generates API parameters for creating a new schedule entry.
     *
     * @param timestampString The formatted timestamp string for the API.
     * @return A map of query parameters.
     */
    fun getApiParameters(timestampString: String): Map<String, String> {
        val params: MutableMap<String, String> = mutableMapOf()
        params[WebApiParams.PROFILE_ID] = profileId.toString()
        params[WebApiParams.IS_DAILY] = if (isDaily) "1" else "0"
        params[WebApiParams.IS_WEEKLY] = "0"
        params[WebApiParams.TIMESTAMP] = timestampString.replace(oldValue = " ", newValue = "T")
        return params
    }

        /**
     * Generates API parameters for updating an existing schedule entry.
     *
     * @param timestampOldString The current formatted timestamp string on the server.
     * @param timestampNewString The new formatted timestamp string to be set.
     * @return A map of query parameters including the update target.
     */
    fun getApiParametersForUpdating(timestampOldString: String, timestampNewString: String): Map<String, String> {
        val params: MutableMap<String, String> = mutableMapOf()
        params[WebApiParams.PROFILE_ID] = profileId.toString()
        params[WebApiParams.IS_DAILY] = if (isDaily) "1" else "0"
        params[WebApiParams.IS_WEEKLY] = "0"
        params[WebApiParams.TIMESTAMP] = timestampOldString.replace(oldValue = " ", newValue = "T")
        params[WebApiParams.TIMESTAMP_NEW] = timestampNewString.replace(oldValue = " ", newValue = "T")
        return params
    }
}