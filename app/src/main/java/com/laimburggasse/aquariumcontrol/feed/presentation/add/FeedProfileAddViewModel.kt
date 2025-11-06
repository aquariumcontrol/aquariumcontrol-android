package com.laimburggasse.aquariumcontrol.feed.presentation.add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.laimburggasse.aquariumcontrol.feed.data.FeedProfilesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FeedProfileAddViewModel @Inject constructor(
    private val feedProfilesRepository: FeedProfilesRepository
) : ViewModel() {
    private val _feedProfileName = MutableStateFlow("")
    val feedProfileName: StateFlow<String> = _feedProfileName.asStateFlow()

    private val _feedProfileAddUiState = MutableStateFlow<FeedProfileAddUiState>(
        value = FeedProfileAddUiState.Idle
    )
    val feedProfileAddUiState: StateFlow<FeedProfileAddUiState> = _feedProfileAddUiState.asStateFlow()

    fun setFeedProfileName(name: String) {
        _feedProfileName.value = name // Update the StateFlow
        // @todo: add here functionality to avoid existing names
    }

    fun onStateHandled() {
        _feedProfileAddUiState.value = FeedProfileAddUiState.Idle
    }

    fun addFeedProfile() {
        if (_feedProfileAddUiState.value is FeedProfileAddUiState.Loading) return

        viewModelScope.launch {
            _feedProfileAddUiState.value = FeedProfileAddUiState.Loading
            feedProfilesRepository.addFeedProfile(feedProfileName = feedProfileName.value)
            _feedProfileAddUiState.value = FeedProfileAddUiState.Success
        }
    }

    fun onFeedProfileAddEvent(event: FeedProfileAddUiEvent) {
        when (event) {
            is FeedProfileAddUiEvent.Add -> {
                addFeedProfile()
            }
            is FeedProfileAddUiEvent.HandleState -> {
                onStateHandled()
            }
            is FeedProfileAddUiEvent.SetFeedProfileName -> {
                setFeedProfileName(event.name)
            }
            is FeedProfileAddUiEvent.Success -> {

            }
        }
    }
}