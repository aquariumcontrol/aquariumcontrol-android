package com.laimburggasse.aquariumcontrol.feed

import com.laimburggasse.aquariumcontrol.MainViewModel

class MockFeedProfilesViewModel(
    override val feedProfilesUiState: FeedProfilesUiState,
    override val isRefreshing: Boolean,
    val feedProfiles: List<FeedProfile>
) : FeedProfilesViewModelInterface {
    override fun getFeedProfileList(): List<FeedProfile> {
        return feedProfiles
    }

    override fun loadData(mainViewModel: MainViewModel) {
        // do nothing
    }

    override fun deleteFeedProfile(feedProfile: FeedProfile) {
        // do nothing
    }

    override fun getFeedProfile(profileName: String): FeedProfile? {
        return feedProfiles.find { it.profileName == profileName }
    }
}