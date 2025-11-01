package com.laimburggasse.aquariumcontrol.refill.history

sealed interface RefillHistoryEvent {
    object Refresh: RefillHistoryEvent
    data class SetRange(val showFullWeek: Boolean): RefillHistoryEvent
}