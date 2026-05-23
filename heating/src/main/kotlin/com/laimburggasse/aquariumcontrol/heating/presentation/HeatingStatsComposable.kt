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

package com.laimburggasse.aquariumcontrol.heating.presentation

import android.content.res.Configuration
import android.graphics.Color
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.charts.CombinedChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.CombinedData
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.laimburggasse.aquariumcontrol.common.presentation.RoundRefreshButtonComposable
import com.laimburggasse.aquariumcontrol.common.presentation.theme.AquariumControllerTheme
import com.laimburggasse.aquariumcontrol.heating.R
import com.laimburggasse.aquariumcontrol.heating.domain.model.HeatingStatsEntry
import com.laimburggasse.aquariumcontrol.heating.preview.HeatingPreviewDataImpl.mockHeatingStatsData
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HeatingStatsComposable(
    heatingStats: List<HeatingStatsEntry>, updateData: () -> Unit,
    isDarkMode: Boolean
) {
    // This composable is only displaying time data charts without any user interaction.
    // Therefore, this composable does not use a view model.
    val labelHeatingStatsEnergyWh = stringResource(R.string.heating_stats_energy_wh)
    val labelAmbientTemperatureC = stringResource(R.string.ambient_temperature_c)
    val labelWaterTemperatureC = stringResource(R.string.water_temperature_c)
    val labelNoDataAvailable = stringResource(R.string.no_data_available)

    val textColor: Int = if (isDarkMode) Color.WHITE else Color.BLACK

    Column(horizontalAlignment = CenterHorizontally) {
        AndroidView(
            modifier = Modifier
                .weight(weight = 1f)
                .fillMaxSize()
                .padding(all = 16.dp), // Add some padding around the chart
            factory = { context ->
                CombinedChart(context).apply {
                    // Chart configuration
                    description.isEnabled = false // Disable description
                    legend.isEnabled = false
                    setDrawGridBackground(false) // No grid
                    isDragEnabled = true
                    setScaleEnabled(true)  // Allow zooming
                    setPinchZoom(true)    // Allow zooming via Pinch gesture

                    // X-Axis configuration
                    xAxis.position = XAxis.XAxisPosition.BOTTOM
                    xAxis.granularity = 1f // One day per label
                    xAxis.textColor = textColor
                    xAxis.valueFormatter = object : ValueFormatter() {
                        private val outputFormat = SimpleDateFormat("dd.MM", Locale.US)

                        override fun getFormattedValue(value: Float): String {
                            val timestamp: Long? = heatingStats.getOrNull(index = value.toInt())?.timestamp
                            return try {
                                timestamp?.let { outputFormat.format(Date(it)) } ?: ""
                            } catch (e: Exception) {
                                ""
                            }
                        }
                    }
                    xAxis.setDrawGridLines(false)

                    // Left Y-Axis (for energy)
                    axisLeft.apply {
                        setDrawGridLines(false)
                        axisMinimum = 0f // Start at 0
                    }
                    axisLeft.textColor = textColor

                    // Right Y-Axis (for temperatures)
                    axisRight.apply {
                        setDrawGridLines(false)
                    }
                    axisRight.textColor = textColor
                }
            },
            update = { chart ->
                if (heatingStats.isNotEmpty()) {
                    // Create data entries
                    val energyEntries: MutableList<BarEntry> = mutableListOf()
                    val ambientTempEntries: MutableList<Entry> = mutableListOf()
                    val waterTempEntries: MutableList<Entry> = mutableListOf()

                    heatingStats.forEachIndexed { index, entry ->
                        energyEntries.add(BarEntry(index.toFloat(), entry.energy.toFloat()))
                        ambientTempEntries.add(
                            Entry(
                                index.toFloat(),
                                entry.ambientTemperatureAverage.toFloat()
                            )
                        )
                        waterTempEntries.add(
                            Entry(
                                index.toFloat(),
                                entry.waterTemperatureAverage.toFloat()
                            )
                        )
                    }

                    // Energy Bar Data Set
                    val energyDataSet: BarDataSet = BarDataSet(
                        energyEntries, labelHeatingStatsEnergyWh
                    ).apply {
                        color = Color.RED
                        valueTextColor = Color.RED
                        valueTextSize = 10f
                        setDrawValues(false) // Optionally hide value text on bars
                    }

                    // Ambient Temperature Line Data Set
                    val ambientTempDataSet: LineDataSet =
                        LineDataSet(ambientTempEntries, labelAmbientTemperatureC).apply {
                            color =
                                heatingStatsLineColorDefinition(
                                    signal = HeatingStatsSignal.AMBIENT_TEMPERATURE
                                )
                            lineWidth = 2f
                            setDrawCircles(false)
                            setDrawValues(false)
                            axisDependency = YAxis.AxisDependency.RIGHT
                        }

                    // Water Temperature Line Data Set
                    val waterTempDataSet: LineDataSet =
                        LineDataSet(waterTempEntries, labelWaterTemperatureC).apply {
                            color =
                                heatingStatsLineColorDefinition(
                                    signal = HeatingStatsSignal.WATER_TEMPERATURE
                                )
                            lineWidth = 2f
                            setDrawCircles(false)
                            setDrawValues(false)
                            axisDependency = YAxis.AxisDependency.RIGHT
                        }

                    // Create CombinedData object
                    val combinedData: CombinedData = CombinedData().apply {
                        setData(BarData(energyDataSet))
                        setData(LineData(listOf(ambientTempDataSet, waterTempDataSet)))
                    }

                    // Set data and refresh the chart
                    chart.data = combinedData
                    chart.invalidate()
                } else {
                    // Handle empty data state
                    chart.clear()
                    chart.setNoDataText(labelNoDataAvailable) // Optional: Display a message
                    chart.invalidate()
                }
            }
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,

        ) {
            HeatingStatsLegendComposable()
            Spacer(modifier = Modifier.width(width = 32.dp))
            RoundRefreshButtonComposable(onClick = updateData)
        }
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    device = "spec:width=800dp,height=400dp,orientation=landscape"
)
@Composable
internal fun HeatingStatsComposableDarkPreview() {
    val isDarkMode: Boolean = isSystemInDarkTheme()
    AquariumControllerTheme(darkTheme = true) {
        Surface(color = MaterialTheme.colorScheme.background) {
            HeatingStatsComposable(heatingStats = mockHeatingStatsData, updateData = {}, isDarkMode)
        }
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO,
    device = "spec:width=800dp,height=400dp,orientation=landscape"
)
@Composable
internal fun HeatingStatsComposableLightPreview() {
    val isDarkMode: Boolean = isSystemInDarkTheme()
    AquariumControllerTheme(darkTheme = false) {
        Surface(color = MaterialTheme.colorScheme.background) {
            HeatingStatsComposable(heatingStats = mockHeatingStatsData, updateData = {}, isDarkMode)
        }
    }
}