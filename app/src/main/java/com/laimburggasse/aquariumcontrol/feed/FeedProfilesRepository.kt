package com.laimburggasse.aquariumcontrol.feed

import com.laimburggasse.aquariumcontrol.MainViewModel
import com.laimburggasse.aquariumcontrol.webapi.DataFetchResult
import com.laimburggasse.aquariumcontrol.webapi.WebApiParams
import com.laimburggasse.aquariumcontrol.webapi.WebApiPostRequest
import com.laimburggasse.aquariumcontrol.webapi.WebApiUrls
import com.laimburggasse.aquariumcontrol.webapi.webApiPostRequestExecution
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext

open class FeedProfilesRepository(private val mainViewModel: MainViewModel) {
    private val _profilesFlow = MutableStateFlow<DataFetchResult<List<FeedProfile>>>(
        value = DataFetchResult.Success(data = emptyList())
    )
    open val profilesFlow = _profilesFlow.asStateFlow()

    open suspend fun fetchFeedProfiles() {
        val profileUrl: String =
            "http://" + mainViewModel.getControllerAddress() + WebApiUrls.FEED_GET_PROFILES
        val profileReader = FeedProfileReader(profileUrl)
        withContext(context = Dispatchers.IO) {
            val result: DataFetchResult<List<FeedProfileImport>> =
                profileReader.readFeedProfiles()
            when (result) {
                is DataFetchResult.Error -> {
                    _profilesFlow.value = DataFetchResult.Error(
                        exception = result.exception, errorMessage = result.errorMessage
                    )
                }
                is DataFetchResult.Success -> {
                    val feedProfileImports: List<FeedProfileImport> = result.data
                    _profilesFlow.value = DataFetchResult.Success(
                        data = feedProfileImports.toFeedProfileList()
                    )
                }
            }
        }
    }

    open suspend fun deleteFeedProfile(feedProfile: FeedProfile) {
        when (val result: DataFetchResult<List<FeedProfile>> = _profilesFlow.value) {
            is DataFetchResult.Error -> {
                // do nothing
            }
            is DataFetchResult.Success -> {
                withContext(context = Dispatchers.IO) {
                    webApiPostRequestExecution(
                        apiUrl = WebApiUrls.FEED_POST_REMOVE_PROFILE,
                        mainViewModel = mainViewModel,
                        webApiPostRequest = WebApiPostRequest(),
                        optionalGetParams = feedProfile.getFeedProfileApiCallParameters()
                    )
                }
                val currentList: MutableList<FeedProfile> = result.data.toMutableList()
                currentList.remove(element = feedProfile)
                _profilesFlow.value = DataFetchResult.Success(data = currentList)
            }
        }
    }

    open suspend fun addFeedProfile(feedProfileName: String) {
        val apiCallParameters: Map<String, String> = mapOf(WebApiParams.PROFILE_NAME to feedProfileName)

       withContext(context = Dispatchers.IO) {
            webApiPostRequestExecution(
                mainViewModel = mainViewModel,
                apiUrl = WebApiUrls.FEED_POST_PROFILE_CREATE,
                webApiPostRequest = WebApiPostRequest(),
                optionalGetParams = apiCallParameters
            )
           fetchFeedProfiles()
        }

    }

    open fun findProfileById(profileId: Int): FeedProfile? {
        return if (_profilesFlow.value is DataFetchResult.Success) {
            val data = (_profilesFlow.value as DataFetchResult.Success).data
            data.find { it.profileID == profileId }
        } else {
            null
        }
    }

    open fun findProfileByName(profileName: String): FeedProfile? {
        return if (_profilesFlow.value is DataFetchResult.Success) {
            val data = (_profilesFlow.value as DataFetchResult.Success).data
            data.find { it.profileName == profileName }
        } else {
            null
        }
    }
}