package com.example.aquariumcontroller.overview

data class OverviewData(
    val timestamp: String,
    val temperatureWater: Double,
    val temperatureWaterFiltered: Double,
    val pH: Double,
    val pHFiltered: Double,
    val conductivity: Double,
    val conductivityFiltered: Double,
    val tankLevelSwitchPosition: String,
    val surfaceVentilation: String,
    val temperatureAmbient: Double,
    val humidityAmbient: Double,
    val heater: String,
)