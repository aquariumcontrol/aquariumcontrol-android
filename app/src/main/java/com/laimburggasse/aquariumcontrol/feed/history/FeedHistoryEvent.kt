package com.laimburggasse.aquariumcontrol.feed.history

sealed interface FeedHistoryEvent {
    object Refresh : FeedHistoryEvent
    data class SetRange(val showFullWeek: Boolean) : FeedHistoryEvent
}