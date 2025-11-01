package com.laimburggasse.aquariumcontrol.feed.common

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.laimburggasse.aquariumcontrol.ViewNavigationRoutes
import com.laimburggasse.aquariumcontrol.feed.entry.FeedScheduleUiEvent
import com.laimburggasse.aquariumcontrol.feed.schedule.FeedScheduleEntry
import com.laimburggasse.aquariumcontrol.feed.schedule.FeedScheduleEntryUiEvent
import com.laimburggasse.aquariumcontrol.feed.schedule.FeedScheduleRepository
import com.laimburggasse.aquariumcontrol.webapi.DataFetchResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class FeedScheduleViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    val feedScheduleRepository: FeedScheduleRepository,
    val feedProfilesRepository: FeedProfilesRepository
) : ViewModel() {

    val feedScheduleUiState: StateFlow<FeedScheduleUiState> = feedScheduleRepository.scheduleFlow
        .map { result ->
            when (result) {
                is DataFetchResult.Error -> FeedScheduleUiState.Error(errorMessage = result.errorMessage)
                is DataFetchResult.Success -> {
                    if (result.data.isEmpty()) {
                        Log.d("FeedScheduleViewModel", "No feed schedule entries found")
                        FeedScheduleUiState.Empty
                    } else {
                        Log.d("FeedScheduleViewModel", "Found ${result.data.size} feed schedule entries")
                        FeedScheduleUiState.Success(feedScheduleEntries = result.data)
                    }
                }
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = FeedScheduleUiState.Loading // The UI starts in a loading state
        )

    val _feedProfile = MutableStateFlow(value = FeedProfile())
    val feedProfile: StateFlow<FeedProfile> = _feedProfile

    val _isNewScheduleEntry = MutableStateFlow(value = false)
    val isNewScheduleEntry: StateFlow<Boolean> = _isNewScheduleEntry

    val _millisSinceEpoch = MutableStateFlow(value = 0L)
    val millisSinceEpoch: StateFlow<Long> = _millisSinceEpoch

    private fun loadDataForProfile(profileId: Int) {
        viewModelScope.launch {
            val profile: FeedProfile? = feedProfilesRepository.findProfileById(profileId)
            if (profile != null) {
                _feedProfile.value = profile
            }
        }
    }

    init {
        // Get the initial values from navigation arguments
        _isNewScheduleEntry.value = savedStateHandle.get(ViewNavigationRoutes.FEED_ARG_IS_NEW_SCHEDULE_ENTRY) ?: false
        _millisSinceEpoch.value = savedStateHandle.get(ViewNavigationRoutes.FEED_ARG_MILLIS_SINCE_EPOCH) ?: 0L
        val profileId: Int? = savedStateHandle.get(ViewNavigationRoutes.FEED_ARG_PROFILE_ID)

        if (profileId != null) {
            loadDataForProfile(profileId)
        } else {
            // @todo: Handle error case
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
                feedProfileName = feedProfile.profileName,
                repeatDaily,
                selectedCalendar
            )
        }
    }

    fun getFeedProfileIdByName(name: String): Int? {
        val feedProfile: FeedProfile? = feedProfilesRepository.findProfileByName(profileName = name)
        return feedProfile?.profileID
    }

    fun onFeedSchedulesUiEvent(event: FeedScheduleUiEvent): Int? {
        when (event) {
            is FeedScheduleUiEvent.GetFeedProfileIdByName -> {
                return getFeedProfileIdByName(event.feedProfileName)
            }
            is FeedScheduleUiEvent.DeleteFeedScheduleEntry -> {
                deleteFeedScheduleEntry(feedScheduleEntry = event.feedScheduleEntry)
            }
        }
        return null
    }

    fun onFeedScheduleEntryUiEvent(event: FeedScheduleEntryUiEvent) {
        when (event) {
            is FeedScheduleEntryUiEvent.UpsertFeedScheduleEntry -> {
                addFeedScheduleEntry(
                    feedProfile = event.feedProfile,
                    repeatDaily = event.feedScheduleEntry.repeatDaily,
                    selectedCalendar = event.selectedCalendar
                )
            }
        }
    }
}
