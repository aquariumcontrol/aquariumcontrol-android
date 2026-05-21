package com.laimburggasse.aquariumcontrol.timedata.data.remote.dto

import com.laimburggasse.aquariumcontrol.common.data.remote.WebApiParams
import com.laimburggasse.aquariumcontrol.timedata.domain.model.TimeDataSample
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.text.SimpleDateFormat
import java.util.Locale

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
    fun transformToTimeDataSample(): TimeDataSample {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)
        val parsedTimestamp = dateFormat.parse(timestamp)
        val timestampMillis = parsedTimestamp?.time ?: 0L

        return TimeDataSample(
            timestamp = timestampMillis,
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