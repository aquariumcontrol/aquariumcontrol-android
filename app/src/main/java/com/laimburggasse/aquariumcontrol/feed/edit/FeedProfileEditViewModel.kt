package com.laimburggasse.aquariumcontrol.feed.edit

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.laimburggasse.aquariumcontrol.feed.FeedProfile
import com.laimburggasse.aquariumcontrol.feed.FeedProfilePhase
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.laimburggasse.aquariumcontrol.MainViewModel
import com.laimburggasse.aquariumcontrol.webapi.WebApiPostRequest
import com.laimburggasse.aquariumcontrol.webapi.WebApiUrls
import com.laimburggasse.aquariumcontrol.webapi.webApiPostRequestExecution
import kotlinx.coroutines.launch
import kotlin.math.abs

class FeedProfileEditViewModel(private val myFeedProfile: FeedProfile) : ViewModel() {

    var phaseMenuItems: List<FeedProfileEditMenuItem>

    private var _lineDataFeedProfileFeeder = MutableLiveData<LineData>()
    private var _lineDataFeedProfileSkimmer = MutableLiveData<LineData>()
    private var _lineDataFeedProfileMainPump1 = MutableLiveData<LineData>()
    private var _lineDataFeedProfileMainPump2 = MutableLiveData<LineData>()
    private var _lineDataFeedProfileAuxPump1 = MutableLiveData<LineData>()
    private var _lineDataFeedProfileAuxPump2 = MutableLiveData<LineData>()
    private var _totalDuration = MutableLiveData<Float>()

    var lineDataFeedProfileFeeder: LiveData<LineData> = _lineDataFeedProfileFeeder
    var lineDataFeedProfileSkimmer: LiveData<LineData> = _lineDataFeedProfileSkimmer
    var lineDataFeedProfileMainPump1: LiveData<LineData> = _lineDataFeedProfileMainPump1
    var lineDataFeedProfileMainPump2: LiveData<LineData> = _lineDataFeedProfileMainPump2
    var lineDataFeedProfileAuxPump1: LiveData<LineData> = _lineDataFeedProfileAuxPump1
    var lineDataFeedProfileAuxPump2: LiveData<LineData> = _lineDataFeedProfileAuxPump2
    var totalDuration: LiveData<Float> = _totalDuration

    init {
        phaseMenuItems = createFeedProfileEditMenuItems()
        transformFeedProfileToChartData()
        _totalDuration.value = myFeedProfile.calcDuration()
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
            myFeedProfile.feedProfilePhases.iterator()

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

    private fun createFeedProfileEditMenuItems(): List<FeedProfileEditMenuItem> {
        return List(10) { index ->
            FeedProfileEditMenuItem(
                id = index,
                level = 1,
                text = "Phase ${index + 1}",
                isToggleButton = false,
                children = mutableListOf(
                    FeedProfileEditMenuItem(
                        id = 0,
                        level = 2,
                        text = "Pause",
                        isToggleButton = false,
                        phaseType = FeedProfileEditPhaseType.PAUSE,
                        children = mutableListOf(
                            FeedProfileEditMenuItem(
                                id = index * 2,
                                level = 3,
                                text = "Duration",
                                isNumericInput = true,
                                phaseType = FeedProfileEditPhaseType.PAUSE,
                                durationInitialValue = myFeedProfile.feedProfilePhases[index * 2].duration,
                                isToggleButton = false,
                            ),
                            FeedProfileEditMenuItem(
                                id = index * 2,
                                level = 3,
                                text = "Skimmer",
                                toggleButtonType = FeedProfileEditToggleButtonType.Skimmer,
                                isToggleButton = true,
                                isNumericInput = false,
                                phaseType = FeedProfileEditPhaseType.PAUSE,
                                isChecked = mutableStateOf(
                                    myFeedProfile.feedProfilePhases[index * 2].skimmer
                                ),
                            ),
                            FeedProfileEditMenuItem(
                                id = index * 2,
                                level = 3,
                                text = "Main Pump 1",
                                toggleButtonType = FeedProfileEditToggleButtonType.MainPump1,
                                isToggleButton = true,
                                isNumericInput = false,
                                phaseType = FeedProfileEditPhaseType.PAUSE,
                                isChecked = mutableStateOf(
                                    myFeedProfile.feedProfilePhases[index * 2].mainPump1
                                ),
                            ),
                            FeedProfileEditMenuItem(
                                id = index * 2,
                                level = 3,
                                text = "Main Pump 2",
                                toggleButtonType = FeedProfileEditToggleButtonType.MainPump2,
                                isToggleButton = true,
                                isNumericInput = false,
                                phaseType = FeedProfileEditPhaseType.PAUSE,
                                isChecked = mutableStateOf(
                                    myFeedProfile.feedProfilePhases[index * 2].mainPump2
                                ),
                            ),
                            FeedProfileEditMenuItem(
                                id = index * 2,
                                level = 3,
                                text = "Aux. Pump 1",
                                toggleButtonType = FeedProfileEditToggleButtonType.AuxPump1,
                                isToggleButton = true,
                                isNumericInput = false,
                                phaseType = FeedProfileEditPhaseType.PAUSE,
                                isChecked = mutableStateOf(
                                    myFeedProfile.feedProfilePhases[index * 2].auxPump1
                                ),
                            ),
                            FeedProfileEditMenuItem(
                                id = index * 2,
                                level = 3,
                                text = "Aux. Pump 2",
                                toggleButtonType = FeedProfileEditToggleButtonType.AuxPump2,
                                isToggleButton = true,
                                isNumericInput = false,
                                phaseType = FeedProfileEditPhaseType.PAUSE,
                                isChecked = mutableStateOf(
                                    myFeedProfile.feedProfilePhases[index * 2].auxPump2
                                ),
                            ),
                        ),
                    ),
                    FeedProfileEditMenuItem(
                        id = 1,
                        level = 2,
                        text = "Feed",
                        isToggleButton = false,
                        phaseType = FeedProfileEditPhaseType.FEED,
                        children = mutableListOf(
                            FeedProfileEditMenuItem(
                                id = index * 2 + 1,
                                level = 3,
                                text = "Duration",
                                isToggleButton = false,
                                isNumericInput = true,
                                phaseType = FeedProfileEditPhaseType.FEED,
                                durationInitialValue = myFeedProfile.feedProfilePhases[index * 2 + 1].duration,
                            ),
                            FeedProfileEditMenuItem(
                                id = index * 2 + 1,
                                level = 3,
                                text = "Skimmer",
                                toggleButtonType = FeedProfileEditToggleButtonType.Skimmer,
                                isToggleButton = true,
                                isNumericInput = false,
                                phaseType = FeedProfileEditPhaseType.FEED,
                                isChecked = mutableStateOf(
                                    myFeedProfile.feedProfilePhases[index * 2 + 1].skimmer
                                ),
                            ),
                            FeedProfileEditMenuItem(
                                id = index * 2 + 1,
                                level = 3,
                                text = "Main Pump 1",
                                toggleButtonType = FeedProfileEditToggleButtonType.MainPump1,
                                isToggleButton = true,
                                isNumericInput = false,
                                phaseType = FeedProfileEditPhaseType.FEED,
                                isChecked = mutableStateOf(
                                    myFeedProfile.feedProfilePhases[index * 2 + 1].mainPump1
                                ),
                            ),
                            FeedProfileEditMenuItem(
                                id = index * 2 + 1,
                                level = 3,
                                text = "Main Pump 2",
                                toggleButtonType = FeedProfileEditToggleButtonType.MainPump2,
                                isToggleButton = true,
                                isNumericInput = false,
                                phaseType = FeedProfileEditPhaseType.FEED,
                                isChecked = mutableStateOf(
                                    myFeedProfile.feedProfilePhases[index * 2 + 1].mainPump2
                                ),
                            ),
                            FeedProfileEditMenuItem(
                                id = index * 2 + 1,
                                level = 3,
                                text = "Aux. Pump 1",
                                toggleButtonType = FeedProfileEditToggleButtonType.AuxPump1,
                                isToggleButton = true,
                                isNumericInput = false,
                                phaseType = FeedProfileEditPhaseType.FEED,
                                isChecked = mutableStateOf(
                                    myFeedProfile.feedProfilePhases[index * 2 + 1].auxPump1
                                ),
                            ),
                            FeedProfileEditMenuItem(
                                id = index * 2 + 1,
                                level = 3,
                                text = "Aux. Pump 2",
                                toggleButtonType = FeedProfileEditToggleButtonType.AuxPump2,
                                isToggleButton = true,
                                isNumericInput = false,
                                phaseType = FeedProfileEditPhaseType.FEED,
                                isChecked = mutableStateOf(
                                    myFeedProfile.feedProfilePhases[index * 2 + 1].auxPump2
                                ),
                            ),
                        ),
                    ),
                ),
            )
        }
    }

    fun updatePhaseDuration(phaseIndex: Int, newDuration: Double) {
        Log.d("FeedProfileEditViewModel", "Updating phase($phaseIndex) duration to $newDuration")
        myFeedProfile.feedProfilePhases[phaseIndex].duration = newDuration
        _totalDuration.value = myFeedProfile.calcDuration()
        transformFeedProfileToChartData()
    }

    fun updateSkimmer(phaseIndex: Int, newValue: Boolean) {
        myFeedProfile.feedProfilePhases[phaseIndex].skimmer = newValue
        transformFeedProfileToChartData()
    }

    fun updateMainPump1(phaseIndex: Int, newValue: Boolean) {
        myFeedProfile.feedProfilePhases[phaseIndex].mainPump1 = newValue
        transformFeedProfileToChartData()
    }

    fun updateMainPump2(phaseIndex: Int, newValue: Boolean) {
        myFeedProfile.feedProfilePhases[phaseIndex].mainPump2 = newValue
        transformFeedProfileToChartData()
    }

    fun updateAuxPump1(phaseIndex: Int, newValue: Boolean) {
        myFeedProfile.feedProfilePhases[phaseIndex].auxPump1 = newValue
        transformFeedProfileToChartData()
    }

    fun updateAuxPump2(phaseIndex: Int, newValue: Boolean) {
        myFeedProfile.feedProfilePhases[phaseIndex].auxPump2 = newValue
        transformFeedProfileToChartData()
    }

    private fun resetFeedProfileEditMenuItems() {
        for ((index, value) in phaseMenuItems.withIndex()) {
            for (value2 in value.children) {
                when (value2.phaseType) {
                    FeedProfileEditPhaseType.PAUSE -> {
                        for (value3 in value2.children) {
                            when (value3.toggleButtonType) {
                                FeedProfileEditToggleButtonType.Skimmer -> {
                                    Log.d(
                                        "FeedProfileEditViewModel",
                                        "setting value3.updateIsChecked(${myFeedProfile.feedProfilePhases[index * 2].skimmer})"
                                    )
                                    value3.updateIsChecked(
                                        myFeedProfile.feedProfilePhases[index * 2].skimmer
                                    )
                                }

                                FeedProfileEditToggleButtonType.MainPump1 -> {
                                    value3.updateIsChecked(
                                        myFeedProfile.feedProfilePhases[index * 2].mainPump1
                                    )
                                }

                                FeedProfileEditToggleButtonType.MainPump2 -> {
                                    value3.updateIsChecked(
                                        myFeedProfile.feedProfilePhases[index * 2].mainPump2
                                    )
                                }

                                FeedProfileEditToggleButtonType.AuxPump1 -> {
                                    value3.updateIsChecked(
                                        myFeedProfile.feedProfilePhases[index * 2].auxPump1
                                    )
                                }

                                FeedProfileEditToggleButtonType.AuxPump2 -> {
                                    value3.updateIsChecked(
                                        myFeedProfile.feedProfilePhases[index * 2].auxPump2
                                    )
                                }

                                null -> {
                                    value3.updateInputValue(
                                        myFeedProfile.feedProfilePhases[index * 2].duration
                                    )
                                    Log.d(
                                        "FeedProfileEditViewModel",
                                        "setting myFeedProfile.value.feedProfilePhases[${index * 2}].duration = ${myFeedProfile.feedProfilePhases[index * 2].duration}"
                                    )
                                }
                            }
                        }
                    }

                    FeedProfileEditPhaseType.FEED -> {
                        for (value3 in value2.children) {
                            when (value3.toggleButtonType) {
                                FeedProfileEditToggleButtonType.Skimmer -> {
                                    value3.updateIsChecked(
                                        myFeedProfile.feedProfilePhases[index * 2 + 1].skimmer
                                    )
                                }

                                FeedProfileEditToggleButtonType.MainPump1 -> {
                                    value3.updateIsChecked(
                                        myFeedProfile.feedProfilePhases[index * 2 + 1].mainPump1
                                    )
                                }

                                FeedProfileEditToggleButtonType.MainPump2 -> {
                                    value3.updateIsChecked(
                                        myFeedProfile.feedProfilePhases[index * 2 + 1].mainPump2
                                    )
                                }

                                FeedProfileEditToggleButtonType.AuxPump1 -> {
                                    value3.updateIsChecked(
                                        myFeedProfile.feedProfilePhases[index * 2 + 1].auxPump1
                                    )
                                }

                                FeedProfileEditToggleButtonType.AuxPump2 -> {
                                    value3.updateIsChecked(
                                        myFeedProfile.feedProfilePhases[index * 2 + 1].auxPump2
                                    )
                                }

                                null -> {
                                    value3.updateInputValue(
                                        myFeedProfile.feedProfilePhases[index * 2 + 1].duration
                                    )
                                    Log.d(
                                        "FeedProfileEditViewModel",
                                        "setting myFeedProfile.value.feedProfilePhases[${index * 2 + 1}].duration = ${myFeedProfile.feedProfilePhases[index * 2 + 1].duration}"
                                    )
                                }
                            }
                        }
                    }

                    null -> {
                        // do nothing
                    }
                }
            }
        }
    }

    fun resetFeedProfile() {
        myFeedProfile.reset()           // copy content from backup data structure
        resetFeedProfileEditMenuItems() // copy from myFeedProfile to phaseMenuItems
    }

    fun updateFeedProfile(mainViewModel: MainViewModel) {
        viewModelScope.launch {
            webApiPostRequestExecution(
                mainViewModel = mainViewModel,
                apiUrl = WebApiUrls.FEED_POST_PROFILE_UPDATE,
                webApiPostRequest = WebApiPostRequest(),
                optionalGetParams = getApiCallParameters()
            )
            myFeedProfile.update()          // overwrite backup data structure
        }
    }

    fun getApiCallParameters(): Map<String, String> {
        return myFeedProfile.getFeedProfileApiCallParameters()
    }

    fun getPhaseDuration(id: Int): Double {
        return myFeedProfile.feedProfilePhases[id].duration
    }
}