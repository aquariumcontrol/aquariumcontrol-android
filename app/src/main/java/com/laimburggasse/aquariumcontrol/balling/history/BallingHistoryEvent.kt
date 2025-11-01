package com.laimburggasse.aquariumcontrol.balling.history

sealed interface BallingHistoryEvent {
    object Refresh : BallingHistoryEvent
    data class SetRange(val showFullWeek: Boolean) : BallingHistoryEvent
}
