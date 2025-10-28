package com.laimburggasse.aquariumcontrol.feed.common

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.laimburggasse.aquariumcontrol.feed.entry.FeedProfilesUiState
import com.laimburggasse.aquariumcontrol.feed.entry.FeedProfilesUiEvent
import com.laimburggasse.aquariumcontrol.webapi.DataFetchResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FeedProfilesViewModel @Inject constructor(
    val feedProfilesRepository: FeedProfilesRepository
) : ViewModel() {

    val feedProfilesUiState: StateFlow<FeedProfilesUiState> = feedProfilesRepository.profilesFlow
        .map { result ->
            when (result) {
                is DataFetchResult.Error -> FeedProfilesUiState.Error(errorMessage = result.errorMessage)
                is DataFetchResult.Success -> {
                    if (result.data.isEmpty()) {
                        FeedProfilesUiState.Empty
                    } else {
                        FeedProfilesUiState.Success(feedProfiles = result.data)
                    }
                }
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.Companion.Eagerly,
            initialValue = FeedProfilesUiState.Loading // The UI starts in a loading state
        )

    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            feedProfilesRepository.fetchFeedProfiles()
        }
    }

    fun deleteFeedProfile(feedProfile: FeedProfile) {
        viewModelScope.launch {
            feedProfilesRepository.deleteFeedProfile(feedProfile)
        }
    }

    fun findProfileById(profileID: Int): FeedProfile? {
        val result: DataFetchResult<List<FeedProfile>> = feedProfilesRepository.profilesFlow.value
        if (result is DataFetchResult.Success) {
            return result.data.find { it.profileID == profileID }
        }
        return null
    }

    fun onFeedProfilesUiEvent(event: FeedProfilesUiEvent) {
        when (event) {
            is FeedProfilesUiEvent.DeleteFeedProfile -> {
                deleteFeedProfile(feedProfile = event.feedProfile)
            }
        }
    }
    // @todo: add functionality to avoid creating feed profiles with already existing names
}