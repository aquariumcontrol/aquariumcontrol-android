package com.laimburggasse.aquariumcontrol.schedule.data.remote.dto

/**
 * Domain model representing a single entry in an actuator's schedule.
 *
 * @property scheduleType The identifier for the actuator type (e.g., "heating", "refill").
 * @property startTime The scheduled start time in decimal hours (e.g., 14.5 for 14:30).
 * @property stopTime The scheduled stop time in decimal hours.
 * @property isLimitationActive True if the schedule constraint should be enforced.
 */
data class ScheduleEntry(
    var scheduleType: String,
    var startTime: Float,
    var stopTime: Float,
    var isLimitationActive: Boolean,
)