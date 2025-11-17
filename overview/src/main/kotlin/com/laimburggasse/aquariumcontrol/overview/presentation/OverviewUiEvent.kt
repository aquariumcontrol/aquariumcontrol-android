package com.laimburggasse.aquariumcontrol.overview.presentation

sealed interface OverviewUiEvent {
    object Refresh : OverviewUiEvent
}