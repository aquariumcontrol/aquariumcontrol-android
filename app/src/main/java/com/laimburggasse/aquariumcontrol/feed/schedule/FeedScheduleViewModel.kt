package com.laimburggasse.aquariumcontrol.feed.schedule

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.laimburggasse.aquariumcontrol.MainViewModel
import com.laimburggasse.aquariumcontrol.feed.FeedProfile
import com.laimburggasse.aquariumcontrol.webapi.DataFetchResult
import com.laimburggasse.aquariumcontrol.webapi.WebApiPostRequest
import com.laimburggasse.aquariumcontrol.webapi.WebApiUrls
import com.laimburggasse.aquariumcontrol.webapi.webApiPostRequestExecution
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.sql.Timestamp
import java.util.Calendar

class FeedScheduleViewModel(
    myFeedScheduleEntries: List<FeedScheduleEntry>?,
) : ViewModel(), FeedScheduleViewModelInterface {

    override var feedScheduleUiState: FeedScheduleUiState by mutableStateOf(value = FeedScheduleUiState.Loading)
        private set

    override var isRefreshing by mutableStateOf(value = false)
        private set

    private var _feedScheduleEntries = mutableStateOf<List<FeedScheduleEntry>?>(value = null)

    private val feedScheduleEntries: MutableState<List<FeedScheduleEntry>?> = _feedScheduleEntries

    init {
        _feedScheduleEntries.value = myFeedScheduleEntries
    }

    override fun loadData(mainViewModel: MainViewModel) {
        viewModelScope.launch {
            isRefreshing = true

            val result: DataFetchResult<List<FeedScheduleEntry>> = fetchFeedScheduleEntries(mainViewModel)

            feedScheduleUiState = when(result) {
                is DataFetchResult.Success -> {
                    val feedScheduleEntries: List<FeedScheduleEntry> = result.data
                    if (feedScheduleEntries.isEmpty()) {
                        FeedScheduleUiState.Empty
                    }
                    else {
                        FeedScheduleUiState.Success(feedScheduleEntries = feedScheduleEntries)
                    }
                }
                is DataFetchResult.Error -> {
                    FeedScheduleUiState.Error(errorMessage = result.errorMessage)
                }
            }

            isRefreshing = false
        }
    }

    suspend fun fetchFeedScheduleEntries(mainViewModel: MainViewModel): DataFetchResult<List<FeedScheduleEntry>> {
        val scheduleUrl: String =
            "http://" + mainViewModel.getControllerAddress() + WebApiUrls.FEED_GET_SCHEDULE
        val scheduleReader = FeedScheduleEntryReader(scheduleUrl)
        return withContext(context = Dispatchers.IO) {
            val result: DataFetchResult<List<FeedScheduleEntryImport>> =
                scheduleReader.readFeedScheduleEntries()
            when (result) {
                is DataFetchResult.Error -> {
                    DataFetchResult.Error(exception = result.exception, errorMessage = result.errorMessage)
                }
                is DataFetchResult.Success -> {
                    val feedScheduleEntryImports: List<FeedScheduleEntryImport> = result.data
                    DataFetchResult.Success(data = feedScheduleEntryImports.toFeedScheduleEntryList())
                }
            }
        }
    }

    override fun getFeedScheduleEntries(): List<FeedScheduleEntry> {
        return if (feedScheduleEntries.value == null) {
            emptyList()
        } else {
            feedScheduleEntries.value!!
        }
    }

    override fun deleteFeedScheduleEntry(feedScheduleEntry: FeedScheduleEntry, mainViewModel: MainViewModel) {
        val currentList: MutableList<FeedScheduleEntry> = _feedScheduleEntries.value.orEmpty().toMutableList()
        currentList.remove(element = feedScheduleEntry)
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

    override fun addFeedScheduleEntry(
        feedScheduleEntry: FeedScheduleEntry,
        feedProfile: FeedProfile,
        repeatDaily: Boolean,
        mainViewModel: MainViewModel,
        selectedCalendar: Calendar
    ) {
        val currentList: MutableList<FeedScheduleEntry> = _feedScheduleEntries.value.orEmpty().toMutableList()
        currentList.add(feedScheduleEntry)
        _feedScheduleEntries.value = currentList
        val apiUrl: String = WebApiUrls.FEED_POST_PROFILE_UPDATE_SCHEDULE

        val optionalGetParams: Map<String, String>? =
            feedProfile.getFeedProfileSchedulingApiCallParameters(
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

    override fun getFeedScheduleEntry(profileName: String, milliSecondsSinceEpoch: Long): FeedScheduleEntry? {
        return feedScheduleEntries.value?.find {
            it.profileName == profileName &&
            it.timestamp.time == milliSecondsSinceEpoch
        }
    }
}
