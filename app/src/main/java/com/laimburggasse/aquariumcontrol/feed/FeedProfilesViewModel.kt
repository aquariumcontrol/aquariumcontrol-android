package com.laimburggasse.aquariumcontrol.feed

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.laimburggasse.aquariumcontrol.MainViewModel
import com.laimburggasse.aquariumcontrol.feed.add.FeedProfileAddEvent
import com.laimburggasse.aquariumcontrol.webapi.WebApiPostRequest
import com.laimburggasse.aquariumcontrol.webapi.WebApiUrls
import com.laimburggasse.aquariumcontrol.webapi.webApiPostRequestExecution
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FeedProfilesViewModel(
    myFeedProfiles: List<FeedProfile>? = null,
) : ViewModel() {
    private var _feedProfiles = mutableStateOf<List<FeedProfile>?>(null)
    private val _eventChannel = Channel<FeedProfileAddEvent>()
    val eventFlow = _eventChannel.receiveAsFlow()
    private val feedProfiles: MutableState<List<FeedProfile>?> = _feedProfiles

    init {
        _feedProfiles.value = myFeedProfiles
    }

    suspend fun readFeedProfiles(controllerAddress: String) {
        val profileUrl =
            "http://" + controllerAddress + WebApiUrls.FEED_GET_PROFILES
        val profileReader = FeedProfileReader(profileUrl)
        val feedProfileImports = profileReader.readFeedProfiles()
        if (feedProfileImports != null) {
            withContext(Dispatchers.Main) {  // Switch to the Main thread to update LiveData
                _feedProfiles.value = feedProfileImports.toFeedProfileList()
            }
        }
    }

    fun getFeedProfiles(): List<FeedProfile> {
        return if (feedProfiles.value == null) {
            emptyList()
        } else {
            feedProfiles.value!!
        }
    }

    fun deleteFeedProfile(feedProfile: FeedProfile) {
        val currentList = _feedProfiles.value.orEmpty().toMutableList()
        currentList.remove(feedProfile)
        _feedProfiles.value = currentList
        feedProfiles.value = currentList
    }

    fun addFeedProfile(
        feedProfile: FeedProfile,
        apiCallParameters: Map<String, String>,
        mainViewModel: MainViewModel
    ) {
        val currentList = _feedProfiles.value.orEmpty().toMutableList()
        currentList.add(feedProfile)
        _feedProfiles.value = currentList
        feedProfiles.value = currentList
        viewModelScope.launch {
            val success = webApiPostRequestExecution(
                mainViewModel = mainViewModel,
                apiUrl = WebApiUrls.FEED_POST_PROFILE_CREATE,
                webApiPostRequest = WebApiPostRequest(),
                optionalGetParams = apiCallParameters
            )
            if (success) {
                _eventChannel.send(FeedProfileAddEvent.FeedProfileAddSuccess)
            } else {
                _eventChannel.send(FeedProfileAddEvent.FeedProfileAddFailure(error = "Creation of feed profile failed."))
            }
        }
    }
}