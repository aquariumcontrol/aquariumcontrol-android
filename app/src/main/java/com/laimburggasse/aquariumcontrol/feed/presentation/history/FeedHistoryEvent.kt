package com.laimburggasse.aquariumcontrol.feed.presentation.history

sealed interface FeedHistoryEvent {
    object Refresh : FeedHistoryEvent
    data class SetRange(val showFullWeek: Boolean) : FeedHistoryEvent
}