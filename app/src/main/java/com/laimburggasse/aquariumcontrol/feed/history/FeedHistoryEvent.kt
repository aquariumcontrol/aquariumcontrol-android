package com.laimburggasse.aquariumcontrol.feed.history

sealed interface FeedHistoryEvent {
    data class Refresh(val showFullWeek: Boolean) : FeedHistoryEvent
}