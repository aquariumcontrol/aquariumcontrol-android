package com.laimburggasse.aquariumcontrol.feed.add

sealed interface FeedProfileAddEvent {
    data object Success: FeedProfileAddEvent
    data class Failure(var error: String): FeedProfileAddEvent
}