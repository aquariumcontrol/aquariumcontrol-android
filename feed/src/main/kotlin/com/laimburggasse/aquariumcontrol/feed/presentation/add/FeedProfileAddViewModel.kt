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

package com.laimburggasse.aquariumcontrol.feed.presentation.add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.laimburggasse.aquariumcontrol.feed.domain.model.FeedProfile
import com.laimburggasse.aquariumcontrol.feed.domain.repository.FeedProfilesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Inject

class FeedProfileAddViewModel @Inject constructor(
    private val feedProfilesRepository: FeedProfilesRepository
) : ViewModel() {
    private val _feedProfileName = MutableStateFlow(value = "")
    private val _feedProfileNameExists = MutableStateFlow(value = false)
    val feedProfileName: StateFlow<String> = _feedProfileName.asStateFlow()
    val feedProfileNameExists: StateFlow<Boolean> = _feedProfileNameExists.asStateFlow()

    private val _feedProfileAddUiState = MutableStateFlow<FeedProfileAddUiState>(
        value = FeedProfileAddUiState.Idle
    )
    val feedProfileAddUiState: StateFlow<FeedProfileAddUiState> = _feedProfileAddUiState.asStateFlow()

    fun setFeedProfileName(name: String) {
        _feedProfileName.value = name // Update the StateFlow
        val existingFeedProfile: FeedProfile? = feedProfilesRepository.findProfileByName(profileName = name)
        _feedProfileNameExists.value = existingFeedProfile != null
    }

    fun onStateHandled() {
        _feedProfileAddUiState.value = FeedProfileAddUiState.Idle
    }

    fun addFeedProfile() {
        if (_feedProfileAddUiState.value is FeedProfileAddUiState.Loading) return
        _feedProfileAddUiState.value = FeedProfileAddUiState.Loading

        viewModelScope.launch {
            feedProfilesRepository.addFeedProfile(feedProfileName = feedProfileName.value)
            _feedProfileAddUiState.value = FeedProfileAddUiState.Success
        }
    }

    fun onFeedProfileAddEvent(event: FeedProfileAddUiEvent) {
        when (event) {
            is FeedProfileAddUiEvent.Add -> {
                addFeedProfile()
            }
            is FeedProfileAddUiEvent.HandleState -> {
                onStateHandled()
            }
            is FeedProfileAddUiEvent.SetFeedProfileName -> {
                setFeedProfileName(event.name)
            }
            is FeedProfileAddUiEvent.Success -> {

            }
        }
    }
}