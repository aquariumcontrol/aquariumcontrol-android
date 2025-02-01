package com.example.aquariumcontroller.feed.edit

import android.graphics.Color
import android.util.Log
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.aquariumcontroller.feed.FeedProfile
import com.example.aquariumcontroller.feed.FeedProfileImport
import com.example.aquariumcontroller.feed.FeedProfilePhase
import com.example.aquariumcontroller.ui.theme.AquariumControllerTheme
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import kotlin.math.abs

const val chartValAllowedDelta = 0.001f

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

fun transformFeedProfileToChartData(feedProfile: FeedProfile): LineData {
    val valuesFeeder = ArrayList<Entry>()
    val valuesSkimmer = ArrayList<Entry>()
    val valuesMainPump1 = ArrayList<Entry>()
    val valuesMainPump2 = ArrayList<Entry>()
    val valuesAuxPump1 = ArrayList<Entry>()
    val valuesAuxPump2 = ArrayList<Entry>()

    val itFeedProfilePhases: Iterator<FeedProfilePhase> = feedProfile.feedProfilePhases.iterator()

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
        }
        else {
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
    val lineDataFeedProfile = LineData(setFeeder)
    lineDataFeedProfile.addDataSet(setSkimmer)
    lineDataFeedProfile.addDataSet(setMainPump1)
    lineDataFeedProfile.addDataSet(setMainPump2)
    lineDataFeedProfile.addDataSet(setAuxPump1)
    lineDataFeedProfile.addDataSet(setAuxPump2)

    return lineDataFeedProfile
}

@Composable
fun FeedProfileDisplayComposable(feedProfile: FeedProfile) {
    val isDarkMode = isSystemInDarkTheme()
    val chartData = remember(feedProfile.stateChangeIndicator) {
        transformFeedProfileToChartData(feedProfile)
    }
    val valueFormatter = FeedProfileAxisValueFormatter(feedProfile)

    AndroidView(modifier = Modifier
        .fillMaxWidth()
        .height(350.dp), factory = { context ->
        LineChart(context).apply {
            // Customize the chart appearance (axis labels, grid lines, etc.)
            data = chartData

            description.isEnabled = false
            legend.isEnabled = false
            axisRight.isEnabled = false
            axisLeft.granularity = 1f
            axisLeft.valueFormatter = valueFormatter
            xAxis.valueFormatter = valueFormatter
            // Set axis label colors based on dark mode
            val axisLabelColor = if (isDarkMode) Color.WHITE else Color.BLACK
            xAxis.textColor = axisLabelColor
            axisLeft.textColor = axisLabelColor
        }
    }, update = { lineChart ->
        // Update the chart data or settings as needed
        lineChart.data = chartData
        lineChart.notifyDataSetChanged()
        lineChart.invalidate()
    })
}

@Preview(showBackground = true)
@Composable
fun FeedProfileDisplayComposablePreview() {
    val mockFeedProfileImport = FeedProfileImport()
    mockFeedProfileImport.profileId = 1
    mockFeedProfileImport.profileName = "Test Profile"
    mockFeedProfileImport.pause01duration = 1000
    mockFeedProfileImport.pause02duration = 1000
    mockFeedProfileImport.pause03duration = 1000
    mockFeedProfileImport.pause04duration = 1000
    mockFeedProfileImport.pause05duration = 1000
    mockFeedProfileImport.pause06duration = 1000
    mockFeedProfileImport.pause07duration = 1000
    mockFeedProfileImport.pause08duration = 1000
    mockFeedProfileImport.pause09duration = 1000
    mockFeedProfileImport.pause10duration = 1000
    mockFeedProfileImport.feed01duration = 1000
    mockFeedProfileImport.feed02duration = 1000
    mockFeedProfileImport.feed03duration = 1000
    mockFeedProfileImport.feed04duration = 1000
    mockFeedProfileImport.feed05duration = 1000
    mockFeedProfileImport.feed06duration = 1000
    mockFeedProfileImport.feed07duration = 1000
    mockFeedProfileImport.feed08duration = 1000
    mockFeedProfileImport.feed09duration = 1000
    mockFeedProfileImport.feed10duration = 1000
    mockFeedProfileImport.pause01skimmer = 1
    mockFeedProfileImport.pause02skimmer = 1
    mockFeedProfileImport.pause03skimmer = 1
    mockFeedProfileImport.pause04skimmer = 1
    mockFeedProfileImport.pause05skimmer = 1
    mockFeedProfileImport.pause06skimmer = 1
    mockFeedProfileImport.pause07skimmer = 1
    mockFeedProfileImport.pause08skimmer = 1
    mockFeedProfileImport.pause09skimmer = 1
    mockFeedProfileImport.pause10skimmer = 0
    mockFeedProfileImport.feed01skimmer = 1
    mockFeedProfileImport.feed02skimmer = 1
    mockFeedProfileImport.feed03skimmer = 1
    mockFeedProfileImport.feed04skimmer = 1
    mockFeedProfileImport.feed05skimmer = 1
    mockFeedProfileImport.feed06skimmer = 1
    mockFeedProfileImport.feed07skimmer = 1
    mockFeedProfileImport.feed08skimmer = 1
    mockFeedProfileImport.feed09skimmer = 1
    mockFeedProfileImport.feed10skimmer = 0
    mockFeedProfileImport.pause01mainPump1 = 1
    mockFeedProfileImport.pause02mainPump1 = 1
    mockFeedProfileImport.pause03mainPump1 = 1
    mockFeedProfileImport.pause04mainPump1 = 1
    mockFeedProfileImport.pause05mainPump1 = 1
    mockFeedProfileImport.pause06mainPump1 = 1
    mockFeedProfileImport.pause07mainPump1 = 1
    mockFeedProfileImport.pause08mainPump1 = 1
    mockFeedProfileImport.pause09mainPump1 = 0
    mockFeedProfileImport.pause10mainPump1 = 0
    mockFeedProfileImport.feed01mainPump1 = 1
    mockFeedProfileImport.feed02mainPump1 = 1
    mockFeedProfileImport.feed03mainPump1 = 1
    mockFeedProfileImport.feed04mainPump1 = 1
    mockFeedProfileImport.feed05mainPump1 = 1
    mockFeedProfileImport.feed06mainPump1 = 1
    mockFeedProfileImport.feed07mainPump1 = 1
    mockFeedProfileImport.feed08mainPump1 = 1
    mockFeedProfileImport.feed09mainPump1 = 0
    mockFeedProfileImport.feed10mainPump1 = 0
    mockFeedProfileImport.pause01mainPump2 = 1
    mockFeedProfileImport.pause02mainPump2 = 1
    mockFeedProfileImport.pause03mainPump2 = 1
    mockFeedProfileImport.pause04mainPump2 = 1
    mockFeedProfileImport.pause05mainPump2 = 1
    mockFeedProfileImport.pause06mainPump2 = 1
    mockFeedProfileImport.pause07mainPump2 = 1
    mockFeedProfileImport.pause08mainPump2 = 0
    mockFeedProfileImport.pause09mainPump2 = 0
    mockFeedProfileImport.pause10mainPump2 = 0
    mockFeedProfileImport.feed01mainPump2 = 1
    mockFeedProfileImport.feed02mainPump2 = 1
    mockFeedProfileImport.feed03mainPump2 = 1
    mockFeedProfileImport.feed04mainPump2 = 1
    mockFeedProfileImport.feed05mainPump2 = 1
    mockFeedProfileImport.feed06mainPump2 = 1
    mockFeedProfileImport.feed07mainPump2 = 1
    mockFeedProfileImport.feed08mainPump2 = 0
    mockFeedProfileImport.feed09mainPump2 = 0
    mockFeedProfileImport.feed10mainPump2 = 0
    mockFeedProfileImport.pause01auxPump1 = 1
    mockFeedProfileImport.pause02auxPump1 = 1
    mockFeedProfileImport.pause03auxPump1 = 1
    mockFeedProfileImport.pause04auxPump1 = 1
    mockFeedProfileImport.pause05auxPump1 = 1
    mockFeedProfileImport.pause06auxPump1 = 1
    mockFeedProfileImport.pause07auxPump1 = 0
    mockFeedProfileImport.pause08auxPump1 = 0
    mockFeedProfileImport.pause09auxPump1 = 0
    mockFeedProfileImport.pause10auxPump1 = 0
    mockFeedProfileImport.feed01auxPump1 = 1
    mockFeedProfileImport.feed02auxPump1 = 1
    mockFeedProfileImport.feed03auxPump1 = 1
    mockFeedProfileImport.feed04auxPump1 = 1
    mockFeedProfileImport.feed05auxPump1 = 1
    mockFeedProfileImport.feed06auxPump1 = 1
    mockFeedProfileImport.feed07auxPump1 = 0
    mockFeedProfileImport.feed08auxPump1 = 0
    mockFeedProfileImport.feed09auxPump1 = 0
    mockFeedProfileImport.feed10auxPump1 = 0
    mockFeedProfileImport.pause01auxPump2 = 1
    mockFeedProfileImport.pause02auxPump2 = 1
    mockFeedProfileImport.pause03auxPump2 = 1
    mockFeedProfileImport.pause04auxPump2 = 1
    mockFeedProfileImport.pause05auxPump2 = 1
    mockFeedProfileImport.pause06auxPump2 = 1
    mockFeedProfileImport.pause07auxPump2 = 0
    mockFeedProfileImport.pause08auxPump2 = 0
    mockFeedProfileImport.pause09auxPump2 = 0
    mockFeedProfileImport.pause10auxPump2 = 0
    mockFeedProfileImport.feed01auxPump2 = 1
    mockFeedProfileImport.feed02auxPump2 = 1
    mockFeedProfileImport.feed03auxPump2 = 1
    mockFeedProfileImport.feed04auxPump2 = 1
    mockFeedProfileImport.feed05auxPump2 = 1
    mockFeedProfileImport.feed06auxPump2 = 1
    mockFeedProfileImport.feed07auxPump2 = 0
    mockFeedProfileImport.feed08auxPump2 = 0
    mockFeedProfileImport.feed09auxPump2 = 0
    mockFeedProfileImport.feed10auxPump2 = 0

    val mockFeedProfile = FeedProfile(mockFeedProfileImport)

    AquariumControllerTheme {
        Column() {
            FeedProfileDisplayComposable(
                feedProfile = mockFeedProfile,
            )
        }
    }
}