package com.laimburggasse.aquariumcontrol.feed.domain.model

import com.laimburggasse.aquariumcontrol.common.data.remote.WebApiParams
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.Locale

data class FeedScheduleEntry (
    var timestamp: Timestamp,
    val profileId: Int,
    val profileName: String,
    val repeatDaily: Boolean
) {
    fun getTimestampString(timestampToBeFormatted: Timestamp): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        return dateFormat.format(timestampToBeFormatted)
    }

    fun getApiParameters(): Map<String, String> {
        val params: MutableMap<String, String> = mutableMapOf()
        params[WebApiParams.PROFILE_ID] = profileId.toString()
        params[WebApiParams.REPEAT_DAILY] = if (repeatDaily) "1" else "0"
        params[WebApiParams.TIMESTAMP] = getTimestampString(
            timestampToBeFormatted = timestamp
        ).replace(oldValue = " ", newValue = "T")
        return params
    }

    fun getApiParametersForUpdating(timestampOld: Timestamp): Map<String, String> {
        val params: MutableMap<String, String> = mutableMapOf()
        params[WebApiParams.PROFILE_ID] = profileId.toString()
        params[WebApiParams.REPEAT_DAILY] = if (repeatDaily) "1" else "0"
        params[WebApiParams.TIMESTAMP_OLD] = getTimestampString(
            timestampToBeFormatted = timestampOld
        ).replace(oldValue = " ", newValue = "T")
        params[WebApiParams.TIMESTAMP_NEW] = getTimestampString(
            timestampToBeFormatted = timestamp
        ).replace(oldValue = " ", newValue = "T")
        return params
    }
}