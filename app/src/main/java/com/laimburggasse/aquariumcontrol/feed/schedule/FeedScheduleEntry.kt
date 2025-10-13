package com.laimburggasse.aquariumcontrol.feed.schedule

import com.laimburggasse.aquariumcontrol.webapi.WebApiParams
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.Locale

data class FeedScheduleEntry (
    var timestamp: Timestamp,
    val profileName: String,
    val repeatDaily: Boolean
) {
    fun getTimestampString(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        return dateFormat.format(timestamp)
    }

    fun getApiParameters(): Map<String, String> {
        val params: MutableMap<String, String> = mutableMapOf()
        params[WebApiParams.PROFILE_NAME] = profileName
        params[WebApiParams.REPEAT_DAILY] = if (repeatDaily) "1" else "0"
        params[WebApiParams.TIMESTAMP] = getTimestampString().replace(oldValue = " ", newValue = "T")
        return params
    }
}