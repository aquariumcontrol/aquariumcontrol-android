package com.laimburggasse.aquariumcontrol.timedata.data.remote.dto

import com.laimburggasse.aquariumcontrol.common.data.remote.WebApiParams
import com.laimburggasse.aquariumcontrol.common.util.DateTimeUtil
import com.laimburggasse.aquariumcontrol.timedata.domain.model.TimeDataSample
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Data Transfer Object (DTO) for parsing real-time sensor and actuator telemetry.
 * Uses string types for timestamps as received from the remote API.
 *
 * @property timestamp The raw string representation of the sample time.
 * @property temperature The current water temperature.
 * @property temperatureFiltered The filtered (moving average) water temperature.
 * @property pH The current water pH level.
 * @property pHFiltered The filtered water pH level.
 * @property conductivity The current water conductivity (TDS).
 * @property conductivityFiltered The filtered water conductivity.
 * @property refillInProgress Integer flag (0 or 1) indicating if a refill is currently active.
 * @property mainTankLevelSwitchStatus Status of the main tank water level switch.
 * @property mainTankLevelSwitchInvalid Internal flag for sensor validity.
 * @property mainTankLevelSwitchPositionStabilized Internal flag for sensor stability.
 * @property surfaceVentilation Integer flag indicating if surface ventilation is active.
 * @property ambientTemperature The current room temperature.
 * @property ambientHumidity The current room humidity.
 * @property heater Integer flag indicating if the heater is active.
 */
@Serializable
data class TimeDataStringSample(
    // This class is used to represent a time data sample read from API with timestamp as String
    @SerialName(value = WebApiParams.TIMESTAMP)
    var timestamp: String = "",

    @SerialName(value = WebApiParams.WATER_TEMPERATURE)
    val temperature: Double = 0.0,

    @SerialName(value = WebApiParams.FILTERED_WATER_TEMPERATURE)
    val temperatureFiltered: Double = 0.0,

    @SerialName(value = WebApiParams.PH_VALUE)
    val pH: Double = 0.0,

    @SerialName(value = WebApiParams.FILTERED_PH_VALUE)
    val pHFiltered: Double = 0.0,

    @SerialName(value = WebApiParams.CONDUCTIVITY)
    val conductivity: Double = 0.0,

    @SerialName(value = WebApiParams.FILTERED_CONDUCTIVITY)
    val conductivityFiltered: Double = 0.0,

    @SerialName(value = WebApiParams.REFILL_IN_PROGRESS)
    val refillInProgress: Int = 0,

    @SerialName(value = WebApiParams.TANK_LEVEL_SWITCH_POSITION)
    val mainTankLevelSwitchStatus: Int = 0,

    @SerialName(value = WebApiParams.TANK_LEVEL_SWITCH_INVALID)
    val mainTankLevelSwitchInvalid: Int = 0,

    @SerialName(value = WebApiParams.TANK_LEVEL_SWITCH_POSITION_STABILIZED)
    val mainTankLevelSwitchPositionStabilized: Int = 0,

    @SerialName(value = WebApiParams.SURFACE_VENTILATION_STATUS)
    val surfaceVentilation: Int = 0,

    @SerialName(value = WebApiParams.AMBIENT_TEMPERATURE)
    val ambientTemperature: Double = 0.0,

    @SerialName(value = WebApiParams.AMBIENT_HUMIDITY)
    val ambientHumidity: Double = 0.0,

    @SerialName(value = WebApiParams.HEATER_STATUS)
    val heater: Int = 0,
) {
    /**
     * Converts this DTO into a domain-level [TimeDataSample].
     * Parses the string [timestamp] into epoch milliseconds.
     *
     * @return A domain model representation of the telemetry sample.
     */
    fun transformToTimeDataSample(): TimeDataSample {
        
        return TimeDataSample(
            timestamp = DateTimeUtil.parseApiTimestamp(timestamp),
            temperature = temperature,
            temperatureFiltered = temperatureFiltered,
            pH = pH,
            pHFiltered = pHFiltered,
            conductivity = conductivity,
            conductivityFiltered = conductivityFiltered,
            refillInProgress = refillInProgress,
            mainTankLevelSwitchStatus = mainTankLevelSwitchStatus,
            surfaceVentilation = surfaceVentilation,
            ambientTemperature = ambientTemperature,
            ambientHumidity = ambientHumidity,
            heater = heater
        )
    }
}