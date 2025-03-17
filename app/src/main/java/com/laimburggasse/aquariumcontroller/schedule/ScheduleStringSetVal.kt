package com.laimburggasse.aquariumcontroller.schedule

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Serializable
class ScheduleStringSetVal (
    @SerialName("scheduleType") var scheduleType: String,
    @SerialName("startTime") var startTime: String,
    @SerialName("stopTime") var stopTime: String,
    @SerialName("isActive") var isLimitationActive: Int,
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

    fun transformToSetVal(): ScheduleSetVal {
        return ScheduleSetVal(
            scheduleType = scheduleType,
            startTime = stringTimeToFloat(startTime, 0f),
            stopTime = stringTimeToFloat(stopTime, 24f),
            isLimitationActive = isLimitationActive > 0
        )
    }
}