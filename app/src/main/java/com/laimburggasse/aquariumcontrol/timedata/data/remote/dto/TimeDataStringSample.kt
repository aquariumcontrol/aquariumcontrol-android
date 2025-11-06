package com.laimburggasse.aquariumcontrol.timedata.data.remote.dto

import com.laimburggasse.aquariumcontrol.timedata.domain.model.TimeDataSample
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.Locale

@Serializable
data class TimeDataStringSample(
    // This class is used to represent a time data sample read from API with timestamp as String
    @SerialName(value = "Timestamp")
    var timestamp: String = "",

    @SerialName(value = "Temperature")
    val temperature: Double = 0.0,

    @SerialName(value = "TemperatureFiltered")
    val temperatureFiltered: Double = 0.0,

    @SerialName(value = "pH")
    val pH: Double = 0.0,

    @SerialName(value = "pHFiltered")
    val pHFiltered: Double = 0.0,

    @SerialName(value = "Conductivity")
    val conductivity: Double = 0.0,

    @SerialName(value = "ConductivityFiltered")
    val conductivityFiltered: Double = 0.0,

    @SerialName(value = "ConductivityCompensated")
    val conductivityCompensated: Double = 0.0,

    @SerialName(value = "RefillInProgress")
    val refillInProgress: Int = 0,

    @SerialName(value = "MainTankLevelSwitchStatus")
    val mainTankLevelSwitchStatus: Int = 0,

    @SerialName(value = "SurfaceVentilation")
    val surfaceVentilation: Int = 0,

    @SerialName(value = "AmbTemp")
    val ambientTemperature: Double = 0.0,

    @SerialName(value = "AmbHum")
    val ambientHumidity: Double = 0.0,

    @SerialName(value = "Heater")
    val heater: Int = 0,

    @SerialName(value = "RefillTankLevel")
    val refillTankLevel: Double = 0.0,

    @SerialName(value = "RefillTankLevelFiltered")
    val refillTankLevelFiltered: Double = 0.0
) {
    fun transformToTimeDataSample(): TimeDataSample {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)
        val parsedTimestamp = dateFormat.parse(timestamp)
        val timestamp = Timestamp(parsedTimestamp!!.time)

        return TimeDataSample(
            timestamp = timestamp,
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