package com.laimburggasse.aquariumcontrol.feed.edit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.laimburggasse.aquariumcontrol.MainViewModel
import com.laimburggasse.aquariumcontrol.feed.FeedProfile
import com.laimburggasse.aquariumcontrol.feed.FeedProfilePhase
import com.laimburggasse.aquariumcontrol.feed.FeedProfilesRepository
import com.laimburggasse.aquariumcontrol.webapi.WebApiPostRequest
import com.laimburggasse.aquariumcontrol.webapi.WebApiUrls
import com.laimburggasse.aquariumcontrol.webapi.webApiPostRequestExecution
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.abs

private const val chartValAllowedDelta = 0.0001

class FeedProfileEditViewModel(
    private val feedProfile: FeedProfile,
    private val feedProfilesRepository: FeedProfilesRepository,
    private val initialExpandedItemIds: Set<Int> = emptySet()
) : ViewModel() {

    private val _feedProfileUiState = MutableStateFlow(value = feedProfile)
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
            transformFeedProfileToChartData()
            _totalDuration.value = feedProfile.calcDuration()
        }
    }

    private fun calcFeederChartValue(isRunning: Boolean): Float {
        return (if (isRunning) FeedProfileDisplayYAxisValueDefinition.FEEDER_ON
        else FeedProfileDisplayYAxisValueDefinition.FEEDER_OFF)
    }

    private fun calcSkimmerChartValue(isRunning: Boolean): Float {
        return (if (isRunning) FeedProfileDisplayYAxisValueDefinition.SKIMMER_ON
        else FeedProfileDisplayYAxisValueDefinition.SKIMMER_OFF)
    }

    private fun calcMainPump1ChartValue(isRunning: Boolean): Float {
        return (if (isRunning) FeedProfileDisplayYAxisValueDefinition.MAIN_PUMP1_ON
        else FeedProfileDisplayYAxisValueDefinition.MAIN_PUMP1_OFF)
    }

    private fun calcMainPump2ChartValue(isRunning: Boolean): Float {
        return (if (isRunning) FeedProfileDisplayYAxisValueDefinition.MAIN_PUMP2_ON
        else FeedProfileDisplayYAxisValueDefinition.MAIN_PUMP2_OFF)
    }

    private fun calcAuxPump1ChartValue(isRunning: Boolean): Float {
        return (if (isRunning) FeedProfileDisplayYAxisValueDefinition.AUX_PUMP1_ON
        else FeedProfileDisplayYAxisValueDefinition.AUX_PUMP1_OFF)
    }

    private fun calcAuxPump2ChartValue(isRunning: Boolean): Float {
        return (if (isRunning) FeedProfileDisplayYAxisValueDefinition.AUX_PUMP2_ON
        else FeedProfileDisplayYAxisValueDefinition.AUX_PUMP2_OFF)
    }

    private fun transformFeedProfileToChartData() {
        val valuesFeeder = ArrayList<Entry>()
        val valuesSkimmer = ArrayList<Entry>()
        val valuesMainPump1 = ArrayList<Entry>()
        val valuesMainPump2 = ArrayList<Entry>()
        val valuesAuxPump1 = ArrayList<Entry>()
        val valuesAuxPump2 = ArrayList<Entry>()

        val itFeedProfilePhases: Iterator<FeedProfilePhase> =
            feedProfile.feedProfilePhases.iterator()

        var feedProfilePhase: FeedProfilePhase
        var cumulatedTime = 0.0

        // except for the feeder, we assume that at feed operation start,
        // the skimmer and pumps are on
        var prevChartValFeeder: Float = FeedProfileDisplayYAxisValueDefinition.FEEDER_OFF
        var prevChartValSkimmer: Float = FeedProfileDisplayYAxisValueDefinition.SKIMMER_ON
        var prevChartValMainPump1: Float = FeedProfileDisplayYAxisValueDefinition.MAIN_PUMP1_ON
        var prevChartValMainPump2: Float = FeedProfileDisplayYAxisValueDefinition.MAIN_PUMP2_ON
        var prevChartValAuxPump1: Float = FeedProfileDisplayYAxisValueDefinition.AUX_PUMP1_ON
        var prevChartValAuxPump2: Float = FeedProfileDisplayYAxisValueDefinition.AUX_PUMP2_ON

        var chartValFeeder: Float
        var chartValSkimmer: Float
        var chartValMainPump1: Float
        var chartValMainPump2: Float
        var chartValAuxPump1: Float
        var chartValAuxPump2: Float

        valuesFeeder.add(
            Entry(
                0.0.toFloat(), prevChartValFeeder
            )
        )
        valuesSkimmer.add(
            Entry(
                0.0.toFloat(), prevChartValSkimmer
            )
        )
        valuesMainPump1.add(
            Entry(
                0.0.toFloat(), prevChartValMainPump1
            )
        )
        valuesMainPump2.add(
            Entry(
                0.0.toFloat(), prevChartValMainPump2
            )
        )
        valuesAuxPump1.add(
            Entry(
                0.0.toFloat(), prevChartValAuxPump1
            )
        )
        valuesAuxPump2.add(
            Entry(
                0.0.toFloat(), prevChartValAuxPump2
            )
        )

        var lastFeedProfilePhaseWithDuration: FeedProfilePhase? = null

        while (itFeedProfilePhases.hasNext()) {
            feedProfilePhase = itFeedProfilePhases.next()
            if (feedProfilePhase.duration == 0.0) {
                continue
            } else {
                lastFeedProfilePhaseWithDuration = feedProfilePhase
            }
            chartValFeeder = calcFeederChartValue(feedProfilePhase.isPause)
            chartValSkimmer = calcSkimmerChartValue(feedProfilePhase.skimmer)
            chartValMainPump1 = calcMainPump1ChartValue(feedProfilePhase.mainPump1)
            chartValMainPump2 = calcMainPump2ChartValue(feedProfilePhase.mainPump2)
            chartValAuxPump1 = calcAuxPump1ChartValue(feedProfilePhase.auxPump1)
            chartValAuxPump2 = calcAuxPump2ChartValue(feedProfilePhase.auxPump2)

            cumulatedTime += feedProfilePhase.duration

            // check if new value is different than previous value and issue additional data point
            if ((abs((prevChartValFeeder - chartValFeeder).toDouble()) > chartValAllowedDelta)) {
                valuesFeeder.add(
                    Entry(
                        cumulatedTime.toFloat(), prevChartValFeeder
                    )
                )
            }
            if ((abs((prevChartValSkimmer - chartValSkimmer).toDouble()) > chartValAllowedDelta)) {
                valuesSkimmer.add(
                    Entry(
                        cumulatedTime.toFloat(), prevChartValSkimmer
                    )
                )
            }
            if ((abs((prevChartValMainPump1 - chartValMainPump1).toDouble()) > chartValAllowedDelta)) {
                valuesMainPump1.add(
                    Entry(
                        cumulatedTime.toFloat(), prevChartValMainPump1
                    )
                )
            }
            if ((abs((prevChartValMainPump2 - chartValMainPump2).toDouble()) > chartValAllowedDelta)) {
                valuesMainPump2.add(
                    Entry(
                        cumulatedTime.toFloat(), prevChartValMainPump2
                    )
                )
            }
            if ((abs((prevChartValAuxPump1 - chartValAuxPump1).toDouble()) > chartValAllowedDelta)) {
                valuesAuxPump1.add(
                    Entry(
                        cumulatedTime.toFloat(), prevChartValAuxPump1
                    )
                )
            }
            if ((abs((prevChartValAuxPump2 - chartValAuxPump2).toDouble()) > chartValAllowedDelta)) {
                valuesAuxPump2.add(
                    Entry(
                        cumulatedTime.toFloat(), prevChartValAuxPump2
                    )
                )
            }

            valuesFeeder.add(Entry(cumulatedTime.toFloat(), chartValFeeder))
            valuesSkimmer.add(Entry(cumulatedTime.toFloat(), chartValSkimmer))
            valuesMainPump1.add(Entry(cumulatedTime.toFloat(), chartValMainPump1))
            valuesMainPump2.add(Entry(cumulatedTime.toFloat(), chartValMainPump2))
            valuesAuxPump1.add(Entry(cumulatedTime.toFloat(), chartValAuxPump1))
            valuesAuxPump2.add(Entry(cumulatedTime.toFloat(), chartValAuxPump2))

            prevChartValFeeder = chartValFeeder
            prevChartValSkimmer = chartValSkimmer
            prevChartValMainPump1 = chartValMainPump1
            prevChartValMainPump2 = chartValMainPump2
            prevChartValAuxPump1 = chartValAuxPump1
            prevChartValAuxPump2 = chartValAuxPump2

        }
        val increment = 0.0000001f
        valuesFeeder.add(
            Entry(
                cumulatedTime.toFloat() + increment,
                FeedProfileDisplayYAxisValueDefinition.FEEDER_OFF
            )
        )
        if (lastFeedProfilePhaseWithDuration != null) {
            if (!lastFeedProfilePhaseWithDuration.skimmer) {
                valuesSkimmer.add(
                    Entry(
                        cumulatedTime.toFloat() + increment,
                        FeedProfileDisplayYAxisValueDefinition.SKIMMER_ON
                    )
                )
            }
            if (!lastFeedProfilePhaseWithDuration.mainPump1) {
                valuesMainPump1.add(
                    Entry(
                        cumulatedTime.toFloat() + increment,
                        FeedProfileDisplayYAxisValueDefinition.MAIN_PUMP1_ON
                    )
                )
            }
            if (!lastFeedProfilePhaseWithDuration.mainPump2) {
                valuesMainPump2.add(
                    Entry(
                        cumulatedTime.toFloat() + increment,
                        FeedProfileDisplayYAxisValueDefinition.MAIN_PUMP2_ON
                    )
                )
            }
            if (!lastFeedProfilePhaseWithDuration.auxPump1) {
                valuesAuxPump1.add(
                    Entry(
                        cumulatedTime.toFloat() + increment,
                        FeedProfileDisplayYAxisValueDefinition.AUX_PUMP1_ON
                    )
                )
            }
            if (!lastFeedProfilePhaseWithDuration.auxPump2) {
                valuesAuxPump2.add(
                    Entry(
                        cumulatedTime.toFloat() + increment,
                        FeedProfileDisplayYAxisValueDefinition.AUX_PUMP2_ON
                    )
                )
            }
        }

        // create a dataset and give it a type
        val setFeeder = LineDataSet(valuesFeeder, "Feeder")
        val setSkimmer = LineDataSet(valuesSkimmer, "Skimmer")
        val setMainPump1 = LineDataSet(valuesMainPump1, "Main Pump 1")
        val setMainPump2 = LineDataSet(valuesMainPump2, "Main Pump 2")
        val setAuxPump1 = LineDataSet(valuesAuxPump1, "Aux. Pump 1")
        val setAuxPump2 = LineDataSet(valuesAuxPump2, "Aux. Pump 2")

        setFeeder.color = feedProfileLineColorDefinition(FeedProfileSignal.FEEDER)
        setSkimmer.color = feedProfileLineColorDefinition(FeedProfileSignal.SKIMMER)
        setMainPump1.color = feedProfileLineColorDefinition(FeedProfileSignal.MAIN_PUMP1)
        setMainPump2.color = feedProfileLineColorDefinition(FeedProfileSignal.MAIN_PUMP2)
        setAuxPump1.color = feedProfileLineColorDefinition(FeedProfileSignal.AUX_PUMP1)
        setAuxPump2.color = feedProfileLineColorDefinition(FeedProfileSignal.AUX_PUMP2)

        setFeeder.lineWidth = 2.5f
        setSkimmer.lineWidth = 2.5f
        setMainPump1.lineWidth = 2.5f
        setMainPump2.lineWidth = 2.5f
        setAuxPump1.lineWidth = 2.5f
        setAuxPump2.lineWidth = 2.5f

        setFeeder.axisDependency = YAxis.AxisDependency.LEFT
        setSkimmer.axisDependency = YAxis.AxisDependency.LEFT
        setMainPump1.axisDependency = YAxis.AxisDependency.LEFT
        setMainPump2.axisDependency = YAxis.AxisDependency.LEFT
        setAuxPump1.axisDependency = YAxis.AxisDependency.LEFT
        setAuxPump2.axisDependency = YAxis.AxisDependency.LEFT

        setFeeder.setDrawValues(false)
        setSkimmer.setDrawValues(false)
        setMainPump1.setDrawValues(false)
        setMainPump2.setDrawValues(false)
        setAuxPump1.setDrawValues(false)
        setAuxPump2.setDrawValues(false)

        setFeeder.setDrawCircles(false)
        setSkimmer.setDrawCircles(false)
        setMainPump1.setDrawCircles(false)
        setMainPump2.setDrawCircles(false)
        setAuxPump1.setDrawCircles(false)
        setAuxPump2.setDrawCircles(false)

        setFeeder.mode = LineDataSet.Mode.LINEAR
        setSkimmer.mode = LineDataSet.Mode.LINEAR
        setMainPump1.mode = LineDataSet.Mode.LINEAR
        setMainPump2.mode = LineDataSet.Mode.LINEAR
        setAuxPump1.mode = LineDataSet.Mode.LINEAR
        setAuxPump2.mode = LineDataSet.Mode.LINEAR

        setFeeder.setDrawFilled(false)
        setSkimmer.setDrawFilled(false)
        setMainPump1.setDrawFilled(false)
        setMainPump2.setDrawFilled(false)
        setAuxPump1.setDrawFilled(false)
        setAuxPump2.setDrawFilled(false)

        // create data objects with the data sets
        val tempLineDataSetFeeder = LineData(setFeeder)
        val tempLineDataSetSkimmer = LineData(setSkimmer)
        val tempLineDataSetMainPump1 = LineData(setMainPump1)
        val tempLineDataSetMainPump2 = LineData(setMainPump2)
        val tempLineDataSetAuxPump1 = LineData(setAuxPump1)
        val tempLineDataSetAuxPump2 = LineData(setAuxPump2)

        _lineDataFeedProfileFeeder.value = tempLineDataSetFeeder
        _lineDataFeedProfileSkimmer.value = tempLineDataSetSkimmer
        _lineDataFeedProfileMainPump1.value = tempLineDataSetMainPump1
        _lineDataFeedProfileMainPump2.value = tempLineDataSetMainPump2
        _lineDataFeedProfileAuxPump1.value = tempLineDataSetAuxPump1
        _lineDataFeedProfileAuxPump2.value = tempLineDataSetAuxPump2
    }

    fun onDurationChange(phaseId: Int, duration: Double) {
        feedProfile.feedProfilePhases[phaseId].duration = duration
        transformFeedProfileToChartData()
        _totalDuration.value = feedProfile.calcDuration()
    }

    fun onToggleStateChange(
        phaseId: Int,
        toggleButtonType: FeedProfileEditToggleButtonType,
        isChecked: Boolean
    ) {
        when (toggleButtonType) {
            FeedProfileEditToggleButtonType.Skimmer -> feedProfile.feedProfilePhases[phaseId].skimmer =
                isChecked

            FeedProfileEditToggleButtonType.MainPump1 -> feedProfile.feedProfilePhases[phaseId].mainPump1 =
                isChecked

            FeedProfileEditToggleButtonType.MainPump2 -> feedProfile.feedProfilePhases[phaseId].mainPump2 =
                isChecked

            FeedProfileEditToggleButtonType.AuxPump1 -> feedProfile.feedProfilePhases[phaseId].auxPump1 =
                isChecked

            FeedProfileEditToggleButtonType.AuxPump2 -> feedProfile.feedProfilePhases[phaseId].auxPump2 =
                isChecked
        }
        transformFeedProfileToChartData()
        _totalDuration.value = feedProfile.calcDuration()
    }

    fun updateFeedProfile(mainViewModel: MainViewModel) {
        viewModelScope.launch {
            val apiCallParameters: Map<String, String> =
                feedProfile.getFeedProfileApiCallParameters()
            withContext(context = Dispatchers.IO) {
                // @todo: move this code to repository
                webApiPostRequestExecution(
                    mainViewModel = mainViewModel,
                    apiUrl = WebApiUrls.FEED_POST_PROFILE_UPDATE,
                    webApiPostRequest = WebApiPostRequest(),
                    optionalGetParams = apiCallParameters
                )
            }
        }
    }

    fun resetFeedProfile() {
        // @todo: recreate this functionality elsewhere
        // Re-create the menu items and chart data to discard any changes
        // phaseMenuItems = createFeedProfileEditMenuItems(currentFeedProfile = feedProfile)
        transformFeedProfileToChartData()
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