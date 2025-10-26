package com.laimburggasse.aquariumcontrol.feed.add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.laimburggasse.aquariumcontrol.feed.common.FeedProfilesRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class FeedProfileAddViewModel(
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

            val success: Boolean = feedProfilesRepository.addFeedProfile(feedProfileName = feedProfileName.value)

            if (success) {
                _feedProfileAddUiState.value = FeedProfileAddUiState.Success
            } else {
                _feedProfileAddUiState.value = FeedProfileAddUiState.Error(message = "Failed to add feed profile")
            }
        }
    }

    fun onFeedProfileAddEvent(event: FeedProfileAddEvent) {
        when (event) {
            is FeedProfileAddEvent.Add -> {
                addFeedProfile()
            }
            is FeedProfileAddEvent.HandleState -> {
                onStateHandled()
            }
            is FeedProfileAddEvent.SetFeedProfileName -> {
                setFeedProfileName(event.name)
            }
            is FeedProfileAddEvent.Success -> {

            }
        }
    }
}