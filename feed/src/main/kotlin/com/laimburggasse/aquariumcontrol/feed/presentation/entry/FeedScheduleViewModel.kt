/*
 * Copyright 2025 Uwe Martin
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.laimburggasse.aquariumcontrol.feed.presentation.entry

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.laimburggasse.aquariumcontrol.common.data.remote.DataFetchResult
import com.laimburggasse.aquariumcontrol.feed.domain.model.FeedProfile
import com.laimburggasse.aquariumcontrol.feed.domain.model.FeedScheduleEntry
import com.laimburggasse.aquariumcontrol.feed.domain.repository.FeedProfilesRepository
import com.laimburggasse.aquariumcontrol.feed.domain.repository.FeedScheduleRepository
import com.laimburggasse.aquariumcontrol.feed.presentation.schedule.FeedScheduleEntryUiEvent
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Inject

/**
 * ViewModel for managing the feed schedule entries.
 * Handles the display, addition, modification, and deletion of scheduled feeding events.
 *
 * @property feedScheduleRepository The repository for managing scheduled feed entries.
 * @property feedProfilesRepository The repository for fetching feed profile details.
 */
@Inject
class FeedScheduleViewModel(
    private val feedScheduleRepository: FeedScheduleRepository,
    private val feedProfilesRepository: FeedProfilesRepository
) : ViewModel() {

    /** Internal mutable state for the currently selected feed profile (when editing a schedule). */
    private val _feedProfile = MutableStateFlow<FeedProfile?>(value = null)

    /** Public read-only state for the currently selected feed profile. */
    val feedProfile: StateFlow<FeedProfile?> = _feedProfile.asStateFlow()

    /** Internal mutable state indicating if the user is creating a new schedule entry. */
    private val _isNewScheduleEntry = MutableStateFlow(value = false)

    /** Public read-only state indicating if the current action is for a new entry. */
    val isNewScheduleEntry: StateFlow<Boolean> = _isNewScheduleEntry.asStateFlow()

    /** Internal mutable state holding the timestamp of the entry being edited. */
    private val _millisSinceEpoch = MutableStateFlow(value = 0L)

    /** Public read-only state holding the timestamp of the current entry. */
    val millisSinceEpoch: StateFlow<Long> = _millisSinceEpoch.asStateFlow()

    /** The reactive UI state for the schedule list, mapped from the repository flow. */
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
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000L),
            initialValue = FeedScheduleSelectionUiState.Loading
        )

    
    /**
     * Initializes the state parameters for editing or creating a schedule entry.
     *
     * @param profileId The ID of the associated feed profile, if any.
     * @param isNewEntry True if this is a new entry, false if editing an existing one.
     * @param millis The timestamp of the entry in milliseconds.
     */
    fun setScheduleParams(profileId: Int?, isNewEntry: Boolean, millis: Long) {
        _isNewScheduleEntry.value = isNewEntry
        _millisSinceEpoch.value = millis
        if (profileId != null) {
            loadDataForProfile(profileId = profileId)
        }
    }

    
    /**
     * Loads the specific [FeedProfile] details from the repository based on its ID.
     *
     * @param profileId The ID of the profile to load.
     */
    private fun loadDataForProfile(profileId: Int) {
        viewModelScope.launch {
            _feedProfile.value = feedProfilesRepository.findProfileById(profileId = profileId)
        }
    }

    
    /**
     * Deletes a specific schedule entry from the repository.
     *
     * @param feedScheduleEntry The entry to delete.
     */
    fun deleteFeedScheduleEntry(feedScheduleEntry: FeedScheduleEntry) {
        viewModelScope.launch {
            feedScheduleRepository.deleteFeedScheduleEntry(feedScheduleEntry = feedScheduleEntry)
        }
    }

    
    /**
     * Adds a new feed schedule entry to the repository.
     *
     * @param feedProfile The profile to be executed.
     * @param repeatDaily Whether the schedule should repeat every day.
     * @param timestampMillis The scheduled execution time.
     */
    fun addFeedScheduleEntry(
        feedProfile: FeedProfile,
        repeatDaily: Boolean,
        timestampMillis: Long
    ) {
        viewModelScope.launch {
            feedScheduleRepository.addFeedScheduleEntry(
                feedProfileId = feedProfile.profileID,
                feedProfileName = feedProfile.profileName,
                repeatDaily = repeatDaily,
                timestampMillis = timestampMillis
            )
        }
    }

    
    /**
     * Updates an existing feed schedule entry in the repository.
     *
     * @param feedProfile The profile to be executed.
     * @param repeatDaily Whether the schedule should repeat every day.
     * @param timestampMillis The new scheduled execution time.
     * @param millisSinceEpoch The original timestamp of the entry being modified (used for identification).
     */
    fun updateFeedScheduleEntry(
        feedProfile: FeedProfile,
        repeatDaily: Boolean,
        timestampMillis: Long,
        millisSinceEpoch: Long // data from old entry
    ) {
        viewModelScope.launch {
            feedScheduleRepository.updateFeedScheduleEntry(
                feedProfileId = feedProfile.profileID,
                feedProfileName = feedProfile.profileName,
                repeatDaily = repeatDaily,
                timestampMillis = timestampMillis,
                millisSinceEpoch = millisSinceEpoch
            )
        }
    }

    
    /**
     * Triggers a manual refresh of the schedule data from the remote source.
     */
    fun triggerRefresh() {
        viewModelScope.launch {
            feedScheduleRepository.triggerRefresh()
        }
    }

    
    /**
     * Handles UI events related to the main schedule list screen.
     *
     * @param event The specific [FeedScheduleUiEvent] to process.
     * @return An optional Integer representing a requested Profile ID, or null.
     */
    fun onFeedScheduleUiEvent(event: FeedScheduleUiEvent): Int? {
        return when (event) {
            is FeedScheduleUiEvent.DeleteFeedScheduleEntry -> {
                deleteFeedScheduleEntry(feedScheduleEntry = event.feedScheduleEntry)
                null
            }
            is FeedScheduleUiEvent.Refresh -> {
                triggerRefresh()
                null
            }
            is FeedScheduleUiEvent.GetFeedProfileIdByName -> {
                feedProfilesRepository.findProfileByName(profileName = event.feedProfileName)?.profileID
            }
        }
    }

    
    /**
     * Handles UI events specifically related to the creation or modification of a single schedule entry.
     *
     * @param event The specific [FeedScheduleEntryUiEvent] to process.
     */
    fun onFeedScheduleEntryUiEvent(event: FeedScheduleEntryUiEvent) {
        when (event) {
            is FeedScheduleEntryUiEvent.UpdateFeedScheduleEntry -> {
                if (event.isNewEntry) {
                    addFeedScheduleEntry(
                        feedProfile = event.feedProfile,
                        repeatDaily = event.feedScheduleEntry.isDaily,
                        timestampMillis = event.selectedCalendar.timeInMillis
                    )
                }
                else {
                    updateFeedScheduleEntry(
                        feedProfile = event.feedProfile,
                        repeatDaily = event.feedScheduleEntry.isDaily,
                        timestampMillis = event.selectedCalendar.timeInMillis,
                        millisSinceEpoch = event.millisSinceEpoch
                    )
                }
            }
        }
    }
}
