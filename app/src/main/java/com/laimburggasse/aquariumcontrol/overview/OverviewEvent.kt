package com.laimburggasse.aquariumcontrol.overview

sealed interface OverviewEvent {
    object Refresh : OverviewEvent
}