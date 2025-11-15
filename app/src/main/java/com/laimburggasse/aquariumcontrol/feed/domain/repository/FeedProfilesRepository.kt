package com.laimburggasse.aquariumcontrol.feed.domain.repository

import com.laimburggasse.aquariumcontrol.feed.domain.model.FeedProfile
import com.laimburggasse.aquariumcontrol.lib.data.remote.DataFetchResult
import kotlinx.coroutines.flow.StateFlow

interface FeedProfilesRepository {
    val profilesFlow: StateFlow<DataFetchResult<List<FeedProfile>>>
    suspend fun addFeedProfile(feedProfileName: String)
    suspend fun deleteFeedProfile(feedProfile: FeedProfile)
    fun findProfileById(profileId: Int): FeedProfile?
    fun findProfileByName(profileName: String): FeedProfile?
    fun updateFeedProfile(modifiedFeedProfile: FeedProfile)
    suspend fun triggerRefresh()
}