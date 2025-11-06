package com.laimburggasse.aquariumcontrol.timedata.presentation

sealed interface TimeDataEvent {
    object Refresh: TimeDataEvent
}