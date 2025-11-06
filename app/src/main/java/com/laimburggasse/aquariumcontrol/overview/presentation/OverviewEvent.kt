package com.laimburggasse.aquariumcontrol.overview.presentation

sealed interface OverviewEvent {
    object Refresh : OverviewEvent
}