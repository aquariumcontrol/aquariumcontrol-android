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
    @SerialName(value = "timestamp")
    var timestamp: String = "",

    @SerialName(value = "waterTemperature")
    val temperature: Double = 0.0,

    @SerialName(value = "filteredWaterTemperature")
    val temperatureFiltered: Double = 0.0,

    @SerialName(value = "phValue")
    val pH: Double = 0.0,

    @SerialName(value = "filteredPhValue")
    val pHFiltered: Double = 0.0,

    @SerialName(value = "conductivity")
    val conductivity: Double = 0.0,

    @SerialName(value = "filteredConductivity")
    val conductivityFiltered: Double = 0.0,

    @SerialName(value = "refillInProgress")
    val refillInProgress: Int = 0,

    @SerialName(value = "tankLevelSwitchPosition")
    val mainTankLevelSwitchStatus: Int = 0,

    @SerialName(value = "tankLevelSwitchInvalid")
    val mainTankLevelSwitchInvalid: Int = 0,

    @SerialName(value = "tankLevelSwitchPositionStabilized")
    val mainTankLevelSwitchPositionStabilized: Int = 0,

    @SerialName(value = "surfaceVentilationStatus")
    val surfaceVentilation: Int = 0,

    @SerialName(value = "ambientTemperature")
    val ambientTemperature: Double = 0.0,

    @SerialName(value = "ambientHumidity")
    val ambientHumidity: Double = 0.0,

    @SerialName(value = "heaterStatus")
    val heater: Int = 0,
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