package com.laimburggasse.aquariumcontrol.feed.presentation.entry

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.laimburggasse.aquariumcontrol.ViewNavigationRoutes
import com.laimburggasse.aquariumcontrol.feed.data.FeedProfilesRepositoryImpl
import com.laimburggasse.aquariumcontrol.feed.data.FeedScheduleRepositoryImpl
import com.laimburggasse.aquariumcontrol.feed.domain.model.FeedProfile
import com.laimburggasse.aquariumcontrol.feed.domain.model.FeedScheduleEntry
import com.laimburggasse.aquariumcontrol.feed.presentation.schedule.FeedScheduleEntryUiEvent
import com.laimburggasse.aquariumcontrol.lib.data.remote.DataFetchResult
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
    val feedScheduleRepository: FeedScheduleRepositoryImpl,
    val feedProfilesRepository: FeedProfilesRepositoryImpl
) : ViewModel() {

    val feedScheduleSelectionUiState: StateFlow<FeedScheduleSelectionUiState> = feedScheduleRepository.scheduleFlow
        .map { result ->
            when (result) {
                is DataFetchResult.Error -> FeedScheduleSelectionUiState.Error(errorMessage = result.errorMessage)
                is DataFetchResult.Success -> {
                    if (result.data.isEmpty()) {
                        FeedScheduleSelectionUiState.Empty
                    } else {
                        FeedScheduleSelectionUiState.Success(feedScheduleEntries = result.data)
                    }
                }
                is DataFetchResult.Loading -> FeedScheduleSelectionUiState.Loading
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.Companion.Eagerly,
            initialValue = FeedScheduleSelectionUiState.Loading // The UI starts in a loading state
        )

    val _feedProfile = MutableStateFlow<FeedProfile?>(value = FeedProfile())
    val feedProfile: StateFlow<FeedProfile?> = _feedProfile

    val _isNewScheduleEntry = MutableStateFlow(value = false)
    val isNewScheduleEntry: StateFlow<Boolean> = _isNewScheduleEntry

    val _millisSinceEpoch = MutableStateFlow(value = 0L)
    val millisSinceEpoch: StateFlow<Long> = _millisSinceEpoch

    private fun loadDataForProfile(profileId: Int) {
        viewModelScope.launch {
            _feedProfile.value = feedProfilesRepository.findProfileById(profileId)
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
            _feedProfile.value = null
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
                feedProfileId = feedProfile.profileID,
                feedProfileName = feedProfile.profileName,
                repeatDaily,
                selectedCalendar
            )
        }
    }

    fun updateFeedScheduleEntry(
        feedProfile: FeedProfile,
        repeatDaily: Boolean,
        selectedCalendar: Calendar,
        millisSinceEpoch: Long // data from old entry
    ) {
        viewModelScope.launch {
            feedScheduleRepository.updateFeedScheduleEntry(
                feedProfileId = feedProfile.profileID,
                feedProfileName = feedProfile.profileName,
                repeatDaily,
                selectedCalendar,
                millisSinceEpoch
            )
        }
    }

    fun getFeedProfileIdByName(name: String): Int? {
        val feedProfile: FeedProfile? = feedProfilesRepository.findProfileByName(profileName = name)
        return feedProfile?.profileID
    }

    fun loadData() {
        viewModelScope.launch {
            feedScheduleRepository.triggerRefresh()
        }
    }

    fun onFeedSchedulesUiEvent(event: FeedScheduleUiEvent): Int? {
        when (event) {
            is FeedScheduleUiEvent.GetFeedProfileIdByName -> {
                return getFeedProfileIdByName(event.feedProfileName)
            }
            is FeedScheduleUiEvent.DeleteFeedScheduleEntry -> {
                deleteFeedScheduleEntry(feedScheduleEntry = event.feedScheduleEntry)
            }
            is FeedScheduleUiEvent.Refresh -> {
                loadData()
            }
        }
        return null
    }

    fun onFeedScheduleEntryUiEvent(event: FeedScheduleEntryUiEvent) {
        when (event) {
            is FeedScheduleEntryUiEvent.UpdateFeedScheduleEntry -> {
                if (event.isNewEntry) {
                    addFeedScheduleEntry(
                        feedProfile = event.feedProfile,
                        repeatDaily = event.feedScheduleEntry.repeatDaily,
                        selectedCalendar = event.selectedCalendar
                    )
                }
                else {
                    updateFeedScheduleEntry(
                        feedProfile = event.feedProfile,
                        repeatDaily = event.feedScheduleEntry.repeatDaily,
                        selectedCalendar = event.selectedCalendar,
                        millisSinceEpoch = event.millisSinceEpoch
                    )
                }
            }
        }
    }
}