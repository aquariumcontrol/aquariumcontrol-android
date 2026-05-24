package com.laimburggasse.aquariumcontrol.summary.data.remote.dto

import com.laimburggasse.aquariumcontrol.summary.domain.model.SummaryData
import kotlinx.serialization.Serializable
import com.laimburggasse.aquariumcontrol.common.util.DateTimeUtil
import kotlinx.datetime.*


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
        
        val parsedTimestamp = try {
            DateTimeUtil.parseApiTimestamp(timestamp) ?: 0L
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
