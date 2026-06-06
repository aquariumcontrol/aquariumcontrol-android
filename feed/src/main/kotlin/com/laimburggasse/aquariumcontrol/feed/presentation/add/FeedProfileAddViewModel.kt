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

/**
 * ViewModel for the Feed profile creation screen.
 * Handles the state and logic for configuring and adding a new feed profile.
 *
 * @property feedProfilesRepository The repository for managing feed profiles.
 */
class FeedProfileAddViewModel @Inject constructor(
    private val feedProfilesRepository: FeedProfilesRepository
) : ViewModel() {
    /** Internal mutable state for the new feed profile's name. */
    private val _feedProfileName = MutableStateFlow(value = "")

    /** Internal mutable state tracking if the chosen feed profile name already exists. */
    private val _feedProfileNameExists = MutableStateFlow(value = false)

    /** Public read-only state for the new feed profile's name. */
    val feedProfileName: StateFlow<String> = _feedProfileName.asStateFlow()

    /** Public read-only state indicating if the currently entered profile name already exists. */
    val feedProfileNameExists: StateFlow<Boolean> = _feedProfileNameExists.asStateFlow()

    /** Internal mutable UI state representing the status of the addition process. */
    private val _feedProfileAddUiState = MutableStateFlow<FeedProfileAddUiState>(
        value = FeedProfileAddUiState.Idle
    )

    /** Public read-only UI state for the feed profile addition process. */
    val feedProfileAddUiState: StateFlow<FeedProfileAddUiState> = _feedProfileAddUiState.asStateFlow()

    
    /**
     * Updates the feed profile name and checks if a profile with this name already exists.
     *
     * @param name The new profile name entered by the user.
     */
    fun setFeedProfileName(name: String) {
        _feedProfileName.value = name // Update the StateFlow
        val existingFeedProfile: FeedProfile? = feedProfilesRepository.findProfileByName(profileName = name)
        _feedProfileNameExists.value = existingFeedProfile != null
    }

    
    /**
     * Resets the UI state back to idle after a state change has been processed by the view.
     */
    fun onStateHandled() {
        _feedProfileAddUiState.value = FeedProfileAddUiState.Idle
    }

    
    /**
     * Initiates the process to add the new feed profile to the repository.
     * Sets the state to Loading while the operation is in progress, and to Success upon completion.
     */
    fun addFeedProfile() {
        if (_feedProfileAddUiState.value is FeedProfileAddUiState.Loading) return
        _feedProfileAddUiState.value = FeedProfileAddUiState.Loading

        viewModelScope.launch {
            feedProfilesRepository.addFeedProfile(feedProfileName = feedProfileName.value)
            _feedProfileAddUiState.value = FeedProfileAddUiState.Success
        }
    }

    
    /**
     * Central event handler for all UI interactions on the feed profile addition screen.
     *
     * @param event The specific [FeedProfileAddUiEvent] to process.
     */
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