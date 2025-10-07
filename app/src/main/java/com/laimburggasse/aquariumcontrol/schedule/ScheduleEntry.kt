package com.laimburggasse.aquariumcontrol.schedule

class ScheduleEntry(
    var scheduleType: String,
    var startTime: Float,
    var stopTime: Float,
    var isLimitationActive: Boolean,
)