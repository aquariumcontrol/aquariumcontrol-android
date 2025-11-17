package com.laimburggasse.aquariumcontrol.overview.domain.model

data class OverviewData(
    // This class represents the data that is displayed on the overview screen.
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