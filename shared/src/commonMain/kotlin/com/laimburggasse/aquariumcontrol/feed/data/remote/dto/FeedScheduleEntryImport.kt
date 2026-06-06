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

package com.laimburggasse.aquariumcontrol.feed.data.remote.dto

import com.laimburggasse.aquariumcontrol.common.data.remote.WebApiParams
import com.laimburggasse.aquariumcontrol.common.util.DateTimeUtil
import com.laimburggasse.aquariumcontrol.feed.domain.model.FeedScheduleEntry
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Data Transfer Object (DTO) for importing scheduled feed entries from the remote API.
 *
 * @property timestampString The raw string representation of the scheduled time.
 * @property profileId The ID of the profile to be executed.
 * @property profileName The name of the profile to be executed.
 * @property isWeekly Flag indicating if the schedule repeats weekly (reserved for future use).
 * @property isDaily Flag indicating if the schedule repeats daily.
 */
@Serializable
data class FeedScheduleEntryImport (
    @SerialName(value = WebApiParams.TIMESTAMP)
    val timestampString: String,

    @SerialName(value = WebApiParams.PROFILE_ID)
    val profileId: Int,

    @SerialName(value = WebApiParams.PROFILE_NAME)
    val profileName: String,

    @SerialName(value = WebApiParams.IS_WEEKLY)
    val isWeekly: Int = 0,

    @SerialName(value = WebApiParams.IS_DAILY)
    val isDaily: Int = 0,
) {
        /**
     * Converts this DTO into a domain-level [FeedScheduleEntry].
     *
     * @return A domain model representation of the scheduled feed entry.
     */
    fun transformToFeedScheduleEntry(): FeedScheduleEntry {

        return FeedScheduleEntry(
            timestamp = DateTimeUtil.parseApiTimestamp(timestampString),
            profileId = profileId,
            profileName = profileName,
            isDaily = isDaily == 1
        )
    }
}