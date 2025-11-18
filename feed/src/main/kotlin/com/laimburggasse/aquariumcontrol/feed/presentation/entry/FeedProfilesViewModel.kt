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
import com.laimburggasse.aquariumcontrol.feed.domain.model.FeedProfile
import com.laimburggasse.aquariumcontrol.feed.domain.repository.FeedProfilesRepository
import com.laimburggasse.aquariumcontrol.common.data.remote.DataFetchResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FeedProfilesViewModel @Inject constructor(
    val feedProfilesRepository: FeedProfilesRepository
) : ViewModel() {

    val feedProfilesUiState: StateFlow<FeedProfilesUiState> = feedProfilesRepository.profilesFlow
        .map { result ->
            when (result) {
                is DataFetchResult.Error -> FeedProfilesUiState.Error(errorMessage = result.errorMessage)
                is DataFetchResult.Success -> {
                    if (result.data.isEmpty()) {
                        FeedProfilesUiState.Empty
                    } else {
                        FeedProfilesUiState.Success(feedProfiles = result.data)
                    }
                }
                is DataFetchResult.Loading -> FeedProfilesUiState.Loading
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.Companion.Eagerly,
            initialValue = FeedProfilesUiState.Loading // The UI starts in a loading state
        )

    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            feedProfilesRepository.triggerRefresh()
        }
    }

    fun deleteFeedProfile(feedProfile: FeedProfile) {
        viewModelScope.launch {
            feedProfilesRepository.deleteFeedProfile(feedProfile)
        }
    }

    fun onFeedProfilesUiEvent(event: FeedProfilesUiEvent) {
        when (event) {
            is FeedProfilesUiEvent.DeleteFeedProfile -> {
                deleteFeedProfile(feedProfile = event.feedProfile)
            }
            is FeedProfilesUiEvent.Refresh -> {
                loadData()
            }
        }
    }
}