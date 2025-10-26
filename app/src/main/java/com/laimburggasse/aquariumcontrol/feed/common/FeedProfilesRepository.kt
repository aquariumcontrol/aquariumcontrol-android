package com.laimburggasse.aquariumcontrol.feed.common

import com.laimburggasse.aquariumcontrol.SessionManager
import com.laimburggasse.aquariumcontrol.webapi.DataFetchResult
import com.laimburggasse.aquariumcontrol.webapi.WebApiParams
import com.laimburggasse.aquariumcontrol.webapi.WebApiPostRequest
import com.laimburggasse.aquariumcontrol.webapi.WebApiUrls
import com.laimburggasse.aquariumcontrol.webapi.webApiPostRequestExecution
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext

open class FeedProfilesRepository(private val sessionManager: SessionManager) {
    private val _profilesFlow = MutableStateFlow<DataFetchResult<List<FeedProfile>>>(
        value = DataFetchResult.Success(data = emptyList())
    )
    open val profilesFlow = _profilesFlow.asStateFlow()

    open suspend fun fetchFeedProfiles() {
        val profileUrl: String =
            "http://" + sessionManager.getControllerAddress() + WebApiUrls.FEED_GET_PROFILES
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
                        data = feedProfileImports.map { importItem ->
                            FeedProfile.fromFeedProfileImport(feedProfileImport = importItem)
                        }
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
                        sessionManager = sessionManager,
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

    open suspend fun addFeedProfile(feedProfileName: String): Boolean {
        val apiCallParameters: Map<String, String> = mapOf(WebApiParams.PROFILE_NAME to feedProfileName)

        val success: Boolean = withContext(context = Dispatchers.IO) {
            webApiPostRequestExecution(
                sessionManager = sessionManager,
                apiUrl = WebApiUrls.FEED_POST_PROFILE_CREATE,
                webApiPostRequest = WebApiPostRequest(),
                optionalGetParams = apiCallParameters
            )
        }

        if (success) {
            fetchFeedProfiles()
        }

        return success
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

    suspend fun updateFeedProfile(modifiedFeedProfile: FeedProfile) {
        val apiCallParameters: Map<String, String> = modifiedFeedProfile.getFeedProfileApiCallParameters()
        withContext(context = Dispatchers.IO) {
            webApiPostRequestExecution(
                sessionManager = sessionManager,
                apiUrl = WebApiUrls.FEED_POST_PROFILE_UPDATE,
                webApiPostRequest = WebApiPostRequest(),
                optionalGetParams = apiCallParameters
            )
        }

        // Update the local list of profiles
        val currentResult: DataFetchResult<List<FeedProfile>> = _profilesFlow.value
        if (currentResult is DataFetchResult.Success) {
            val updatedList: List<FeedProfile> = currentResult.data.map { existingProfile ->
                if (existingProfile.profileID == modifiedFeedProfile.profileID) {
                    modifiedFeedProfile
                } else {
                    existingProfile
                }
            }
            _profilesFlow.value = DataFetchResult.Success(data = updatedList)
        }
    }
}