package com.laimburggasse.aquariumcontrol.schedule

enum class ScheduleType(val key: String) {
    BALLING(key = "Balling"),
    REFILL(key = "Refill"),
    VENTILATION(key = "Ventilation"),
    HEATING(key = "Heating"),
}