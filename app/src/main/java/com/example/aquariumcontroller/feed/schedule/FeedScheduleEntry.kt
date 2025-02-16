package com.example.aquariumcontroller.feed.schedule

import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.Locale

data class FeedScheduleEntry (
    val timestamp: Timestamp,
    val profileName: String,
    val isDaily: Boolean
) {
    fun getTimestampString(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        return dateFormat.format(timestamp)
    }
}