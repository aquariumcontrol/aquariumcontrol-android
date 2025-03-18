package com.laimburggasse.aquariumcontrol.heating

import java.sql.Date

data class HeatingStatsEntry(
    val date: Date,
    val energy: Double,
    val ambientTemperatureAverage: Double,
    val waterTemperatureAverage: Double,
    val heatingControlRuntime: Int
)
