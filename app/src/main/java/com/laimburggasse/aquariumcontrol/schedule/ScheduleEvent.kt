package com.laimburggasse.aquariumcontrol.schedule

sealed interface ScheduleEvent {
    object Update: ScheduleEvent
    object Reset: ScheduleEvent
    data class ChangeTime(
        val scheduleType: ScheduleType,
        val timeType: TimeType,
        val newTime: Float
    ): ScheduleEvent
    data class ChangeLimitationActive(
        val scheduleType: ScheduleType,
        val isActive: Boolean
    ): ScheduleEvent
}