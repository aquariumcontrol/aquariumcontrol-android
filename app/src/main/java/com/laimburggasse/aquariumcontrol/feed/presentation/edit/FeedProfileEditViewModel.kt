package com.laimburggasse.aquariumcontrol.feed.presentation.edit

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.laimburggasse.aquariumcontrol.feed.data.FeedProfilesRepository
import com.laimburggasse.aquariumcontrol.feed.domain.model.FeedProfile
import com.laimburggasse.aquariumcontrol.feed.domain.model.FeedProfilePhase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FeedProfileEditViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val feedProfilesRepository: FeedProfilesRepository,
) : ViewModel() {
    private var originalFeedProfile: FeedProfile = FeedProfile()

    private val _feedProfileState = MutableStateFlow<FeedProfile?>(value = originalFeedProfile)
    private val _expandedItemIds = MutableStateFlow<Set<Int>>(value = emptySet())

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

    init {
        viewModelScope.launch {
            // Get the profileId from navigation arguments via SavedStateHandle.
            val profileId: Int? = savedStateHandle["feedProfileId"]

            if (profileId != null) {
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
            } else {
                _feedProfileState.value = null // triggers UiState error
            }
        }
    }

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

    fun updateFeedProfile() {
        viewModelScope.launch {
            val profileToUpdate: FeedProfile? = _feedProfileState.value
            if (profileToUpdate != null) {
                feedProfilesRepository.updateFeedProfile(modifiedFeedProfile = profileToUpdate)
            }
        }
    }

    fun resetFeedProfile() {
        _feedProfileState.value = originalFeedProfile
    }

    fun onExpanded(itemId: Int) {
        _expandedItemIds.value = _expandedItemIds.value.let { currentIds ->
            if (itemId in currentIds) {
                currentIds - itemId // Item was expanded, so collapse it by removing its ID
            } else {
                currentIds + itemId // Item was collapsed, so expand it by adding its ID
            }
        }
    }

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