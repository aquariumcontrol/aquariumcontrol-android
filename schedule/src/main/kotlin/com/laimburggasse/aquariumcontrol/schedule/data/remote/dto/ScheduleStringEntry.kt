package com.laimburggasse.aquariumcontrol.schedule.data.remote.dto

import com.laimburggasse.aquariumcontrol.common.data.remote.WebApiParams
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Serializable
data class ScheduleStringEntry (
    @SerialName(WebApiParams.SCHEDULE_TYPE) var scheduleType: String,
    @SerialName(WebApiParams.START_TIME) var startTime: String,
    @SerialName(WebApiParams.STOP_TIME) var stopTime: String,
    @SerialName(WebApiParams.IS_ACTIVE) var isLimitationActive: Int,
) {
    private fun stringTimeToFloat(timeString: String, alternativeValue: Float): Float {
        val dateFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        val parsedDate = dateFormat.parse(timeString)
        val calendar = Calendar.getInstance().apply {
            if (parsedDate != null) {
                time = parsedDate
            }
            else {
                return alternativeValue
            }
        }
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)
        val second = calendar.get(Calendar.SECOND)
        return hour + minute / 60f + second / 3600f
    }

    fun transformToSetVal(): ScheduleEntry {
        val newEntry = ScheduleEntry(
            scheduleType = scheduleType,
            startTime = stringTimeToFloat(timeString = startTime, alternativeValue = 0f),
            stopTime = stringTimeToFloat(timeString = stopTime, alternativeValue = 24f),
            isLimitationActive = isLimitationActive > 0
        )
        return newEntry
    }
}