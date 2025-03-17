package com.laimburggasse.aquariumcontroller.timedata

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.Locale

@Serializable
data class TimeDataStringSample(
    // This class is used to represent a time data sample read from API with timestamp as String
    @SerialName("Timestamp")
    var timestamp: String = "",

    @SerialName("Temperature")
    val temperature: Double = 0.0,

    @SerialName("TemperatureFiltered")
    val temperatureFiltered: Double = 0.0,

    @SerialName("pH")
    val pH: Double = 0.0,

    @SerialName("pHFiltered")
    val pHFiltered: Double = 0.0,

    @SerialName("Conductivity")
    val conductivity: Double = 0.0,

    @SerialName("ConductivityFiltered")
    val conductivityFiltered: Double = 0.0,

    @SerialName("ConductivityCompensated")
    val conductivityCompensated: Double = 0.0,

    @SerialName("RefillInProgress")
    val refillInProgress: Int = 0,

    @SerialName("MainTankLevelSwitchStatus")
    val mainTankLevelSwitchStatus: Int = 0,

    @SerialName("SurfaceVentilation")
    val surfaceVentilation: Int = 0,

    @SerialName("AmbTemp")
    val ambientTemperature: Double = 0.0,

    @SerialName("AmbHum")
    val ambientHumidity: Double = 0.0,

    @SerialName("Heater")
    val heater: Int = 0,

    @SerialName("RefillTankLevel")
    val refillTankLevel: Double = 0.0,

    @SerialName("RefillTankLevelFiltered")
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
            heater = heater)
    }
}