package com.laimburggasse.aquariumcontrol.feed.presentation.entry

import com.laimburggasse.aquariumcontrol.feed.domain.model.FeedProfile

sealed interface FeedProfilesUiEvent {
    data class DeleteFeedProfile(val feedProfile: FeedProfile): FeedProfilesUiEvent
    object Refresh: FeedProfilesUiEvent
}