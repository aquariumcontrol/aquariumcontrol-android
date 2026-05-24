package com.laimburggasse.aquariumcontrol.schedule.data.remote.dto

data class ScheduleEntry(
    var scheduleType: String,
    var startTime: Float,
    var stopTime: Float,
    var isLimitationActive: Boolean,
)