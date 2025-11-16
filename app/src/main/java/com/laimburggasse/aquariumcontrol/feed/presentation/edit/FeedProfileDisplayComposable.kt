package com.laimburggasse.aquariumcontrol.feed.presentation.edit

import android.content.res.Configuration
import android.graphics.Color
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.LineData
import com.laimburggasse.aquariumcontrol.feed.domain.model.FeedProfile
import com.laimburggasse.aquariumcontrol.common.presentation.theme.AquariumControllerTheme

@Composable
fun FeedProfileDisplayComposable(
    lineDataFeeder: LineData?,
    lineDataSkimmer: LineData?,
    lineDataMainPump1: LineData?,
    lineDataMainPump2: LineData?,
    lineDataAuxPump1: LineData?,
    lineDataAuxPump2: LineData?,
    totalDuration: Float
) {
    // This composable displays a set of charts based on the provided feed profile data.

    val isDarkMode: Boolean = isSystemInDarkTheme()

    AndroidView(modifier = Modifier
        .fillMaxWidth()
        .height(height = 50.dp), factory = { context ->
        LineChart(context).apply {
            data = lineDataFeeder
            description.isEnabled = false
            legend.isEnabled = false
            axisRight.isEnabled = false
            axisLeft.granularity = FeedProfileDisplayYAxisValueDefinition.GRANULARITY
            axisLeft.axisMinimum = FeedProfileDisplayYAxisValueDefinition.MIN_VALUE
            axisLeft.axisMaximum = FeedProfileDisplayYAxisValueDefinition.MAX_VALUE
            axisLeft.setDrawTopYLabelEntry(true)
            setScaleEnabled(false)
            xAxis.isEnabled = false
            val valueFormatter = FeedProfileAxisValueFormatter(totalDuration)
            axisLeft.valueFormatter = valueFormatter
            val axisLabelColor: Int = if (isDarkMode) Color.WHITE else Color.BLACK
            axisLeft.textColor = axisLabelColor
        }
    }, update = { lineChart ->
        // Update the chart data or settings as needed
        lineChart.data = lineDataFeeder
        lineChart.notifyDataSetChanged()
        lineChart.invalidate()
    })

    AndroidView(modifier = Modifier
        .fillMaxWidth()
        .height(height = 50.dp), factory = { context ->
        LineChart(context).apply {
            data = lineDataSkimmer
            description.isEnabled = false
            legend.isEnabled = false
            axisRight.isEnabled = false
            axisLeft.granularity = FeedProfileDisplayYAxisValueDefinition.GRANULARITY
            axisLeft.axisMinimum = FeedProfileDisplayYAxisValueDefinition.MIN_VALUE
            axisLeft.axisMaximum = FeedProfileDisplayYAxisValueDefinition.MAX_VALUE
            axisLeft.setDrawTopYLabelEntry(true)
            setScaleEnabled(false)
            xAxis.isEnabled = false
            val valueFormatter = FeedProfileAxisValueFormatter(totalDuration)
            axisLeft.valueFormatter = valueFormatter
            // Set axis label colors based on dark mode
            val axisLabelColor: Int = if (isDarkMode) Color.WHITE else Color.BLACK
            axisLeft.textColor = axisLabelColor
        }
    }, update = { lineChart ->
        lineChart.data = lineDataSkimmer
        lineChart.notifyDataSetChanged()
        lineChart.invalidate()
    })

    AndroidView(modifier = Modifier
        .fillMaxWidth()
        .height(height = 50.dp), factory = { context ->
        LineChart(context).apply {
            data = lineDataMainPump1
            description.isEnabled = false
            legend.isEnabled = false
            axisRight.isEnabled = false
            axisLeft.granularity = FeedProfileDisplayYAxisValueDefinition.GRANULARITY
            axisLeft.axisMinimum = FeedProfileDisplayYAxisValueDefinition.MIN_VALUE
            axisLeft.axisMaximum = FeedProfileDisplayYAxisValueDefinition.MAX_VALUE
            axisLeft.setDrawTopYLabelEntry(true)
            setScaleEnabled(false)
            xAxis.isEnabled = false
            val valueFormatter = FeedProfileAxisValueFormatter(totalDuration)
            axisLeft.valueFormatter = valueFormatter
            val axisLabelColor: Int = if (isDarkMode) Color.WHITE else Color.BLACK
            axisLeft.textColor = axisLabelColor
        }
    }, update = { lineChart ->
        lineChart.data = lineDataMainPump1
        lineChart.notifyDataSetChanged()
        lineChart.invalidate()
    })

    AndroidView(modifier = Modifier
        .fillMaxWidth()
        .height(height = 50.dp), factory = { context ->
        LineChart(context).apply {
            data = lineDataMainPump2
            description.isEnabled = false
            legend.isEnabled = false
            axisRight.isEnabled = false
            axisLeft.granularity = FeedProfileDisplayYAxisValueDefinition.GRANULARITY
            axisLeft.axisMinimum = FeedProfileDisplayYAxisValueDefinition.MIN_VALUE
            axisLeft.axisMaximum = FeedProfileDisplayYAxisValueDefinition.MAX_VALUE
            axisLeft.setDrawTopYLabelEntry(true)
            setScaleEnabled(false)
            xAxis.isEnabled = false
            val valueFormatter = FeedProfileAxisValueFormatter(totalDuration)
            axisLeft.valueFormatter = valueFormatter
            val axisLabelColor: Int = if (isDarkMode) Color.WHITE else Color.BLACK
            axisLeft.textColor = axisLabelColor
        }
    }, update = { lineChart ->
        lineChart.data = lineDataMainPump2
        lineChart.notifyDataSetChanged()
        lineChart.invalidate()
    })

    AndroidView(modifier = Modifier
        .fillMaxWidth()
        .height(height = 50.dp), factory = { context ->
        LineChart(context).apply {
            data = lineDataAuxPump1
            description.isEnabled = false
            legend.isEnabled = false
            axisRight.isEnabled = false
            axisLeft.granularity = FeedProfileDisplayYAxisValueDefinition.GRANULARITY
            axisLeft.axisMinimum = FeedProfileDisplayYAxisValueDefinition.MIN_VALUE
            axisLeft.axisMaximum = FeedProfileDisplayYAxisValueDefinition.MAX_VALUE
            axisLeft.setDrawTopYLabelEntry(true)
            setScaleEnabled(false)
            xAxis.isEnabled = false
            val valueFormatter = FeedProfileAxisValueFormatter(totalDuration)
            axisLeft.valueFormatter = valueFormatter
            val axisLabelColor = if (isDarkMode) Color.WHITE else Color.BLACK
            axisLeft.textColor = axisLabelColor
        }
    }, update = { lineChart ->
        lineChart.data = lineDataAuxPump1
        lineChart.notifyDataSetChanged()
        lineChart.invalidate()
    })

    AndroidView(modifier = Modifier
        .fillMaxWidth()
        .height(height = 50.dp), factory = { context ->
        LineChart(context).apply {
            data = lineDataAuxPump2
            description.isEnabled = false
            legend.isEnabled = false
            axisRight.isEnabled = false
            axisLeft.granularity = FeedProfileDisplayYAxisValueDefinition.GRANULARITY
            axisLeft.axisMinimum = FeedProfileDisplayYAxisValueDefinition.MIN_VALUE
            axisLeft.axisMaximum = FeedProfileDisplayYAxisValueDefinition.MAX_VALUE
            axisLeft.setDrawTopYLabelEntry(true)
            setScaleEnabled(false)
            xAxis.setDrawGridLines(false)
            val valueFormatter = FeedProfileAxisValueFormatter(totalDuration)
            axisLeft.valueFormatter = valueFormatter
            xAxis.valueFormatter = valueFormatter
            xAxis.position = XAxis.XAxisPosition.BOTTOM
            val axisLabelColor = if (isDarkMode) Color.WHITE else Color.BLACK
            xAxis.textColor = axisLabelColor
            axisLeft.textColor = axisLabelColor
        }
    }, update = { lineChart ->
        lineChart.data = lineDataAuxPump2
        lineChart.notifyDataSetChanged()
        lineChart.invalidate()
    })
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
internal fun FeedProfileDisplayComposablePreview() {
    val mockFeedProfile: FeedProfile = FeedProfile().apply {
        feedProfilePhases[0].duration = 5.0
        feedProfilePhases[1].duration = 10.0
        feedProfilePhases[1].skimmer = true
    }

    val chartData: FeedProfileChartDataBundle = FeedProfileChartDataFactory.create(mockFeedProfile)

    AquariumControllerTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            Column {
                FeedProfileDisplayComposable(
                    lineDataFeeder = chartData.lineDataFeeder,
                    lineDataSkimmer = chartData.lineDataSkimmer,
                    lineDataMainPump1 = chartData.lineDataMainPump1,
                    lineDataMainPump2 = chartData.lineDataMainPump2,
                    lineDataAuxPump1 = chartData.lineDataAuxPump1,
                    lineDataAuxPump2 = chartData.lineDataAuxPump2,
                    totalDuration = chartData.totalDuration
                )
            }
        }
    }
}