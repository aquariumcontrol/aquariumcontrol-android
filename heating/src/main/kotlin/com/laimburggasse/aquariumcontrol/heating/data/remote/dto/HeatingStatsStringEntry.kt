package com.laimburggasse.aquariumcontrol.heating.data.remote.dto

import com.laimburggasse.aquariumcontrol.common.data.remote.WebApiParams
import com.laimburggasse.aquariumcontrol.heating.domain.model.HeatingStatsEntry
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

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
        return HeatingStatsEntry(
            date = dateString,
            energy = energy,
            ambientTemperatureAverage = ambientTemperatureAverage,
            waterTemperatureAverage = waterTemperatureAverage,
            heatingControlRuntime = heatingControlRuntime
        )
    }
}