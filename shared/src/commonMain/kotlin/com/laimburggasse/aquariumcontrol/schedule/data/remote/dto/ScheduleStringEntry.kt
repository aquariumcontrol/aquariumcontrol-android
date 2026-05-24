package com.laimburggasse.aquariumcontrol.schedule.data.remote.dto

import com.laimburggasse.aquariumcontrol.common.data.remote.WebApiParams
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.datetime.LocalTime

@Serializable
data class ScheduleStringEntry (
    @SerialName(WebApiParams.SCHEDULE_TYPE) var scheduleType: String,
    @SerialName(WebApiParams.START_TIME) var startTime: String,
    @SerialName(WebApiParams.STOP_TIME) var stopTime: String,
    @SerialName(WebApiParams.IS_ACTIVE) var isLimitationActive: Int,
) {
    private fun stringTimeToFloat(timeString: String, alternativeValue: Float): Float {
        return try {
            val localTime = LocalTime.parse(timeString)
            localTime.hour + localTime.minute / 60f + localTime.second / 3600f
        } catch (e: Exception) {
            alternativeValue
        }
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
