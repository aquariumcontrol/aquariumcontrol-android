package com.laimburggasse.aquariumcontrol.feed.add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.laimburggasse.aquariumcontrol.feed.FeedProfilesRepository
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

    private val _eventChannel = Channel<FeedProfileAddEvent>()
    val eventFlow = _eventChannel.receiveAsFlow()

    fun setFeedProfileName(name: String) {
        _feedProfileName.value = name // Update the StateFlow
        // @todo: add here functionality to avoid existing names
    }


    fun addFeedProfile() {
        viewModelScope.launch {
            feedProfilesRepository.addFeedProfile(feedProfileName = feedProfileName.value)
            _eventChannel.send(element = FeedProfileAddEvent.Success)
        }
    }
}