package com.laimburggasse.aquariumcontrol.feed.common

sealed class FeedNavigationEvent {
    object ToFeedProfileEdit: FeedNavigationEvent()
    object ToFeedProfileAdd: FeedNavigationEvent()
    object ToFeedHistory24h: FeedNavigationEvent()
    object ToFeedHistory7d: FeedNavigationEvent()
    object ToFeedScheduling: FeedNavigationEvent()
    object ToFeedMain: FeedNavigationEvent()
}