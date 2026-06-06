package com.laimburggasse.aquariumcontrol.schedule.data.remote.dto

import com.laimburggasse.aquariumcontrol.common.data.remote.WebApiParams
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.datetime.LocalTime

/**
 * Data Transfer Object (DTO) for parsing schedule entries from the remote API.
 * Uses string types for times as received from the server.
 *
 * @property scheduleType The raw identifier for the actuator category.
 * @property startTime The raw start time string (e.g., "08:00:00").
 * @property stopTime The raw stop time string (e.g., "22:00:00").
 * @property isLimitationActive Integer flag (0 or 1) indicating if the limitation is enabled.
 */
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

    /**
     * Converts this DTO into a domain-level [ScheduleEntry].
     * Parses the time strings into decimal float values.
     *
     * @return A domain model representation of the schedule entry.
     */
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
