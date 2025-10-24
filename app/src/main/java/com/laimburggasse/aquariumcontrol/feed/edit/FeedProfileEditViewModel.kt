package com.laimburggasse.aquariumcontrol.feed.edit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.mikephil.charting.data.LineData
import com.laimburggasse.aquariumcontrol.feed.common.FeedProfile
import com.laimburggasse.aquariumcontrol.feed.common.FeedProfilePhase
import com.laimburggasse.aquariumcontrol.feed.common.FeedProfilesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class FeedProfileEditViewModel(
    private val originalFeedProfile: FeedProfile,
    private val feedProfilesRepository: FeedProfilesRepository,
    private val initialExpandedItemIds: Set<Int> = emptySet() // for testing purposes
) : ViewModel() {
    private val _feedProfileState = MutableStateFlow(value = originalFeedProfile)
    private val _expandedItemIds = MutableStateFlow(value = initialExpandedItemIds)
    private val _lineDataFeeder = MutableStateFlow<LineData?>(value = null)
    private val _lineDataSkimmer = MutableStateFlow<LineData?>(value = null)
    private val _lineDataMainPump1 = MutableStateFlow<LineData?>(value = null)
    private val _lineDataMainPump2 = MutableStateFlow<LineData?>(value = null)
    private val _lineDataAuxPump1 = MutableStateFlow<LineData?>(value = null)
    private val _lineDataAuxPump2 = MutableStateFlow<LineData?>(value = null)
    private val _totalDuration = MutableStateFlow(value = 0f)

    val uiState: StateFlow<FeedProfileEditUiState> = combine(
        _feedProfileState, _expandedItemIds, _lineDataFeeder, _lineDataSkimmer,
        _lineDataMainPump1, _lineDataMainPump2, _lineDataAuxPump1, _lineDataAuxPump2,
        _totalDuration
    ) {
        // due to high number of flows, no named parameters are available
        latestValues: Array<Any?> ->

        val feedProfile = latestValues[0] as FeedProfile
        val expandedIds: Set<Int> = latestValues[1] as Set<Int>
        val feeder = latestValues[2] as? LineData
        val skimmer = latestValues[3] as? LineData
        val mainPump1 = latestValues[4] as? LineData
        val mainPump2 = latestValues[5] as? LineData
        val auxPump1 = latestValues[6] as? LineData
        val auxPump2 = latestValues[7] as? LineData
        val totalDuration = latestValues[8] as Float

        FeedProfileEditUiState(
            feedProfile = feedProfile,
            expandedItemIds = expandedIds,
            lineDataFeeder = feeder,
            lineDataSkimmer = skimmer,
            lineDataMainPump1 = mainPump1,
            lineDataMainPump2 = mainPump2,
            lineDataAuxPump1 = auxPump1,
            lineDataAuxPump2 = auxPump2,
            totalDuration = totalDuration
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = FeedProfileEditUiState() // Start with a default, empty state
    )
    
    init {
        viewModelScope.launch {
            _feedProfileState.collect { currentProfile ->
                val chartBundle: FeedProfileChartDataBundle = FeedProfileChartDataFactory.create(currentProfile)
                _lineDataFeeder.value = chartBundle.lineDataFeeder
                _lineDataSkimmer.value = chartBundle.lineDataSkimmer
                _lineDataMainPump1.value = chartBundle.lineDataMainPump1
                _lineDataMainPump2.value = chartBundle.lineDataMainPump2
                _lineDataAuxPump1.value = chartBundle.lineDataAuxPump1
                _lineDataAuxPump2.value = chartBundle.lineDataAuxPump2
                _totalDuration.value = chartBundle.totalDuration
            }
        }
    }

    fun onDurationChange(id: Int, duration: Double) {
        val phaseId: Int = mapMenuItemIdToPhaseId(id)
        val currentProfile: FeedProfile = _feedProfileState.value
        val newPhases: MutableList<FeedProfilePhase> = currentProfile.feedProfilePhases.toMutableList()
        if (phaseId >= newPhases.size) return // Safety check (defensive programming)
        val oldPhase: FeedProfilePhase = newPhases[phaseId]
        val newPhase: FeedProfilePhase = oldPhase.copy(duration = duration)
        newPhases[phaseId] = newPhase
        _feedProfileState.value = currentProfile.copy(feedProfilePhases = newPhases)
    }

    fun onToggleStateChange(
        id: Int,
        toggleButtonType: FeedProfileEditToggleButtonType,
        isChecked: Boolean
    ) {
        val phaseId: Int = mapMenuItemIdToPhaseId(id)
        val currentProfile: FeedProfile = _feedProfileState.value
        val newPhases: MutableList<FeedProfilePhase> = currentProfile.feedProfilePhases.toMutableList()
        if (phaseId >= newPhases.size) return // Safety check (defensive programming)
        val oldPhase: FeedProfilePhase = newPhases[phaseId]
        val newPhase: FeedProfilePhase = when (toggleButtonType) {
            FeedProfileEditToggleButtonType.Skimmer -> oldPhase.copy(skimmer = isChecked)
            FeedProfileEditToggleButtonType.MainPump1 -> oldPhase.copy(mainPump1 = isChecked)
            FeedProfileEditToggleButtonType.MainPump2 -> oldPhase.copy(mainPump2 = isChecked)
            FeedProfileEditToggleButtonType.AuxPump1 -> oldPhase.copy(auxPump1 = isChecked)
            FeedProfileEditToggleButtonType.AuxPump2 -> oldPhase.copy(auxPump2 = isChecked)
        }
        newPhases[phaseId] = newPhase
        _feedProfileState.value = currentProfile.copy(feedProfilePhases = newPhases)
    }

    fun updateFeedProfile() {
        viewModelScope.launch {
            val profileToUpdate: FeedProfile = _feedProfileState.value
            feedProfilesRepository.updateFeedProfile(modifiedFeedProfile = profileToUpdate)
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