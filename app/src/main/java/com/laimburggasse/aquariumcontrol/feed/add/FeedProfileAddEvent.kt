package com.laimburggasse.aquariumcontrol.feed.add

sealed interface FeedProfileAddEvent {
    data object FeedProfileAddSuccess: FeedProfileAddEvent
    data class FeedProfileAddFailure(var error: String): FeedProfileAddEvent
}