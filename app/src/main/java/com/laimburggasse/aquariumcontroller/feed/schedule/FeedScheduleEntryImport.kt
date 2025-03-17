package com.laimburggasse.aquariumcontroller.feed.schedule

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.Locale

@Serializable
data class FeedScheduleEntryImport (
    @SerialName("TimeStamp")
    val timestampString: String,

    @SerialName("ProfileName")
    val profileName: String,

    @SerialName("IsDaily")
    val isDaily: Int = 0,
) {
    fun transformToFeedScheduleEntry(): FeedScheduleEntry {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)
        val parsedTimestamp = dateFormat.parse(timestampString)
        val timestamp = Timestamp(parsedTimestamp!!.time)

        return (FeedScheduleEntry(timestamp, profileName, isDaily == 1))
    }
}
