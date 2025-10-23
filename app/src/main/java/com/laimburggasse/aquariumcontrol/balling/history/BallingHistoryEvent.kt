package com.laimburggasse.aquariumcontrol.balling.history

sealed interface BallingHistoryEvent {
    data class Refresh(val showFullWeek: Boolean) : BallingHistoryEvent
}
