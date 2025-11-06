package com.laimburggasse.aquariumcontrol.refill.presentation.history

sealed interface RefillHistoryUiEvent {
    object Refresh: RefillHistoryUiEvent
    data class SetRange(val showFullWeek: Boolean): RefillHistoryUiEvent
}