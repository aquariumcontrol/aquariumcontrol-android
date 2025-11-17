package com.laimburggasse.aquariumcontrol.heating.testing

import com.laimburggasse.aquariumcontrol.heating.data.remote.dto.HeatingStatsStringEntry
import com.laimburggasse.aquariumcontrol.heating.domain.model.HeatingSetVals
import com.laimburggasse.aquariumcontrol.heating.domain.model.HeatingStatsEntry
import com.laimburggasse.aquariumcontrol.heating.presentation.HeatingSetValsUiState
import com.laimburggasse.aquariumcontrol.heating.presentation.HeatingStatsUiState

fun createMockStatsData(): List<HeatingStatsEntry> {
    val mockHeatingStatsStringEntries: List<HeatingStatsStringEntry> = listOf(
        HeatingStatsStringEntry(
            dateString = "2025-1-12",
            energy = 1988.17,
            ambientTemperatureAverage = 22.6869,
            waterTemperatureAverage = 25.4176,
            heatingControlRuntime = 36235
        ),
        HeatingStatsStringEntry(
            dateString = "2025-1-25",
            energy = 18.7222,
            ambientTemperatureAverage = 18.9717,
            waterTemperatureAverage = 25.4007,
            heatingControlRuntime = 389
        )
    )

    return mockHeatingStatsStringEntries.map {
        it.transformToHeatingStatsEntry()
    }
}

object HeatingMockData {
    val mockHeatingStatsData = createMockStatsData()
    val mockHeatingStatsUiState = HeatingStatsUiState.Success(
        heatingStats = mockHeatingStatsData
    )
    val mockHeatingSetVals = HeatingSetVals(
        heatingSwitchOffTemp = 23.5,
        heatingSwitchOnTemp = 27.5
    )
    val mockHeatingSetValsUiState = HeatingSetValsUiState.Success(
        heatingSetVals = mockHeatingSetVals
    )
}