package com.laimburggasse.aquariumcontrol.feed.add

sealed interface FeedProfileAddEvent {
    data object Success: FeedProfileAddEvent
    data object HandleState: FeedProfileAddEvent
    data class SetFeedProfileName(val name: String): FeedProfileAddEvent
    data object Add: FeedProfileAddEvent
}