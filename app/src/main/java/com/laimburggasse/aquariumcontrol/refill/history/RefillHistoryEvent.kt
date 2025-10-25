package com.laimburggasse.aquariumcontrol.refill.history

sealed interface RefillHistoryEvent {
    data class Refresh(val showFullWeek: Boolean): RefillHistoryEvent
}