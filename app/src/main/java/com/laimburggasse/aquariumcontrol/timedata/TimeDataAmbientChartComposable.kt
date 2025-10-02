package com.laimburggasse.aquariumcontrol.timedata

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
import com.laimburggasse.aquariumcontrol.ui.theme.AquariumControllerTheme

@Composable
fun TimeDataAmbientChartComposable(
    ambientDataSet: LineData,
    textColor: Int
) {
    // Ambient chart
    AndroidView(
        modifier = Modifier
            .fillMaxWidth()
            .height(height = chartHeight.dp), factory = { context ->
            LineChart(context).apply {
                data = ambientDataSet
                description.isEnabled = false
                legend.isEnabled = true
                legend.textColor = textColor
                axisRight.isEnabled = true
                axisRight.textColor = textColor
                axisRight.valueFormatter =
                    TimeDataYAxisPercentValueFormatter()
                axisLeft.textColor = textColor
                axisLeft.setDrawTopYLabelEntry(true)
                axisLeft.valueFormatter =
                    TimeDataYAxisDegreeCelsiusValueFormatter()
                xAxis.granularity = axisGranularityTime
                xAxis.valueFormatter = TimeDataXAxisValueFormatter()
                xAxis.position = XAxis.XAxisPosition.BOTTOM
                xAxis.textColor = textColor
            }
        }, update = { lineChart ->
            lineChart.data = ambientDataSet
            lineChart.notifyDataSetChanged()
            lineChart.invalidate()
        })
}

@Preview(showBackground = true, heightDp = 450, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(showBackground = true, heightDp = 450, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
internal fun TimeDataAmbientChartComposablePreview() {
    val textColor = if (isSystemInDarkTheme()) Color.WHITE else Color.BLACK
    val mockDataSets = TimeDataSets(timeData =generateMockTimeData())
    AquariumControllerTheme {
        Surface {
            TimeDataAmbientChartComposable(
                ambientDataSet = mockDataSets.dataAmbient,
                textColor = textColor
            )
        }
    }
}