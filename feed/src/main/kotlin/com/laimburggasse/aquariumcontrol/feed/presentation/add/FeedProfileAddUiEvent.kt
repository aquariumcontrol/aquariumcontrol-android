package com.laimburggasse.aquariumcontrol.feed.presentation.add

sealed interface FeedProfileAddUiEvent {
    data object Success: FeedProfileAddUiEvent
    data object HandleState: FeedProfileAddUiEvent
    data class SetFeedProfileName(val name: String): FeedProfileAddUiEvent
    data object Add: FeedProfileAddUiEvent
}