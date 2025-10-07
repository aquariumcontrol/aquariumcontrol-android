package com.laimburggasse.aquariumcontrol.feed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.laimburggasse.aquariumcontrol.MainViewModel
import com.laimburggasse.aquariumcontrol.webapi.WebApiPostRequest
import com.laimburggasse.aquariumcontrol.webapi.WebApiUrls
import com.laimburggasse.aquariumcontrol.webapi.webApiPostRequestExecution
import kotlinx.coroutines.launch

class FeedProfileViewModel(
    private val feedProfile: FeedProfile,
    private val mainViewModel: MainViewModel
): ViewModel() {
    fun onDelete() {
        viewModelScope.launch {
            webApiPostRequestExecution(
                apiUrl = WebApiUrls.FEED_POST_REMOVE_PROFILE,
                mainViewModel = mainViewModel,
                webApiPostRequest = WebApiPostRequest(),
                optionalGetParams = feedProfile.getFeedProfileApiCallParameters()
            )
        }
    }
}