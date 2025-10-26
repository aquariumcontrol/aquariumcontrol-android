package com.laimburggasse.aquariumcontrol.timedata

sealed interface TimeDataEvent {
    object Refresh: TimeDataEvent
}