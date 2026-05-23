package com.laimburggasse.aquariumcontrol.feed.data.remote.dto

import com.laimburggasse.aquariumcontrol.common.data.remote.WebApiParams
import com.laimburggasse.aquariumcontrol.feed.domain.model.FeedHistoryLogEntry
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.text.SimpleDateFormat
import java.util.Locale

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
    fun toFeedHistoryLogEntry(): FeedHistoryLogEntry {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)
        val timestampMillis = try {
            dateFormat.parse(timestamp)?.time ?: 0L
        } catch (e: Exception) {
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
