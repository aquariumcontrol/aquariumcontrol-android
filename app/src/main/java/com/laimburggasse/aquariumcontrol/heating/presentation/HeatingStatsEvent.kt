package com.laimburggasse.aquariumcontrol.heating.presentation

sealed interface HeatingStatsEvent {
    object Refresh: HeatingStatsEvent
}