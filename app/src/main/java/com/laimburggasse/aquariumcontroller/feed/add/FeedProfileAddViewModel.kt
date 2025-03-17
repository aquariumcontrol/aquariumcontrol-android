package com.laimburggasse.aquariumcontroller.feed.add

import androidx.lifecycle.ViewModel
import com.laimburggasse.aquariumcontroller.feed.FeedProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class FeedProfileAddViewModel(private val myFeedProfile: FeedProfile) : ViewModel() {
    private val _feedProfileName = MutableStateFlow(myFeedProfile.profileName)
    val feedProfileName: StateFlow<String> = _feedProfileName.asStateFlow()

    fun getApiCallParameters(): Map<String, String> {
        return myFeedProfile.getFeedProfileApiCallParameters()
    }

    fun setFeedProfileName(name: String) {
        myFeedProfile.profileName = name
        _feedProfileName.value = name // Update the StateFlow
    }

    fun getFeedProfile(): FeedProfile {
        return myFeedProfile
    }
}