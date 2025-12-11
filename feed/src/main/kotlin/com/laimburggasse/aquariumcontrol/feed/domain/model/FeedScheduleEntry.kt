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
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.Locale

data class FeedScheduleEntry (
    var timestamp: Timestamp,
    val profileId: Int,
    val profileName: String,
    val isDaily: Boolean
) {
    fun getTimestampString(timestampToBeFormatted: Timestamp): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        return dateFormat.format(timestampToBeFormatted)
    }

    fun getApiParameters(): Map<String, String> {
        val params: MutableMap<String, String> = mutableMapOf()
        params[WebApiParams.PROFILE_ID] = profileId.toString()
        params[WebApiParams.IS_DAILY] = if (isDaily) "1" else "0"
        params[WebApiParams.TIMESTAMP] = getTimestampString(
            timestampToBeFormatted = timestamp
        ).replace(oldValue = " ", newValue = "T")
        return params
    }

    fun getApiParametersForUpdating(timestampOld: Timestamp): Map<String, String> {
        val params: MutableMap<String, String> = mutableMapOf()
        params[WebApiParams.PROFILE_ID] = profileId.toString()
        params[WebApiParams.IS_DAILY] = if (isDaily) "1" else "0"
        params[WebApiParams.TIMESTAMP] = getTimestampString(
            timestampToBeFormatted = timestampOld
        ).replace(oldValue = " ", newValue = "T")
        params[WebApiParams.TIMESTAMP_NEW] = getTimestampString(
            timestampToBeFormatted = timestamp
        ).replace(oldValue = " ", newValue = "T")
        return params
    }
}