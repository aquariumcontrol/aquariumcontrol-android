package com.laimburggasse.aquariumcontrol.heating.data.remote.dto

import com.laimburggasse.aquariumcontrol.common.data.remote.WebApiParams
import com.laimburggasse.aquariumcontrol.heating.domain.model.HeatingStatsEntry
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.sql.Date
import java.text.SimpleDateFormat
import java.util.Locale

@Serializable
data class HeatingStatsStringEntry(
    @SerialName(value = WebApiParams.DATE)
    val dateString: String,

    @SerialName(value = WebApiParams.ENERGY)
    val energy: Double,

    @SerialName(value = WebApiParams.AMBIENT_TEMP_AVERAGE)
    val ambientTemperatureAverage: Double,

    @SerialName(value = WebApiParams.WATER_TEMP_AVERAGE)
    val waterTemperatureAverage: Double,

    @SerialName(value = WebApiParams.HEATING_CONTROL_RUNTIME)
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