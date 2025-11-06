package com.laimburggasse.aquariumcontrol.heating.data.remote.dto

import com.laimburggasse.aquariumcontrol.heating.domain.model.HeatingStatsEntry
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.sql.Date
import java.text.SimpleDateFormat
import java.util.Locale

@Serializable
data class HeatingStatsStringEntry(
    @SerialName(value = "Date")
    val dateString: String,

    @SerialName(value = "Energy")
    val energy: Double,

    @SerialName(value = "AmbientTempAverage")
    val ambientTemperatureAverage: Double,

    @SerialName(value = "WaterTempAverage")
    val waterTemperatureAverage: Double,

    @SerialName(value = "HeatingControlRuntime")
    val heatingControlRuntime: Int
) {
    fun transformToHeatingStatsEntry(): HeatingStatsEntry {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val parsedTimestamp = dateFormat.parse(dateString)
        val date = Date(parsedTimestamp!!.time)

        return HeatingStatsEntry(
            date,
            energy,
            ambientTemperatureAverage,
            waterTemperatureAverage,
            heatingControlRuntime
        )
    }
}