package com.example.aquariumcontroller.heating

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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.aquariumcontroller.lib_composables.RoundRefreshButtonComposable
import com.example.aquariumcontroller.ui.theme.AquariumControllerTheme
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
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun HeatingStatsComposable(heatingStats: List<HeatingStatsEntry>, updateData: () -> Unit) {
    // This composable is only displaying time data charts without any user interaction.
    // Therefore, this composable does not use a view model.
    val isDarkMode = isSystemInDarkTheme()
    val textColor = if (isDarkMode) Color.WHITE else Color.BLACK

    Column(horizontalAlignment = CenterHorizontally) {
        AndroidView(
            modifier = Modifier
                .weight(1f)
                .fillMaxSize()
                .padding(16.dp), // Add some padding around the chart
            factory = { context ->
                CombinedChart(context).apply {
                    // Chart configuration
                    description.isEnabled = false // Disable description
                    legend.isEnabled = false
                    setDrawGridBackground(false) // No grid
                    isDragEnabled = true
                    setScaleEnabled(true)  //Allow zooming
                    setPinchZoom(true)    //Allow zooming via Pinc gesture

                    // X-Axis configuration
                    xAxis.position = XAxis.XAxisPosition.BOTTOM
                    xAxis.granularity = 1f // One day per label
                    xAxis.textColor = textColor
                    xAxis.valueFormatter = object : ValueFormatter() {
                        private val dateFormat =
                            SimpleDateFormat("dd.MM", Locale.US) //Format to Day and month.

                        override fun getFormattedValue(value: Float): String {
                            // Convert the float value (which represents days) back to a Date object.
                            val date = heatingStats.getOrNull(value.toInt())?.date
                            return date?.let { dateFormat.format(it) }
                                ?: "" // Return formatted date or empty
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
                    val energyEntries = mutableListOf<BarEntry>()
                    val ambientTempEntries = mutableListOf<Entry>()
                    val waterTempEntries = mutableListOf<Entry>()

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
                    val energyDataSet = BarDataSet(energyEntries, "Energy [Wh]").apply {
                        color = Color.RED
                        valueTextColor = Color.RED
                        valueTextSize = 10f
                        setDrawValues(false) // Optionally hide value text on bars
                    }

                    // Ambient Temperature Line Data Set
                    val ambientTempDataSet =
                        LineDataSet(ambientTempEntries, "Ambient Temperature [°C]").apply {
                            color =
                                heatingStatsLineColorDefinition(HeatingStatsSignal.AMBIENT_TEMPERATURE)
                            lineWidth = 2f
                            setDrawCircles(false)
                            setDrawValues(false)
                            axisDependency = YAxis.AxisDependency.RIGHT
                        }

                    // Water Temperature Line Data Set
                    val waterTempDataSet =
                        LineDataSet(waterTempEntries, "Water Temperature [°C]").apply {
                            color =
                                heatingStatsLineColorDefinition(HeatingStatsSignal.WATER_TEMPERATURE)
                            lineWidth = 2f
                            setDrawCircles(false)
                            setDrawValues(false)
                            axisDependency = YAxis.AxisDependency.RIGHT
                        }

                    // Create CombinedData object
                    val combinedData = CombinedData().apply {
                        setData(BarData(energyDataSet))
                        setData(LineData(listOf(ambientTempDataSet, waterTempDataSet)))
                    }

                    // Set data and refresh the chart
                    chart.data = combinedData
                    chart.invalidate()
                } else {
                    // Handle empty data state (e.g., show a placeholder or clear the chart)
                    chart.clear()
                    chart.setNoDataText("No data available") // Optional: Display a message
                    chart.invalidate()
                }
            }
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,

        ) {
            HeatingStatsLegend()
            Spacer(modifier = Modifier.width(32.dp))
            RoundRefreshButtonComposable(onClick = updateData)
        }
    }
}

fun createMockHeatingStatsData(): List<HeatingStatsEntry> {
    val mockHeatingStatsStringEntries = listOf(
        HeatingStatsStringEntry(
            dateString = "2025-1-12",
            energy = 1988.17,
            ambientTemperatureAverage = 22.6869,
            waterTemperatureAverage = 25.4176,
            heatingControlRuntime = 36235
        ),
        HeatingStatsStringEntry(
            dateString = "2025-1-25",
            energy = 18.7222,
            ambientTemperatureAverage = 18.9717,
            waterTemperatureAverage = 25.4007,
            heatingControlRuntime = 389
        )
    )

    return mockHeatingStatsStringEntries.map {
        it.transformToHeatingStatsEntry()
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PreviewHeatingStatsComposable() {
    val mockData = createMockHeatingStatsData()

    AquariumControllerTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            HeatingStatsComposable(heatingStats = mockData, updateData = {})
        }
    }
}