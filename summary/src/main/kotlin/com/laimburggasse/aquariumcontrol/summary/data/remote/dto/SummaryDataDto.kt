package com.laimburggasse.aquariumcontrol.summary.data.remote.dto

import com.laimburggasse.aquariumcontrol.summary.domain.model.SummaryData
import kotlinx.serialization.Serializable
import java.text.SimpleDateFormat
import java.util.Locale

@Serializable
data class SummaryDataDto(
    val timestamp: String,
    val waterTemperature: Double,
    val filteredWaterTemperature: Double,
    val phValue: Double,
    val filteredPhValue: Double,
    val conductivity: Double,
    val filteredConductivity: Double,
    val tankLevelSwitchPosition: String,
    val surfaceVentilationStatus: String,
    val ambientTemperature: Double,
    val ambientHumidity: Double,
    val heatingStatus: String,
) {
    fun toSummaryData(): SummaryData {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)
        val parsedTimestamp = try {
            dateFormat.parse(timestamp)?.time ?: 0L
        } catch (e: Exception) {
            0L
        }

        return SummaryData(
            timestamp = parsedTimestamp,
            waterTemperature = waterTemperature,
            filteredWaterTemperature = filteredWaterTemperature,
            phValue = phValue,
            filteredPhValue = filteredPhValue,
            conductivity = conductivity,
            filteredConductivity = filteredConductivity,
            tankLevelSwitchPosition = tankLevelSwitchPosition,
            surfaceVentilationStatus = surfaceVentilationStatus,
            ambientTemperature = ambientTemperature,
            ambientHumidity = ambientHumidity,
            heatingStatus = heatingStatus
        )
    }
}
