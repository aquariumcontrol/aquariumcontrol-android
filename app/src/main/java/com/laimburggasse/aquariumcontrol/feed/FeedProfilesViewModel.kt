package com.laimburggasse.aquariumcontrol.feed

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.laimburggasse.aquariumcontrol.MainViewModel
import com.laimburggasse.aquariumcontrol.feed.add.FeedProfileAddEvent
import com.laimburggasse.aquariumcontrol.webapi.DataFetchResult
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
) : ViewModel(), FeedProfilesViewModelInterface {
    override var feedProfilesUiState: FeedProfilesUiState by mutableStateOf(value = FeedProfilesUiState.Loading)
        private set

    override var isRefreshing by mutableStateOf(value = false)
        private set
    private var _feedProfiles = mutableStateOf<List<FeedProfile>?>(null)
    private val _eventChannel = Channel<FeedProfileAddEvent>()
    val eventFlow = _eventChannel.receiveAsFlow()
    private val feedProfiles: MutableState<List<FeedProfile>?> = _feedProfiles

    init {
        _feedProfiles.value = myFeedProfiles
    }

    override fun loadData(mainViewModel: MainViewModel) {
        viewModelScope.launch {
            isRefreshing = true

            val result: DataFetchResult<List<FeedProfile>> = fetchFeedProfiles(mainViewModel)

            feedProfilesUiState = when(result) {
                is DataFetchResult.Success -> {
                    val feedProfiles: List<FeedProfile> = result.data
                    if (feedProfiles.isEmpty()) {
                        FeedProfilesUiState.Empty
                    }
                    else {
                        FeedProfilesUiState.Success(feedProfiles = feedProfiles)
                    }
                }
                is DataFetchResult.Error -> {
                    FeedProfilesUiState.Error(errorMessage = result.errorMessage)
                }
            }

            isRefreshing = false
        }
    }

    suspend fun fetchFeedProfiles(mainViewModel: MainViewModel): DataFetchResult<List<FeedProfile>> {
        val profileUrl =
            "http://" + mainViewModel.getControllerAddress() + WebApiUrls.FEED_GET_PROFILES
        val profileReader = FeedProfileReader(profileUrl)
        return withContext(context = Dispatchers.IO) {
            val result: DataFetchResult<List<FeedProfileImport>> =
                profileReader.readFeedProfiles()
            when (result) {
                is DataFetchResult.Error -> {
                    DataFetchResult.Error(exception = result.exception, errorMessage = result.errorMessage)
                }
                is DataFetchResult.Success -> {
                    val feedProfileImports: List<FeedProfileImport> = result.data
                    DataFetchResult.Success(data = feedProfileImports.toFeedProfileList())
                }
            }
        }
    }

    override fun getFeedProfileList(): List<FeedProfile> {
        return if (feedProfiles.value == null) {
            emptyList()
        } else {
            feedProfiles.value!!
        }
    }

    override fun deleteFeedProfile(feedProfile: FeedProfile) {
        val currentList = _feedProfiles.value.orEmpty().toMutableList()
        currentList.remove(feedProfile)
        _feedProfiles.value = currentList
        feedProfiles.value = currentList
    }

    override fun getFeedProfile(profileName: String): FeedProfile? {
        return feedProfiles.value?.find { it.profileName == profileName }
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

    // @todo: add functionality to avoid creating feed profiles with already existing names
}