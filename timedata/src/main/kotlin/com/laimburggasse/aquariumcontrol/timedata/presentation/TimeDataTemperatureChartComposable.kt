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
fun TimeDataTemperatureChartComposable(
    temperatureDataSet: LineData,
    textColor: Int
) {
    // Main temperature chart
    AndroidView(
        modifier = Modifier
            .fillMaxWidth()
            .height(height = chartHeight.dp), factory = { context ->
            LineChart(context).apply {
                data = temperatureDataSet
                description.isEnabled = false
                legend.isEnabled = true
                legend.textColor = textColor
                axisRight.isEnabled = true
                axisRight.valueFormatter =
                    TimeDataYAxisOnOffValueFormatter()
                axisRight.granularity = axisGranularityOnOff
                axisRight.textColor = textColor
                axisLeft.setDrawTopYLabelEntry(true)
                axisLeft.valueFormatter =
                    TimeDataYAxisDegreeCelsiusValueFormatter()
                axisLeft.textColor = textColor
                xAxis.granularity = axisGranularityTime
                xAxis.valueFormatter = TimeDataXAxisValueFormatter()
                xAxis.position = XAxis.XAxisPosition.BOTTOM
                xAxis.textColor = textColor
            }
        }, update = { lineChart ->
            lineChart.data = temperatureDataSet
            lineChart.notifyDataSetChanged()
            lineChart.invalidate()
        })
}

@Preview(showBackground = true, heightDp = 450, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(showBackground = true, heightDp = 450, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
internal fun TimeDataTemperatureChartComposablePreview() {
    val textColor: Int = if (isSystemInDarkTheme()) Color.WHITE else Color.BLACK
    AquariumControllerTheme {
        Surface {
            TimeDataTemperatureChartComposable(
                temperatureDataSet = mockTimeDataSets.dataTemperature,
                textColor = textColor
            )
        }
    }
}