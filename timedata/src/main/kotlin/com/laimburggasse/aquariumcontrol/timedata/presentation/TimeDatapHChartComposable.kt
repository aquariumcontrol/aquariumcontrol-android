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
    val textColor = if (isSystemInDarkTheme()) Color.WHITE else Color.BLACK
    val mockDataSets = TimeDataSets(timeData = generateMockTimeData())
    AquariumControllerTheme {
        Surface {
            TimeDatapHChartComposable(
                pHDataSet = mockDataSets.datapH,
                textColor = textColor
            )
        }
    }
}
