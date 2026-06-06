package com.laimburggasse.aquariumcontrol.summary.data.remote.dto

import com.laimburggasse.aquariumcontrol.common.util.DateTimeUtil
import com.laimburggasse.aquariumcontrol.summary.domain.model.SummaryData
import kotlinx.serialization.Serializable

/**
 * Data Transfer Object (DTO) used for parsing the complete aquarium summary state from the remote API.
 *
 * @property timestamp Raw string timestamp from the server.
 * @property waterTemperature Current water temperature.
 * @property filteredWaterTemperature Moving average or filtered water temperature.
 * @property phValue Current pH level.
 * @property filteredPhValue Moving average or filtered pH level.
 * @property conductivity Current conductivity (TDS) value.
 * @property filteredConductivity Moving average or filtered conductivity.
 * @property tankLevelSwitchPosition Status string of the tank water level switch.
 * @property surfaceVentilationStatus Status string of the surface ventilation actuator.
 * @property ambientTemperature Current room temperature.
 * @property ambientHumidity Current room humidity.
 * @property heatingStatus Status string of the water heating actuator.
 */
@Serializable
data class SummaryDataDto(
    val timestamp: String,
    val waterTemperature: Double,
    val filteredWaterTemperature: Double,
    val phValue: Double,
    val filteredPhValue: Double,
    val conductivity: Double,
    val filteredConductivity: Double,
    val tankLevelSwitchPosition: String,
    val surfaceVentilationStatus: String,
    val ambientTemperature: Double,
    val ambientHumidity: Double,
    val heatingStatus: String,
) {
    /**
     * Converts this DTO into a [SummaryData] domain model.
     * Parses the string timestamp into epoch milliseconds.
     *
     * @return A domain representation of the aquarium state.
     */
    fun toSummaryData(): SummaryData {
        
        val parsedTimestamp = try {
            DateTimeUtil.parseApiTimestamp(timestamp)
        } catch (_: Exception) {
            0L
        }

        return SummaryData(
            timestamp = parsedTimestamp,
            waterTemperature = waterTemperature,
            filteredWaterTemperature = filteredWaterTemperature,
            phValue = phValue,
            filteredPhValue = filteredPhValue,
            conductivity = conductivity,
            filteredConductivity = filteredConductivity,
            tankLevelSwitchPosition = tankLevelSwitchPosition,
            surfaceVentilationStatus = surfaceVentilationStatus,
            ambientTemperature = ambientTemperature,
            ambientHumidity = ambientHumidity,
            heatingStatus = heatingStatus
        )
    }
}
