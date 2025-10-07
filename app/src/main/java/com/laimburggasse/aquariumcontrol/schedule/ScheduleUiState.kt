package com.laimburggasse.aquariumcontrol.schedule

sealed class ScheduleUiState {
    object Loading : ScheduleUiState()
    object Empty: ScheduleUiState()
    data class Success(val scheduleEntries: ScheduleEntries) : ScheduleUiState()
    data class Error(val errorMessage: String) : ScheduleUiState()
}