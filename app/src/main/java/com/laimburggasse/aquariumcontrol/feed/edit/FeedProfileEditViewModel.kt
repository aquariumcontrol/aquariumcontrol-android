package com.laimburggasse.aquariumcontrol.feed.edit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.laimburggasse.aquariumcontrol.feed.FeedProfile
import com.laimburggasse.aquariumcontrol.feed.FeedProfilePhase
import com.laimburggasse.aquariumcontrol.feed.FeedProfilesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.math.abs

private const val chartValAllowedDelta = 0.0001

class FeedProfileEditViewModel(
    private val originalFeedProfile: FeedProfile,
    private val feedProfilesRepository: FeedProfilesRepository,
    private val initialExpandedItemIds: Set<Int> = emptySet() // for testing purposes
) : ViewModel() {
    private val _feedProfileUiState = MutableStateFlow(value = originalFeedProfile)
    val feedProfileUiState: StateFlow<FeedProfile> = _feedProfileUiState.asStateFlow()
    private val _expandedItemIds = MutableStateFlow(value = initialExpandedItemIds)
    val expandedItemIds: StateFlow<Set<Int>> = _expandedItemIds.asStateFlow()

    private val _lineDataFeedProfileFeeder = MutableStateFlow<LineData?>(null)
    val lineDataFeedProfileFeeder: StateFlow<LineData?> = _lineDataFeedProfileFeeder.asStateFlow()

    private val _lineDataFeedProfileSkimmer = MutableStateFlow<LineData?>(null)
    val lineDataFeedProfileSkimmer: StateFlow<LineData?> = _lineDataFeedProfileSkimmer.asStateFlow()

    private val _lineDataFeedProfileMainPump1 = MutableStateFlow<LineData?>(null)
    val lineDataFeedProfileMainPump1: StateFlow<LineData?> =
        _lineDataFeedProfileMainPump1.asStateFlow()

    private val _lineDataFeedProfileMainPump2 = MutableStateFlow<LineData?>(null)
    val lineDataFeedProfileMainPump2: StateFlow<LineData?> =
        _lineDataFeedProfileMainPump2.asStateFlow()

    private val _lineDataFeedProfileAuxPump1 = MutableStateFlow<LineData?>(null)
    val lineDataFeedProfileAuxPump1: StateFlow<LineData?> =
        _lineDataFeedProfileAuxPump1.asStateFlow()

    private val _lineDataFeedProfileAuxPump2 = MutableStateFlow<LineData?>(null)
    val lineDataFeedProfileAuxPump2: StateFlow<LineData?> =
        _lineDataFeedProfileAuxPump2.asStateFlow()

    private val _totalDuration = MutableStateFlow(0f)
    val totalDuration: StateFlow<Float> = _totalDuration.asStateFlow()

    init {
        viewModelScope.launch {
            feedProfileUiState.collect { currentProfile ->
                transformFeedProfileToChartData(currentProfile)
                _totalDuration.value = currentProfile.calcDuration()
            }
        }
    }

    private fun transformFeedProfileToChartData(currentProfile: FeedProfile) {
        // Configuration for all chart lines
        val chartLines: List<FeedProfileChartLine> = listOf(
            FeedProfileChartLine(name = "Feeder", signal = FeedProfileSignal.FEEDER,
                lineDataStateFlow = _lineDataFeedProfileFeeder,
                getValue = { phase -> !phase.isPause }, // Feeder is "ON" during feed phases
                yAxisValueOn = FeedProfileDisplayYAxisValueDefinition.FEEDER_ON,
                yAxisValueOff = FeedProfileDisplayYAxisValueDefinition.FEEDER_OFF),
            FeedProfileChartLine(name = "Skimmer", signal = FeedProfileSignal.SKIMMER,
                lineDataStateFlow = _lineDataFeedProfileSkimmer,
                getValue = { phase -> phase.skimmer },
                yAxisValueOn = FeedProfileDisplayYAxisValueDefinition.SKIMMER_ON,
                yAxisValueOff = FeedProfileDisplayYAxisValueDefinition.SKIMMER_OFF),
            FeedProfileChartLine(name = "Main Pump 1", signal = FeedProfileSignal.MAIN_PUMP1,
                lineDataStateFlow = _lineDataFeedProfileMainPump1,
                getValue = { phase -> phase.mainPump1 },
                yAxisValueOn = FeedProfileDisplayYAxisValueDefinition.MAIN_PUMP1_ON,
                yAxisValueOff = FeedProfileDisplayYAxisValueDefinition.MAIN_PUMP1_OFF),
            FeedProfileChartLine(name = "Main Pump 2", signal = FeedProfileSignal.MAIN_PUMP2,
                lineDataStateFlow = _lineDataFeedProfileMainPump2,
                getValue = { phase -> phase.mainPump2 },
                yAxisValueOn = FeedProfileDisplayYAxisValueDefinition.MAIN_PUMP2_ON,
                yAxisValueOff = FeedProfileDisplayYAxisValueDefinition.MAIN_PUMP2_OFF),
            FeedProfileChartLine(name = "Aux. Pump 1", signal = FeedProfileSignal.AUX_PUMP1,
                lineDataStateFlow = _lineDataFeedProfileAuxPump1,
                getValue = { phase -> phase.auxPump1 },
                yAxisValueOn = FeedProfileDisplayYAxisValueDefinition.AUX_PUMP1_ON,
                yAxisValueOff = FeedProfileDisplayYAxisValueDefinition.AUX_PUMP1_OFF),
            FeedProfileChartLine("Aux. Pump 2", signal = FeedProfileSignal.AUX_PUMP2,
                lineDataStateFlow = _lineDataFeedProfileAuxPump2,
                getValue = { phase -> phase.auxPump2 },
                yAxisValueOn = FeedProfileDisplayYAxisValueDefinition.AUX_PUMP2_ON,
                yAxisValueOff = FeedProfileDisplayYAxisValueDefinition.AUX_PUMP2_OFF)
        )

        // A map to hold the list of data points (Entry) for each chart line.
        val valuesMap: Map<FeedProfileChartLine, MutableList<Entry>> = chartLines.associateWith { mutableListOf() }
        var cumulatedTime = 0.0f

        // Add the initial state (time = 0.0) for every line.
        val firstPhase: FeedProfilePhase = currentProfile.feedProfilePhases.first()
        chartLines.forEach { line ->
            val initialValue: Float = if (line.getValue(firstPhase)) line.yAxisValueOn else line.yAxisValueOff
            valuesMap.getValue(key = line).add(Entry(0f, initialValue))
        }

        // Process each phase transition to create the staircase.
        currentProfile.feedProfilePhases.forEach { phase ->
            val phaseDuration: Float = phase.duration.toFloat()

            // If the phase has no duration, it causes no change in time, so we skip it.
            if (phaseDuration <= 0f) return@forEach

            chartLines.forEach { line ->
                val entries: MutableList<Entry> = valuesMap.getValue(line)
                val lastValue: Float = entries.last().y
                val newValue: Float = if (line.getValue(phase)) line.yAxisValueOn else line.yAxisValueOff

                // If the state for this line has changed compared to the last data point...
                if (abs(newValue - lastValue) > chartValAllowedDelta) {
                    // ...add a point at the current time with the PREVIOUS value to create a vertical line.
                    entries.add(Entry(cumulatedTime, lastValue))
                    // And add another point at the SAME time with the NEW value.
                    entries.add(Entry(cumulatedTime, newValue))
                }
            }
            // Move time forward for the next phase.
            cumulatedTime += phaseDuration

            // Add a final point for each line at the end of the current phase's duration.
            chartLines.forEach { line ->
                val entries: MutableList<Entry> = valuesMap.getValue(key = line)
                val newValue: Float = if (line.getValue(phase)) line.yAxisValueOn else line.yAxisValueOff
                // Add a point to "hold" the state for the duration of this phase.
                entries.add(Entry(cumulatedTime, newValue))
            }
        }

        // Create the final LineDataSet for each line and update its StateFlow.
        chartLines.forEach { line ->
            val set: LineDataSet = LineDataSet(valuesMap.getValue(line), line.name).apply {
                color = feedProfileLineColorDefinition(signal = line.signal)
                lineWidth = 2.5f
                axisDependency = YAxis.AxisDependency.LEFT
                setDrawValues(false) // Hide values on the chart
                setDrawCircles(false) // Hide data point circles
            }
            line.lineDataStateFlow.value = LineData(set)
        }
    }

    fun onDurationChange(id: Int, duration: Double) {
        val phaseId: Int = mapMenuItemIdToPhaseId(id)
        val currentProfile: FeedProfile = _feedProfileUiState.value
        val newPhases: MutableList<FeedProfilePhase> = currentProfile.feedProfilePhases.toMutableList()
        if (phaseId >= newPhases.size) return // Safety check (defensive programming)
        val oldPhase: FeedProfilePhase = newPhases[phaseId]
        val newPhase: FeedProfilePhase = oldPhase.copy(duration = duration)
        newPhases[phaseId] = newPhase
        _feedProfileUiState.value = currentProfile.copy(feedProfilePhases = newPhases)
    }

    fun onToggleStateChange(
        id: Int,
        toggleButtonType: FeedProfileEditToggleButtonType,
        isChecked: Boolean
    ) {
        val phaseId: Int = mapMenuItemIdToPhaseId(id)
        val currentProfile: FeedProfile = _feedProfileUiState.value
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
        _feedProfileUiState.value = currentProfile.copy(feedProfilePhases = newPhases)
    }

    fun updateFeedProfile() {
        viewModelScope.launch {
            val profileToUpdate: FeedProfile = _feedProfileUiState.value
            feedProfilesRepository.updateFeedProfile(modifiedFeedProfile = profileToUpdate)
        }
    }

    fun resetFeedProfile() {
        _feedProfileUiState.value = originalFeedProfile
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
}