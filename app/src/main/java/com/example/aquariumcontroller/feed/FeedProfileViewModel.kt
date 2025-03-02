package com.example.aquariumcontroller.feed

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.aquariumcontroller.webapi.WebApiUrls
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class FeedProfileViewModel(myFeedProfiles: List<FeedProfile>? = null) :
    ViewModel() {
    private var _feedProfiles = mutableStateOf<List<FeedProfile>?>(null)

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
        Log.d("FeedProfileViewModel", "Deleting feed profile: $feedProfile")
        val currentList = _feedProfiles.value.orEmpty().toMutableList()
        currentList.remove(feedProfile)
        _feedProfiles.value = currentList
        feedProfiles.value = currentList
    }

    fun addFeedProfile(feedProfile: FeedProfile) {
        Log.d("FeedProfileViewModel", "Adding feed Profile: $feedProfile")
        val currentList = _feedProfiles.value.orEmpty().toMutableList()
        currentList.add(feedProfile)
        _feedProfiles.value = currentList
        feedProfiles.value = currentList
    }
}