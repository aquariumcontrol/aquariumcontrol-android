package com.laimburggasse.aquariumcontrol.feed.schedule

import com.laimburggasse.aquariumcontrol.MainViewModel
import com.laimburggasse.aquariumcontrol.feed.FeedProfile
import com.laimburggasse.aquariumcontrol.webapi.DataFetchResult
import com.laimburggasse.aquariumcontrol.webapi.WebApiPostRequest
import com.laimburggasse.aquariumcontrol.webapi.WebApiUrls
import com.laimburggasse.aquariumcontrol.webapi.webApiPostRequestExecution
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import java.sql.Timestamp
import java.util.Calendar

open class FeedScheduleRepository(private val mainViewModel: MainViewModel) {
    private val _scheduleFlow = MutableStateFlow<DataFetchResult<List<FeedScheduleEntry>>>(
        value = DataFetchResult.Success(data = emptyList())
    )
    open val scheduleFlow = _scheduleFlow.asStateFlow()

    open suspend fun fetchFeedScheduleEntries() {
        val scheduleUrl: String =
            "http://" + mainViewModel.getControllerAddress() + WebApiUrls.FEED_GET_SCHEDULE
        val scheduleReader = FeedScheduleEntryReader(scheduleUrl)
        withContext(context = Dispatchers.IO) {
            val result: DataFetchResult<List<FeedScheduleEntryImport>> =
                scheduleReader.readFeedScheduleEntries()
            when (result) {
                is DataFetchResult.Error -> {
                    _scheduleFlow.value = DataFetchResult.Error(
                        exception = result.exception, errorMessage = result.errorMessage
                    )
                }
                is DataFetchResult.Success -> {
                    val feedScheduleEntryImports: List<FeedScheduleEntryImport> = result.data
                    _scheduleFlow.value = DataFetchResult.Success(
                        data = feedScheduleEntryImports.toFeedScheduleEntryList()
                    )
                }
            }
        }
    }
    
    open suspend fun deleteFeedScheduleEntry(feedScheduleEntry: FeedScheduleEntry) {
        withContext(context = Dispatchers.IO) {
            webApiPostRequestExecution(
                apiUrl = WebApiUrls.FEED_POST_SCHEDULE_REMOVE_ENTRY,
                mainViewModel = mainViewModel,
                webApiPostRequest = WebApiPostRequest(),
                optionalGetParams = feedScheduleEntry.getApiParameters()
            )
            val currentResult = _scheduleFlow.value
            if (currentResult is DataFetchResult.Success) {
                val updatedList = currentResult.data.toMutableList()
                updatedList.remove(feedScheduleEntry)
                _scheduleFlow.value = DataFetchResult.Success(updatedList)
            }
        }
    }

    fun getFeedScheduleEntry(profileName: String, milliSecondsSinceEpoch: Long): FeedScheduleEntry? {
        val currentResult = _scheduleFlow.value
        return if (currentResult is DataFetchResult.Success) {
            currentResult.data.find {
                it.profileName == profileName &&
                        it.timestamp.time == milliSecondsSinceEpoch
            }
        } else {
            null
        }
    }

    suspend fun addFeedScheduleEntry(
        feedProfile: FeedProfile,
        repeatDaily: Boolean,
        selectedCalendar: Calendar
    ) {
        val currentResult: DataFetchResult<List<FeedScheduleEntry>> = _scheduleFlow.value
        if (currentResult is DataFetchResult.Success) {
            val apiUrl: String = WebApiUrls.FEED_POST_PROFILE_UPDATE_SCHEDULE

            val optionalGetParams: Map<String, String>? =
                feedProfile.getFeedProfileSchedulingApiCallParameters(
                    isDaily = repeatDaily,
                    timestamp = Timestamp(selectedCalendar.timeInMillis)
                )

            if (optionalGetParams != null) {
                webApiPostRequestExecution(
                    mainViewModel = mainViewModel,
                    webApiPostRequest = WebApiPostRequest(),
                    optionalGetParams = optionalGetParams,
                    apiUrl = apiUrl
                )
            }
        }
    }
}