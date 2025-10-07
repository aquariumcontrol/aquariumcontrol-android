package com.laimburggasse.aquariumcontrol.feed.schedule

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.laimburggasse.aquariumcontrol.MainViewModel
import com.laimburggasse.aquariumcontrol.feed.FeedProfile
import com.laimburggasse.aquariumcontrol.webapi.WebApiPostRequest
import com.laimburggasse.aquariumcontrol.webapi.WebApiUrls
import com.laimburggasse.aquariumcontrol.webapi.webApiPostRequestExecution
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.sql.Timestamp
import java.util.Calendar

class FeedScheduleViewModel(myFeedScheduleEntries: List<FeedScheduleEntry>?) :
    ViewModel() {
    private var _feedScheduleEntries = mutableStateOf<List<FeedScheduleEntry>?>(null)

    private val feedScheduleEntries: MutableState<List<FeedScheduleEntry>?> = _feedScheduleEntries

    init {
        _feedScheduleEntries.value = myFeedScheduleEntries
    }

    suspend fun readFeedScheduleEntries(controllerAddress: String) {
        val scheduleUrl =
            "http://" + controllerAddress + WebApiUrls.FEED_GET_SCHEDULE
        val scheduleReader = FeedScheduleEntryReader(scheduleUrl)
        val feedScheduleEntryImports = scheduleReader.readFeedScheduleEntries()
        if (feedScheduleEntryImports != null) {
            withContext(Dispatchers.Main) {  // Switch to the Main thread to update LiveData
                _feedScheduleEntries.value = feedScheduleEntryImports.toFeedScheduleEntryList()
            }
        }
    }

    fun getFeedScheduleEntries(): List<FeedScheduleEntry> {
        return if (feedScheduleEntries.value == null) {
            emptyList()
        } else {
            feedScheduleEntries.value!!
        }
    }

    fun deleteFeedScheduleEntry(feedScheduleEntry: FeedScheduleEntry, mainViewModel: MainViewModel) {
        val currentList = _feedScheduleEntries.value.orEmpty().toMutableList()
        currentList.remove(feedScheduleEntry)
        _feedScheduleEntries.value = currentList
        viewModelScope.launch {
            webApiPostRequestExecution(
                apiUrl = WebApiUrls.FEED_POST_SCHEDULE_REMOVE_ENTRY,
                mainViewModel = mainViewModel,
                webApiPostRequest = WebApiPostRequest(),
                optionalGetParams = feedScheduleEntry.getApiParameters()
            )
        }
    }

    fun addFeedScheduleEntry(
        feedScheduleEntry: FeedScheduleEntry,
        feedProfile: FeedProfile?,
        repeatDaily: Boolean,
        mainViewModel: MainViewModel,
        selectedCalendar: Calendar
    ) {
        val currentList = _feedScheduleEntries.value.orEmpty().toMutableList()
        currentList.add(feedScheduleEntry)
        _feedScheduleEntries.value = currentList
        val apiUrl: String =
            if (feedProfile == null) WebApiUrls.FEED_POST_PROFILE_SCHEDULE
            else WebApiUrls.FEED_POST_PROFILE_UPDATE_SCHEDULE

        val optionalGetParams =
            feedProfile?.getFeedProfileSchedulingApiCallParameters(
                isDaily = repeatDaily,
                timestamp = Timestamp(selectedCalendar.timeInMillis)
            )

        if (optionalGetParams != null) {
            viewModelScope.launch {
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