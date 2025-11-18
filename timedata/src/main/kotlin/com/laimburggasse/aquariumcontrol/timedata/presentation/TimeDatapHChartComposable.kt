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

package com.laimburggasse.aquariumcontrol.timedata.presentation

import android.content.res.Configuration
import android.graphics.Color
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import com.laimburggasse.aquariumcontrol.timedata.testing.TimedataMockData.mockTimeDataSets

@Composable
fun TimeDatapHChartComposable(
    pHDataSet: LineData,
    textColor: Int
) {
    // pH Chart
    AndroidView(
        modifier = Modifier
            .fillMaxWidth()
            .height(height = chartHeight.dp), factory = { context ->
            LineChart(context).apply {
                data = pHDataSet
                description.isEnabled = false
                legend.isEnabled = true
                legend.textColor = textColor
                axisRight.isEnabled = false
                axisLeft.setDrawTopYLabelEntry(true)
                xAxis.granularity = axisGranularityTime
                xAxis.valueFormatter = TimeDataXAxisValueFormatter()
                xAxis.position = XAxis.XAxisPosition.BOTTOM
                xAxis.textColor = textColor
                axisLeft.textColor = textColor
            }
        }, update = { lineChart ->
            lineChart.data = pHDataSet
            lineChart.notifyDataSetChanged()
            lineChart.invalidate()
        })
}

@Preview(showBackground = true, heightDp = 450, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(showBackground = true, heightDp = 450, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
internal fun TimeDatapHChartComposablePreview() {
    val textColor: Int = if (isSystemInDarkTheme()) Color.WHITE else Color.BLACK
    AquariumControllerTheme {
        Surface {
            TimeDatapHChartComposable(
                pHDataSet = mockTimeDataSets.datapH,
                textColor = textColor
            )
        }
    }
}
