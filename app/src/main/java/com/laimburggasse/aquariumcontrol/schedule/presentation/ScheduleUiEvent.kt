package com.laimburggasse.aquariumcontrol.schedule.presentation

sealed interface ScheduleUiEvent {
    object Update: ScheduleUiEvent
    object Reset: ScheduleUiEvent
    data class ChangeTime(
        val scheduleType: ScheduleType,
        val timeType: TimeType,
        val newTime: Float
    ): ScheduleUiEvent
    data class ChangeLimitationActive(
        val scheduleType: ScheduleType,
        val isActive: Boolean
    ): ScheduleUiEvent
    object Refresh: ScheduleUiEvent
}