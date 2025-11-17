package com.laimburggasse.aquariumcontrol.feed.data.remote.dto

import com.laimburggasse.aquariumcontrol.feed.domain.model.FeedScheduleEntry
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.Locale

@Serializable
data class FeedScheduleEntryImport (
    @SerialName(value = "Timestamp")
    val timestampString: String,

    @SerialName(value = "ProfileId")
    val profileId: Int,

    @SerialName(value = "ProfileName")
    val profileName: String,

    @SerialName(value = "IsDaily")
    val isDaily: Int = 0,
) {
    fun transformToFeedScheduleEntry(): FeedScheduleEntry {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)
        val parsedTimestamp = dateFormat.parse(timestampString)
        val timestamp = Timestamp(parsedTimestamp!!.time)

        return (FeedScheduleEntry(timestamp, profileId, profileName, repeatDaily = isDaily == 1))
    }
}