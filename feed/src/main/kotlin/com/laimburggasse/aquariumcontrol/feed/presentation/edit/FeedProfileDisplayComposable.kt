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
import com.laimburggasse.aquariumcontrol.common.presentation.theme.AquariumControllerTheme
import com.laimburggasse.aquariumcontrol.feed.preview.FeedPreviewDataImpl.mockChartData

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
    AquariumControllerTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            Column {
                FeedProfileDisplayComposable(
                    lineDataFeeder = mockChartData.lineDataFeeder,
                    lineDataSkimmer = mockChartData.lineDataSkimmer,
                    lineDataMainPump1 = mockChartData.lineDataMainPump1,
                    lineDataMainPump2 = mockChartData.lineDataMainPump2,
                    lineDataAuxPump1 = mockChartData.lineDataAuxPump1,
                    lineDataAuxPump2 = mockChartData.lineDataAuxPump2,
                    totalDuration = mockChartData.totalDuration
                )
            }
        }
    }
}