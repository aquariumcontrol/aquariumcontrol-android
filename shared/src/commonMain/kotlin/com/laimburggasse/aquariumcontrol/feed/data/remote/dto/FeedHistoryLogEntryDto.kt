package com.laimburggasse.aquariumcontrol.feed.data.remote.dto

import com.laimburggasse.aquariumcontrol.common.data.remote.WebApiParams
import com.laimburggasse.aquariumcontrol.common.util.DateTimeUtil
import com.laimburggasse.aquariumcontrol.feed.domain.model.FeedHistoryLogEntry
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Data Transfer Object (DTO) for parsing feed history log entries from the remote API.
 *
 * @property timestamp The raw string representation of the log entry's time.
 * @property feederOnTime The duration the feeder was active.
 * @property profileName The name of the feed profile used.
 * @property idx The unique identifier of the feed profile.
 */
@Serializable
data class FeedHistoryLogEntryDto(
    @SerialName(value = WebApiParams.TIMESTAMP)
    val timestamp: String,
    @SerialName(value = WebApiParams.FEEDER_ON_TIME)
    val feederOnTime: Double,
    @SerialName(value = WebApiParams.PROFILE_NAME)
    val profileName: String,
    @SerialName(WebApiParams.PROFILE_ID)
    val idx: Int
) {
        /**
     * Converts this DTO into a domain-level [FeedHistoryLogEntry].
     * Parses the string timestamp into epoch milliseconds using [DateTimeUtil].
     *
     * @return A domain model representation of the feed history log entry.
     */
    fun toFeedHistoryLogEntry(): FeedHistoryLogEntry {
        
        val timestampMillis = try {
            DateTimeUtil.parseApiTimestamp(timestamp)
        } catch (_: Exception) {
            0L
        }
        return FeedHistoryLogEntry(
            timestamp = timestampMillis,
            feederOnTime = feederOnTime,
            profileName = profileName,
            idx = idx
        )
    }
}
