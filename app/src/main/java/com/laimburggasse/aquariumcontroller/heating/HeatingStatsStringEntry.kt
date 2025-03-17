package com.laimburggasse.aquariumcontroller.heating

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.sql.Date
import java.text.SimpleDateFormat
import java.util.Locale

@Serializable
data class HeatingStatsStringEntry(
    @SerialName("Date")
    val dateString: String,

    @SerialName("Energy")
    val energy: Double,

    @SerialName("AmbientTempAverage")
    val ambientTemperatureAverage: Double,

    @SerialName("WaterTempAverage")
    val waterTemperatureAverage: Double,

    @SerialName("HeatingControlRuntime")
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
