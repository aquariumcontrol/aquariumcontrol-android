package com.laimburggasse.aquariumcontrol.heating.data.remote.dto

import com.laimburggasse.aquariumcontrol.common.data.remote.WebApiParams
import com.laimburggasse.aquariumcontrol.common.util.DateTimeUtil
import com.laimburggasse.aquariumcontrol.heating.domain.model.HeatingStatsEntry
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Data Transfer Object (DTO) for parsing heating statistics from the remote API.
 * Uses string types for timestamps as received from the server.
 *
 * @property dateString The raw date string from the API.
 * @property energy The energy consumption value.
 * @property ambientTemperatureAverage The average ambient temperature during the period.
 * @property waterTemperatureAverage The average water temperature during the period.
 * @property heatingControlRuntime The duration in seconds the heating was active.
 */
@Serializable
data class HeatingStatsStringEntry(
    @SerialName(value = WebApiParams.DATE)
    val dateString: String,

    @SerialName(value = WebApiParams.ENERGY)
    val energy: Double,

    @SerialName(value = WebApiParams.AMBIENT_TEMP_AVERAGE)
    val ambientTemperatureAverage: Double,

    @SerialName(value = WebApiParams.WATER_TEMP_AVERAGE)
    val waterTemperatureAverage: Double,

    @SerialName(value = WebApiParams.HEATING_CONTROL_RUNTIME)
    val heatingControlRuntime: Int
) {
    /**
     * Converts this DTO into a domain-level [HeatingStatsEntry].
     * Parses the [dateString] into epoch milliseconds.
     *
     * @return A domain model representation of the heating statistics.
     */
    fun transformToHeatingStatsEntry(): HeatingStatsEntry {
        
        val timestampMillis = try {
            DateTimeUtil.parseApiTimestamp(dateString)
        } catch (_: Exception) {
            0L
        }
        return HeatingStatsEntry(
            timestamp = timestampMillis,
            energy = energy,
            ambientTemperatureAverage = ambientTemperatureAverage,
            waterTemperatureAverage = waterTemperatureAverage,
            heatingControlRuntime = heatingControlRuntime
        )
    }
}