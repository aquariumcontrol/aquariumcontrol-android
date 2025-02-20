package com.example.aquariumcontroller.feed.schedule

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.aquariumcontroller.ApiUrls
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class FeedScheduleViewModel(private val myFeedScheduleEntries: List<FeedScheduleEntry>?) :
    ViewModel() {
    private var _feedScheduleEntries = mutableStateOf<List<FeedScheduleEntry>?>(null)

    private val feedScheduleEntries: MutableState<List<FeedScheduleEntry>?> = _feedScheduleEntries

    init {
        _feedScheduleEntries.value = myFeedScheduleEntries
    }

    suspend fun readFeedScheduleEntries(controllerAddress: String) {
        val scheduleUrl =
            "http://" + controllerAddress + ApiUrls.FEED_GET_SCHEDULE
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

    fun deleteFeedScheduleEntry(feedScheduleEntry: FeedScheduleEntry) {
        Log.d("FeedScheduleViewModel", "Deleting feed schedule entry: $feedScheduleEntry")
        val currentList = _feedScheduleEntries.value.orEmpty().toMutableList()
        currentList.remove(feedScheduleEntry)
        _feedScheduleEntries.value = currentList
    }
}