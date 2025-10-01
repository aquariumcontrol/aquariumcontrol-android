package com.laimburggasse.aquariumcontrol.feed.edit

import android.content.res.Configuration
import android.graphics.Color
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.laimburggasse.aquariumcontrol.feed.FeedProfile
import com.laimburggasse.aquariumcontrol.feed.FeedProfileImport
import com.laimburggasse.aquariumcontrol.ui.theme.AquariumControllerTheme
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis

const val chartValAllowedDelta = 0.001f

@Composable
fun FeedProfileDisplayComposable(feedProfileEditViewModel: FeedProfileEditViewModel) {
    // This composable displays a set of charts based on the provided feed profile data.
    
    val isDarkMode = isSystemInDarkTheme()

    val chartDataFeeder by feedProfileEditViewModel.lineDataFeedProfileFeeder.observeAsState()
    val chartDataSkimmer by feedProfileEditViewModel.lineDataFeedProfileSkimmer.observeAsState()
    val chartDataMainPump1 by feedProfileEditViewModel.lineDataFeedProfileMainPump1.observeAsState()
    val chartDataMainPump2 by feedProfileEditViewModel.lineDataFeedProfileMainPump2.observeAsState()
    val chartDataAuxPump1 by feedProfileEditViewModel.lineDataFeedProfileAuxPump1.observeAsState()
    val chartDataAuxPump2 by feedProfileEditViewModel.lineDataFeedProfileAuxPump2.observeAsState()
    val totalDuration by feedProfileEditViewModel.totalDuration.observeAsState()

    AndroidView(modifier = Modifier
        .fillMaxWidth()
        .height(50.dp), factory = { context ->
        LineChart(context).apply {
            data = chartDataFeeder
            description.isEnabled = false
            legend.isEnabled = false
            axisRight.isEnabled = false
            axisLeft.granularity = FeedProfileDisplayYAxisValueDefinition.GRANULARITY
            axisLeft.axisMinimum = FeedProfileDisplayYAxisValueDefinition.MIN_VALUE
            axisLeft.axisMaximum = FeedProfileDisplayYAxisValueDefinition.MAX_VALUE
            axisLeft.setDrawTopYLabelEntry(true)
            setScaleEnabled(false)
            xAxis.isEnabled = false
            val valueFormatter =
                totalDuration?.let { FeedProfileAxisValueFormatter(it) }
            axisLeft.valueFormatter = valueFormatter
            val axisLabelColor = if (isDarkMode) Color.WHITE else Color.BLACK
            axisLeft.textColor = axisLabelColor
        }
    }, update = { lineChart ->
        // Update the chart data or settings as needed
        lineChart.data = chartDataFeeder
        lineChart.notifyDataSetChanged()
        lineChart.invalidate()
    })

    AndroidView(modifier = Modifier
        .fillMaxWidth()
        .height(50.dp), factory = { context ->
        LineChart(context).apply {
            data = chartDataSkimmer
            description.isEnabled = false
            legend.isEnabled = false
            axisRight.isEnabled = false
            axisLeft.granularity = FeedProfileDisplayYAxisValueDefinition.GRANULARITY
            axisLeft.axisMinimum = FeedProfileDisplayYAxisValueDefinition.MIN_VALUE
            axisLeft.axisMaximum = FeedProfileDisplayYAxisValueDefinition.MAX_VALUE
            axisLeft.setDrawTopYLabelEntry(true)
            setScaleEnabled(false)
            xAxis.isEnabled = false
            val valueFormatter =
                totalDuration?.let { FeedProfileAxisValueFormatter(it) }
            axisLeft.valueFormatter = valueFormatter
            // Set axis label colors based on dark mode
            val axisLabelColor = if (isDarkMode) Color.WHITE else Color.BLACK
            axisLeft.textColor = axisLabelColor
        }
    }, update = { lineChart ->
        lineChart.data = chartDataSkimmer
        lineChart.notifyDataSetChanged()
        lineChart.invalidate()
    })

    AndroidView(modifier = Modifier
        .fillMaxWidth()
        .height(50.dp), factory = { context ->
        LineChart(context).apply {
            data = chartDataMainPump1
            description.isEnabled = false
            legend.isEnabled = false
            axisRight.isEnabled = false
            axisLeft.granularity = FeedProfileDisplayYAxisValueDefinition.GRANULARITY
            axisLeft.axisMinimum = FeedProfileDisplayYAxisValueDefinition.MIN_VALUE
            axisLeft.axisMaximum = FeedProfileDisplayYAxisValueDefinition.MAX_VALUE
            axisLeft.setDrawTopYLabelEntry(true)
            setScaleEnabled(false)
            xAxis.isEnabled = false
            val valueFormatter =
                totalDuration?.let { FeedProfileAxisValueFormatter(it) }
            axisLeft.valueFormatter = valueFormatter
            val axisLabelColor = if (isDarkMode) Color.WHITE else Color.BLACK
            axisLeft.textColor = axisLabelColor
        }
    }, update = { lineChart ->
        lineChart.data = chartDataMainPump1
        lineChart.notifyDataSetChanged()
        lineChart.invalidate()
    })

    AndroidView(modifier = Modifier
        .fillMaxWidth()
        .height(50.dp), factory = { context ->
        LineChart(context).apply {
            data = chartDataMainPump2
            description.isEnabled = false
            legend.isEnabled = false
            axisRight.isEnabled = false
            axisLeft.granularity = FeedProfileDisplayYAxisValueDefinition.GRANULARITY
            axisLeft.axisMinimum = FeedProfileDisplayYAxisValueDefinition.MIN_VALUE
            axisLeft.axisMaximum = FeedProfileDisplayYAxisValueDefinition.MAX_VALUE
            axisLeft.setDrawTopYLabelEntry(true)
            setScaleEnabled(false)
            xAxis.isEnabled = false
            val valueFormatter =
                totalDuration?.let { FeedProfileAxisValueFormatter(it) }
            axisLeft.valueFormatter = valueFormatter
            val axisLabelColor = if (isDarkMode) Color.WHITE else Color.BLACK
            axisLeft.textColor = axisLabelColor
        }
    }, update = { lineChart ->
        lineChart.data = chartDataMainPump2
        lineChart.notifyDataSetChanged()
        lineChart.invalidate()
    })

    AndroidView(modifier = Modifier
        .fillMaxWidth()
        .height(50.dp), factory = { context ->
        LineChart(context).apply {
            data = chartDataAuxPump1
            description.isEnabled = false
            legend.isEnabled = false
            axisRight.isEnabled = false
            axisLeft.granularity = FeedProfileDisplayYAxisValueDefinition.GRANULARITY
            axisLeft.axisMinimum = FeedProfileDisplayYAxisValueDefinition.MIN_VALUE
            axisLeft.axisMaximum = FeedProfileDisplayYAxisValueDefinition.MAX_VALUE
            axisLeft.setDrawTopYLabelEntry(true)
            setScaleEnabled(false)
            xAxis.isEnabled = false
            val valueFormatter =
                totalDuration?.let { FeedProfileAxisValueFormatter(it) }
            axisLeft.valueFormatter = valueFormatter
            val axisLabelColor = if (isDarkMode) Color.WHITE else Color.BLACK
            axisLeft.textColor = axisLabelColor
        }
    }, update = { lineChart ->
        lineChart.data = chartDataAuxPump1
        lineChart.notifyDataSetChanged()
        lineChart.invalidate()
    })

    AndroidView(modifier = Modifier
        .fillMaxWidth()
        .height(50.dp), factory = { context ->
        LineChart(context).apply {
            data = chartDataAuxPump2
            description.isEnabled = false
            legend.isEnabled = false
            axisRight.isEnabled = false
            axisLeft.granularity = FeedProfileDisplayYAxisValueDefinition.GRANULARITY
            axisLeft.axisMinimum = FeedProfileDisplayYAxisValueDefinition.MIN_VALUE
            axisLeft.axisMaximum = FeedProfileDisplayYAxisValueDefinition.MAX_VALUE
            axisLeft.setDrawTopYLabelEntry(true)
            setScaleEnabled(false)
            xAxis.setDrawGridLines(false)
            val valueFormatter =
                totalDuration?.let { FeedProfileAxisValueFormatter(it) }
            axisLeft.valueFormatter = valueFormatter
            xAxis.valueFormatter = valueFormatter
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM)
            val axisLabelColor = if (isDarkMode) Color.WHITE else Color.BLACK
            xAxis.textColor = axisLabelColor
            axisLeft.textColor = axisLabelColor
        }
    }, update = { lineChart ->
        lineChart.data = chartDataAuxPump2
        lineChart.notifyDataSetChanged()
        lineChart.invalidate()
    })
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
internal fun FeedProfileDisplayComposablePreview() {
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

    val feedProfileEditViewModel = FeedProfileEditViewModel(mockFeedProfile)

    AquariumControllerTheme {
        Surface(
            color = MaterialTheme.colorScheme.background
        ) {
            Column {
                FeedProfileDisplayComposable(
                    feedProfileEditViewModel = feedProfileEditViewModel
                )
            }
        }
    }
}