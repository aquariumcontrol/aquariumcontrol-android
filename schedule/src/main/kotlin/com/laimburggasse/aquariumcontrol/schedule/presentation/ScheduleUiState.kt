package com.laimburggasse.aquariumcontrol.schedule.presentation

import com.laimburggasse.aquariumcontrol.schedule.domain.model.ScheduleEntries

sealed class ScheduleUiState {
    object Loading : ScheduleUiState()
    object Empty: ScheduleUiState()
    data class Success(val scheduleEntries: ScheduleEntries) : ScheduleUiState()
    data class Error(val errorMessage: String) : ScheduleUiState()
}