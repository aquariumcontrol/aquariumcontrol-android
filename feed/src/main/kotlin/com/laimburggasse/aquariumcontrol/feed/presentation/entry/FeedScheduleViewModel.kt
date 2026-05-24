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

@Inject
class FeedScheduleViewModel(
    private val feedScheduleRepository: FeedScheduleRepository,
    private val feedProfilesRepository: FeedProfilesRepository
) : ViewModel() {

    private val _feedProfile = MutableStateFlow<FeedProfile?>(value = null)
    val feedProfile: StateFlow<FeedProfile?> = _feedProfile.asStateFlow()

    private val _isNewScheduleEntry = MutableStateFlow(value = false)
    val isNewScheduleEntry: StateFlow<Boolean> = _isNewScheduleEntry.asStateFlow()

    private val _millisSinceEpoch = MutableStateFlow(value = 0L)
    val millisSinceEpoch: StateFlow<Long> = _millisSinceEpoch.asStateFlow()

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

    fun setScheduleParams(profileId: Int?, isNewEntry: Boolean, millis: Long) {
        _isNewScheduleEntry.value = isNewEntry
        _millisSinceEpoch.value = millis
        if (profileId != null) {
            loadDataForProfile(profileId = profileId)
        }
    }

    private fun loadDataForProfile(profileId: Int) {
        viewModelScope.launch {
            _feedProfile.value = feedProfilesRepository.findProfileById(profileId = profileId)
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

    fun triggerRefresh() {
        viewModelScope.launch {
            feedScheduleRepository.triggerRefresh()
        }
    }

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
