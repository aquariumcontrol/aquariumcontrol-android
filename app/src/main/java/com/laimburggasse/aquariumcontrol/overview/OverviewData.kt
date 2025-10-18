package com.laimburggasse.aquariumcontrol.overview

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
) {
    constructor() : this(
        timestamp = "00:00:00",
        temperatureWater = 0.0,
        temperatureWaterFiltered = 0.0,
        pH = 0.0,
        pHFiltered = 0.0,
        conductivity = 0.0,
        conductivityFiltered = 0.0,
        tankLevelSwitchPosition = "Unknown",
        surfaceVentilation = "Unknown",
        temperatureAmbient = 0.0,
        humidityAmbient = 0.0,
        heater = "Unknown"
    )
}