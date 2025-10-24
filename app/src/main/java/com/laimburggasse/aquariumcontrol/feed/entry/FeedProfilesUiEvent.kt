package com.laimburggasse.aquariumcontrol.feed.entry

import com.laimburggasse.aquariumcontrol.feed.common.FeedProfile

sealed interface FeedProfilesUiEvent {
    data class DeleteFeedProfile(val feedProfile: FeedProfile): FeedProfilesUiEvent
}