package com.laimburggasse.aquariumcontrol.feed

import com.laimburggasse.aquariumcontrol.MockMainViewModel
import com.laimburggasse.aquariumcontrol.feed.common.FeedProfile
import com.laimburggasse.aquariumcontrol.feed.common.FeedProfilesRepository
import com.laimburggasse.aquariumcontrol.webapi.DataFetchResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class MockFeedProfilesRepository(
    mockFeedProfiles: DataFetchResult<List<FeedProfile>>
) : FeedProfilesRepository(mainViewModel = MockMainViewModel()) {

    private val _profilesFlow = MutableStateFlow(value = mockFeedProfiles)

    override val profilesFlow = _profilesFlow.asStateFlow()

    override suspend fun fetchFeedProfiles() {
        // do nothing
    }

    override suspend fun deleteFeedProfile(feedProfile: FeedProfile) {
        // do nothing
    }
}