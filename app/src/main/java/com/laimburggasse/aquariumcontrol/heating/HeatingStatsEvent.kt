package com.laimburggasse.aquariumcontrol.heating

sealed interface HeatingStatsEvent {
    object Refresh: HeatingStatsEvent
}