package com.laimburggasse.aquariumcontrol.balling.presentation.history

sealed interface BallingHistoryUiEvent {
    object Refresh : BallingHistoryUiEvent
    data class SetRange(val showFullWeek: Boolean) : BallingHistoryUiEvent
}
