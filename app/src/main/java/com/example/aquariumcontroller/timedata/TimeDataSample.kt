package com.example.aquariumcontroller.timedata

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.sql.Timestamp

@Serializable
data class TimeDataSample(
    // This class is used to represent a time data sample
    @Serializable(with = TimestampSerializer::class)
    var timestamp: Timestamp = Timestamp(0L),

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

    fun timeSeconds(): Long {
        return timestamp.time / 1000
    }
}