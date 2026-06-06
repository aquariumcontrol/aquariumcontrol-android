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

package com.laimburggasse.aquariumcontrol.feed.presentation.edit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.laimburggasse.aquariumcontrol.feed.domain.model.FeedProfile
import com.laimburggasse.aquariumcontrol.feed.domain.model.FeedProfilePhase
import com.laimburggasse.aquariumcontrol.feed.domain.repository.FeedProfilesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Inject

/**
 * ViewModel for the Feed profile editing screen.
 * Manages the state, visual chart data, and modifications for an existing feed profile.
 *
 * @property feedProfilesRepository The repository providing feed profile data.
 */
class FeedProfileEditViewModel @Inject constructor(
    private val feedProfilesRepository: FeedProfilesRepository,
) : ViewModel() {
    /** The untouched profile used as a backup for the reset functionality. */
    private var originalFeedProfile: FeedProfile = FeedProfile()

    /** Internal mutable state holding the current (optimistic) profile modifications. */
    private val _feedProfileState = MutableStateFlow<FeedProfile?>(value = originalFeedProfile)

    /** Internal mutable state tracking which UI menu items are expanded. */
    private val _expandedItemIds = MutableStateFlow<Set<Int>>(value = emptySet())

    /** Public read-only UI state combining profile data, expanded states, and chart configurations. */
    val uiState: StateFlow<FeedProfileEditUiState> = combine(
        flow = _feedProfileState, flow2 = _expandedItemIds
    ) { feedProfile, expandedItemIds ->
        if (feedProfile != null) {
            val chartBundle: FeedProfileChartDataBundle = FeedProfileChartDataFactory.create(feedProfile)

            FeedProfileEditUiState(
                feedProfile = feedProfile,
                expandedItemIds = expandedItemIds,
                lineDataFeeder = chartBundle.lineDataFeeder,
                lineDataSkimmer = chartBundle.lineDataSkimmer,
                lineDataMainPump1 = chartBundle.lineDataMainPump1,
                lineDataMainPump2 = chartBundle.lineDataMainPump2,
                lineDataAuxPump1 = chartBundle.lineDataAuxPump1,
                lineDataAuxPump2 = chartBundle.lineDataAuxPump2,
                totalDuration = chartBundle.totalDuration
            )
        }
        else {
            FeedProfileEditUiState()
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
        initialValue = FeedProfileEditUiState() // Start with a default, empty state
    )

    
    /**
     * Initializes the ViewModel with a specific profile ID, loading it from the repository.
     *
     * @param profileId The ID of the feed profile to edit.
     */
    fun setProfileId(profileId: Int) {
        viewModelScope.launch {
            // Fetch the initial FeedProfile from the repository.
            val profile: FeedProfile? = feedProfilesRepository.findProfileById(profileId)

            if (profile != null) {
                // Store the fetched profile as the "original" for the reset functionality.
                originalFeedProfile = profile

                // Update the state flow for the feed profile, which will trigger the 'combine' operator.
                _feedProfileState.value = profile
            } else {
                _feedProfileState.value = null // triggers UiState error
            }
        }
    }

    
    /**
     * Updates the duration of a specific feed profile phase.
     *
     * @param id The UI menu item ID corresponding to the phase.
     * @param duration The new duration in seconds.
     */
    fun onDurationChange(id: Int, duration: Double) {
        val phaseId: Int = mapMenuItemIdToPhaseId(id)

        _feedProfileState.update { currentProfile ->
            if (currentProfile != null) {
                // Create a new list using mapIndexed.
                val newPhases: List<FeedProfilePhase> =
                    currentProfile.feedProfilePhases.mapIndexed { index, currentPhase ->
                        if (index == phaseId) {
                            // If this is the phase we want to change, return a new, copied phase.
                            currentPhase.copy(duration = duration)
                        } else {
                            // Otherwise, return the existing, unchanged phase.
                            currentPhase
                        }
                    }
                // Return a new profile object containing the new list.
                currentProfile.copy(feedProfilePhases = newPhases)
            }
            else {
                currentProfile
            }
        }
    }

    
    /**
     * Updates the boolean state of a specific actuator within a phase.
     *
     * @param id The UI menu item ID corresponding to the phase.
     * @param toggleButtonType The type of actuator being toggled (e.g., Skimmer, MainPump1).
     * @param isChecked The new boolean state.
     */
    fun onToggleStateChange(
        id: Int,
        toggleButtonType: FeedProfileEditToggleButtonType,
        isChecked: Boolean
    ) {
        val phaseId: Int = mapMenuItemIdToPhaseId(id)
        _feedProfileState.update { currentProfile ->
            if (currentProfile != null) {
                val newPhases: List<FeedProfilePhase> =
                    currentProfile.feedProfilePhases.mapIndexed { index, currentPhase ->
                        if (index == phaseId) {
                            when (toggleButtonType) {
                                FeedProfileEditToggleButtonType.Skimmer -> currentPhase.copy(
                                    skimmer = isChecked
                                )
                                FeedProfileEditToggleButtonType.MainPump1 -> currentPhase.copy(
                                    mainPump1 = isChecked
                                )
                                FeedProfileEditToggleButtonType.MainPump2 -> currentPhase.copy(
                                    mainPump2 = isChecked
                                )
                                FeedProfileEditToggleButtonType.AuxPump1 -> currentPhase.copy(
                                    auxPump1 = isChecked
                                )
                                FeedProfileEditToggleButtonType.AuxPump2 -> currentPhase.copy(
                                    auxPump2 = isChecked
                                )
                            }
                        } else {
                            currentPhase
                        }
                    }
                currentProfile.copy(feedProfilePhases = newPhases)
            }
            else {
                currentProfile
            }
        }
    }

    
    /**
     * Commits the current optimistic modifications to the repository.
     */
    fun updateFeedProfile() {
        viewModelScope.launch {
            val profileToUpdate: FeedProfile? = _feedProfileState.value
            if (profileToUpdate != null) {
                feedProfilesRepository.updateFeedProfile(modifiedFeedProfile = profileToUpdate)
            }
        }
    }

    
    /**
     * Discards any current modifications and reverts the state back to the original profile.
     */
    fun resetFeedProfile() {
        _feedProfileState.value = originalFeedProfile
    }

    
    /**
     * Toggles the expansion state of a specific UI menu item.
     *
     * @param itemId The ID of the UI element to toggle.
     */
    fun onExpanded(itemId: Int) {
        _expandedItemIds.value = _expandedItemIds.value.let { currentIds ->
            if (itemId in currentIds) {
                currentIds - itemId // Item was expanded, so collapse it by removing its ID
            } else {
                currentIds + itemId // Item was collapsed, so expand it by adding its ID
            }
        }
    }

    
    /**
     * Central event handler for all UI interactions on the feed profile edit screen.
     *
     * @param event The specific [FeedProfileEditUiEvent] to process.
     */
    fun onFeedProfileEditUiEvent(event: FeedProfileEditUiEvent) {
        when (event) {
            is FeedProfileEditUiEvent.DurationChange -> {
                onDurationChange(id = event.menuItemId, duration = event.newDuration)
            }
            is FeedProfileEditUiEvent.SwitchToggle -> {
                onToggleStateChange(
                    id = event.menuItemId,
                    toggleButtonType = event.toggleButtonType,
                    isChecked = event.isChecked
                )
            }
            is FeedProfileEditUiEvent.ExpandChildren -> {
                onExpanded(itemId = event.menuItemId)
            }
            is FeedProfileEditUiEvent.Update -> {
                updateFeedProfile()
            }
            is FeedProfileEditUiEvent.Reset -> {
                resetFeedProfile()
            }
        }
    }
}