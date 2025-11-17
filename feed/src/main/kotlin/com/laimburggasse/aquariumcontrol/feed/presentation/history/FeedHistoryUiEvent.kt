package com.laimburggasse.aquariumcontrol.feed.presentation.history

sealed interface FeedHistoryUiEvent {
    object Refresh : FeedHistoryUiEvent
    data class SetRange(val showFullWeek: Boolean) : FeedHistoryUiEvent
}