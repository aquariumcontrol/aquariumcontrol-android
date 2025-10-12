package com.laimburggasse.aquariumcontrol.feed.schedule

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.laimburggasse.aquariumcontrol.feed.FeedProfile
import com.laimburggasse.aquariumcontrol.feed.FeedProfilesRepository
import com.laimburggasse.aquariumcontrol.webapi.DataFetchResult
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar

class FeedScheduleViewModel(
    val feedScheduleRepository: FeedScheduleRepository,
    val feedProfilesRepository: FeedProfilesRepository
) : ViewModel() {

    val feedScheduleUiState: StateFlow<FeedScheduleUiState> = feedScheduleRepository.scheduleFlow
        .map { result ->
            when (result) {
                is DataFetchResult.Error -> FeedScheduleUiState.Error(errorMessage = result.errorMessage)
                is DataFetchResult.Success -> {
                    if (result.data.isEmpty()) {
                        FeedScheduleUiState.Empty
                    } else {
                        FeedScheduleUiState.Success(feedScheduleEntries = result.data)
                    }
                }
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = FeedScheduleUiState.Loading // The UI starts in a loading state
        )

    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            feedScheduleRepository.fetchFeedScheduleEntries()
        }
    }

    fun deleteFeedScheduleEntry(feedScheduleEntry: FeedScheduleEntry) {
        viewModelScope.launch {
            feedScheduleRepository.deleteFeedScheduleEntry(feedScheduleEntry = feedScheduleEntry)
        }
    }

    fun addFeedScheduleEntry(
        feedProfile: FeedProfile,
        repeatDaily: Boolean,
        selectedCalendar: Calendar
    ) {
        viewModelScope.launch {
            feedScheduleRepository.addFeedScheduleEntry(
                feedProfile,
                repeatDaily,
                selectedCalendar
            )
        }
    }

    fun getFeedProfileIdByName(name: String): Int? {
        val feedProfile: FeedProfile? = feedProfilesRepository.findProfileByName(profileName = name)
        return feedProfile?.profileID
    }
}
