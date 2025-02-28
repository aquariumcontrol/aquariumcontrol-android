package com.example.aquariumcontroller.feed.schedule

import com.example.aquariumcontroller.webapi.WebApiParams
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.Locale

data class FeedScheduleEntry (
    var timestamp: Timestamp,
    val profileName: String,
    val isDaily: Boolean
) {
    fun getTimestampString(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        return dateFormat.format(timestamp)
    }

    fun getApiParameters(): Map<String, String> {
        val params = mutableMapOf<String, String>()
        params[WebApiParams.PROFILE_NAME] = profileName
        params[WebApiParams.REPEAT_DAILY] = if (isDaily) "1" else "0"
        params[WebApiParams.TIMESTAMP] = getTimestampString().replace(" ", "T")
        return params
    }
}