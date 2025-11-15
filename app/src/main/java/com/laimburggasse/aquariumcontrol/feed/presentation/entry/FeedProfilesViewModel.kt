package com.laimburggasse.aquariumcontrol.feed.presentation.entry

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.laimburggasse.aquariumcontrol.feed.data.FeedProfilesRepository
import com.laimburggasse.aquariumcontrol.feed.domain.model.FeedProfile
import com.laimburggasse.aquariumcontrol.lib.data.remote.DataFetchResult
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
                is DataFetchResult.Loading -> FeedProfilesUiState.Loading
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
            feedProfilesRepository.triggerRefresh()
        }
    }

    fun deleteFeedProfile(feedProfile: FeedProfile) {
        viewModelScope.launch {
            feedProfilesRepository.deleteFeedProfile(feedProfile)
        }
    }

    fun onFeedProfilesUiEvent(event: FeedProfilesUiEvent) {
        when (event) {
            is FeedProfilesUiEvent.DeleteFeedProfile -> {
                deleteFeedProfile(feedProfile = event.feedProfile)
            }
            is FeedProfilesUiEvent.Refresh -> {
                loadData()
            }
        }
    }
}