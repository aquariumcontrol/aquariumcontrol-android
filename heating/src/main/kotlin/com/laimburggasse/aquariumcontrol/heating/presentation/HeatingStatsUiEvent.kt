package com.laimburggasse.aquariumcontrol.heating.presentation

sealed interface HeatingStatsUiEvent {
    object Refresh: HeatingStatsUiEvent
}