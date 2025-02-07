package com.example.aquariumcontroller.timedata

import java.sql.Timestamp

data class TimeDataSample(
    val timestamp: Timestamp,
    val temperature: Double,
    val temperatureFiltered: Double,
    val pH: Double,
    val pHFiltered: Double,
    val conductivity: Double,
    val conductivityFiltered: Double,
    val refillInProgress: Int,
    val mainTankLevelSwitchStatus: Int,
    val surfaceVentilation: Int,
    val ambientTemperature: Double,
    val ambientHumidity: Double,
    val heater: Int
) {
    fun timeSeconds(): Long {
        return timestamp.time / 1000
    }
}