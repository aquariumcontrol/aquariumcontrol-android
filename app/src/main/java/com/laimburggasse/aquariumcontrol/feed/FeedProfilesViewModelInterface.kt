package com.laimburggasse.aquariumcontrol.feed

import com.laimburggasse.aquariumcontrol.MainViewModel

interface FeedProfilesViewModelInterface {
    val feedProfilesUiState: FeedProfilesUiState
    val isRefreshing: Boolean
    fun getFeedProfileList(): List<FeedProfile>

    fun loadData(mainViewModel: MainViewModel)

    fun deleteFeedProfile(feedProfile: FeedProfile)

    fun getFeedProfile(profileName: String): FeedProfile?
}