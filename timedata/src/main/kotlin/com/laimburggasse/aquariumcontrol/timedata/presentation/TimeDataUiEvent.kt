package com.laimburggasse.aquariumcontrol.timedata.presentation

sealed interface TimeDataUiEvent {
    object Refresh: TimeDataUiEvent
}