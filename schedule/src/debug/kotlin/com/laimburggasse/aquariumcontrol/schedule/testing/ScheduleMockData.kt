package com.laimburggasse.aquariumcontrol.schedule.testing

import com.laimburggasse.aquariumcontrol.schedule.domain.model.ScheduleEntries
import com.laimburggasse.aquariumcontrol.schedule.presentation.ScheduleUiState

object ScheduleMockData {
    val mockScheduleEntries = ScheduleEntries(
        ballingStartTime = 2F,
        ballingStopTime = 22F,
        ballingLimitationActive = false,
        ballingScheduleLoaded = true,
        refillStartTime = 3F,
        refillStopTime = 21F,
        refillLimitationActive = true,
        refillScheduleLoaded = true,
        ventilationStartTime = 4F,
        ventilationStopTime = 20F,
        ventilationLimitationActive = false,
        ventilationScheduleLoaded = true,
        heatingStartTime = 5F,
        heatingStopTime = 19F,
        heatingLimitationActive = true,
        heatingScheduleLoaded = true
    )
    val mockScheduleUiStateSuccess = ScheduleUiState.Success(
        scheduleEntries = mockScheduleEntries
    )
    val mockScheduleUiStateError = ScheduleUiState.Error(
        errorMessage = "Mock Error loading data"
    )
}