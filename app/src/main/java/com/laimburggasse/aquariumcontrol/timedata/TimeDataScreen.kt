package com.laimburggasse.aquariumcontrol.timedata

import android.content.res.Configuration
import android.graphics.Color
import android.util.Log
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavHostController
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.laimburggasse.aquariumcontrol.MainViewModel
import com.laimburggasse.aquariumcontrol.R
import com.laimburggasse.aquariumcontrol.ViewNavigationRoutes
import com.laimburggasse.aquariumcontrol.lib_composables.ScreenHeadlineComposable
import com.laimburggasse.aquariumcontrol.maindropdownmenu.MainDropdownMenuComposable
import com.laimburggasse.aquariumcontrol.timedata.TimeDataUiState.Error
import com.laimburggasse.aquariumcontrol.timedata.TimeDataUiState.Loading
import com.laimburggasse.aquariumcontrol.timedata.TimeDataUiState.Success
import com.laimburggasse.aquariumcontrol.ui.theme.AquariumControllerTheme
import com.laimburggasse.aquariumcontrol.webapi.DataFetchResult
import com.laimburggasse.aquariumcontrol.webapi.WebApiUrls
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.sql.Timestamp
import java.time.LocalDateTime
import java.time.ZoneOffset
import kotlin.math.sin
import kotlin.random.Random

const val chartHeight = 200
const val axisGranularityOnOff = 1.0f
const val axisGranularityTime = 14400f

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun TimeDataScreen(
    navHostController: NavHostController? = null,
    mainViewModel: MainViewModel? = null,
    mockTimeDataUiState: TimeDataUiState? = null
) {
    // This screen is only displaying time data charts without any user interaction.
    // Therefore, this screen does not use a view model.
    val isDarkMode = isSystemInDarkTheme()

    val textColor = if (isDarkMode) Color.WHITE else Color.BLACK

    val isControllerAddressValid: Boolean = !mainViewModel?.getControllerAddress().isNullOrBlank()

    var timeDataUiState by remember { mutableStateOf<TimeDataUiState>(mockTimeDataUiState ?:Loading) }

    val coroutineScope = rememberCoroutineScope()

    val configuration = LocalConfiguration.current

    val displayCircularProgressIndicatorInPreview: Boolean = when (configuration.orientation) {
        Configuration.ORIENTATION_LANDSCAPE -> {
            false
        }
        else -> {
            if (mockTimeDataUiState != null) {
                mockTimeDataUiState is Loading
            }
            else {
                false
            }
        }
    }

    var isRefreshing by remember {
        mutableStateOf(
            displayCircularProgressIndicatorInPreview
        )
    }

    // Function to trigger data fetching
    fun loadData() {
        coroutineScope.launch {
            if (mockTimeDataUiState != null) { // For preview and testing
                timeDataUiState = mockTimeDataUiState
            } else {
                if (mainViewModel != null) {
                    fetchTimeData(mainViewModel) { newState ->
                        timeDataUiState = newState
                    }
                }
            }
        }
    }

    // Use a LaunchedEffect to fetch data on initial composition
    LaunchedEffect(key1 = isControllerAddressValid) { // Key on controller address validity
        if (mainViewModel != null && isControllerAddressValid) { // Check for null and validity
            loadData()
        }
    }

    val pullRefreshState = rememberPullRefreshState(
        refreshing = isRefreshing,
        onRefresh = {
            // This lambda will be called when the user pulls to refresh
            isRefreshing = true
            // Perform the refresh operation here (e.g., fetch new data)
            coroutineScope.launch {
                if (isControllerAddressValid) {
                    loadData()
                    delay(500)
                    isRefreshing = false // Reset the refresh state
                }
            }
        }
    )

    AquariumControllerTheme {
        Surface(
            modifier = Modifier,
            color = MaterialTheme.colorScheme.background
        ) {
            Box {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    ScreenHeadlineComposable(
                        modifier = Modifier,
                        text = stringResource(id = R.string.time_data_series),
                    )
                    Box(
                        modifier = Modifier.pullRefresh(pullRefreshState),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.verticalScroll(rememberScrollState())
                        ) {
                            when (val state = timeDataUiState) {
                                is Loading -> {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center,
                                        modifier = Modifier.fillMaxSize(),
                                    ) {
                                        CircularProgressIndicator(
                                            modifier = Modifier
                                                .size(64.dp)
                                                .fillMaxWidth(),
                                            color = MaterialTheme.colorScheme.primary,
                                            strokeWidth = 5.dp
                                        )
                                        Spacer(modifier = Modifier.height(16.dp))
                                        Text(
                                            modifier = Modifier.fillMaxWidth(),
                                            text = "Loading data...",
                                            textAlign = TextAlign.Center,
                                            style = MaterialTheme.typography.titleMedium
                                        )
                                    }
                                }
                                is Success -> {
                                    val timeDataSets = state.timeDataSets
                                    // Main temperature chart
                                    AndroidView (modifier = Modifier
                                        .fillMaxWidth()
                                        .height(chartHeight.dp), factory = { context ->
                                        LineChart(context).apply {
                                            data = timeDataSets.dataTemperature
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
                                        lineChart.data = timeDataSets.dataTemperature
                                        lineChart.notifyDataSetChanged()
                                        lineChart.invalidate()
                                    })

                                    // pH Chart
                                    AndroidView(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(chartHeight.dp), factory = { context ->
                                            LineChart(context).apply {
                                                data = timeDataSets.datapH
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
                                            lineChart.data = timeDataSets.datapH
                                            lineChart.notifyDataSetChanged()
                                            lineChart.invalidate()
                                        })

                                    // Conductivity chart
                                    AndroidView(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(chartHeight.dp), factory = { context ->
                                            LineChart(context).apply {
                                                data = timeDataSets.dataConductivity
                                                description.isEnabled = false
                                                legend.isEnabled = true
                                                legend.textColor = textColor
                                                axisRight.isEnabled = false
                                                axisLeft.valueFormatter =
                                                    TimeDataYAxisMicroSievertPerCentimeterValueFormatter()
                                                axisLeft.setDrawTopYLabelEntry(true)
                                                xAxis.granularity = axisGranularityTime
                                                xAxis.valueFormatter = TimeDataXAxisValueFormatter()
                                                xAxis.position = XAxis.XAxisPosition.BOTTOM
                                                xAxis.textColor = textColor
                                                axisLeft.textColor = textColor
                                            }
                                        }, update = { lineChart ->
                                            lineChart.data = timeDataSets.dataConductivity
                                            lineChart.notifyDataSetChanged()
                                            lineChart.invalidate()
                                        })

                                    // Tank level chart
                                    AndroidView (modifier = Modifier
                                        .fillMaxWidth()
                                        .height(chartHeight.dp), factory = { context ->
                                            LineChart(context).apply {
                                                data = timeDataSets.dataTankLevel
                                                description.isEnabled = false
                                                legend.isEnabled = true
                                                legend.textColor = textColor
                                                axisRight.isEnabled = false
                                                axisLeft.setDrawTopYLabelEntry(true)
                                                axisLeft.granularity = axisGranularityOnOff
                                                axisLeft.valueFormatter = TimeDataYAxisOnOffValueFormatter()
                                                xAxis.granularity = axisGranularityTime
                                                xAxis.valueFormatter = TimeDataXAxisValueFormatter()
                                                xAxis.position = XAxis.XAxisPosition.BOTTOM
                                                xAxis.textColor = textColor
                                                axisLeft.textColor = textColor
                                            }
                                        }, update = { lineChart ->
                                            lineChart.data = timeDataSets.dataTankLevel
                                            lineChart.notifyDataSetChanged()
                                            lineChart.invalidate()
                                        })

                                    // Ambient chart
                                    AndroidView(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(chartHeight.dp), factory = { context ->
                                            LineChart(context).apply {
                                                data = timeDataSets.dataAmbient
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
                                            lineChart.data = timeDataSets.dataAmbient
                                            lineChart.notifyDataSetChanged()
                                            lineChart.invalidate()
                                        })
                                }
                                is Error -> {
                                    // Show an error message and a retry button
                                    Column(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(16.dp),
                                        verticalArrangement = Arrangement.Center,
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(
                                            text = "Error loading data:",
                                            style = MaterialTheme.typography.titleMedium
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(
                                            text = state.errorMessage,
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.error
                                        )
                                        Spacer(modifier = Modifier.height(16.dp))
                                        Button(onClick = { loadData() }) { // Retry button
                                            Text("Retry")
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                MainDropdownMenuComposable(
                    navHostController = navHostController,
                    navigationRouteSource = ViewNavigationRoutes.timeData,
                    mainViewModel = mainViewModel
                )
            }
        }
    }
}

sealed class TimeDataUiState {
    object Loading : TimeDataUiState()
    data class Success(val timeDataSets: TimeDataSets) : TimeDataUiState()
    data class Error(val errorMessage: String) : TimeDataUiState()
}

private suspend fun fetchTimeData(
    mainViewModel: MainViewModel,
    updateUiState: (TimeDataUiState) -> Unit,
) {
    updateUiState(Loading) // Indicate loading state
    withContext(Dispatchers.IO) {
        val url =
            "http://" + mainViewModel.getControllerAddress() + WebApiUrls.DATA_GET_START_STOP
        val reader = TimeDataReader(url)
        when (val result = reader.readTimeDataSeries()) {
            is DataFetchResult.Success -> {
                val timeDataStringList = result.data
                if (timeDataStringList.isNotEmpty()) {
                    val timeDataSamples =
                        timeDataStringList.map { timeDataStringSample ->
                            timeDataStringSample.transformToTimeDataSample()
                        }
                    updateUiState(Success(TimeDataSets(timeDataSamples)))
                } else {
                    // Handle case where data is successfully fetched but is empty
                    // You might want a specific state for this or treat it as an error
                    updateUiState(Error("No data available."))
                }
            }
            is DataFetchResult.Error -> {
                Log.e("TimeDataScreen", "Error fetching time data: ${result.errorMessage}", result.exception)
                updateUiState(Error(result.errorMessage))
            }
        }
    }
}

fun generateMockTimeData(): List<TimeDataSample> {
    val startTime = LocalDateTime.of(2024, 10, 24, 0, 0) // October 24th, 2024, 0:00 AM
    val samples = mutableListOf<TimeDataSample>()
    val random = Random(12345) // arbitrary but fixed seed to have reproducible results

    // Helper function to add noise
    fun addNoise(value: Double, max: Double): Double {
        val noise = random.nextDouble(-0.01 * max, 0.01 * max)
        return value + noise
    }

    // Low-pass filter (simple moving average)
    fun lowPassFilter(data: List<Double>, windowSize: Int): List<Double> {
        val filteredData = mutableListOf<Double>()
        for (i in data.indices) {
            val start = maxOf(0, i - windowSize)
            val end = minOf(data.size, i + windowSize + 1)
            val window = data.subList(start, end)
            filteredData.add(window.average())
        }
        return filteredData
    }

    val temperatures = mutableListOf<Double>()
    val pHs = mutableListOf<Double>()
    val conductivities = mutableListOf<Double>()
    val ambientTemperatures = mutableListOf<Double>()
    val ambientHumidities = mutableListOf<Double>()

    for (i in 0 until 24 * 60) { // 24 hours * 60 minutes
        val currentTime = startTime.plusMinutes(i.toLong())
        val secondsOfDay = currentTime.toEpochSecond(ZoneOffset.UTC)
        val timestamp = Timestamp(secondsOfDay * 1000)

        // Sinus profiles with noise
        val temperature = 26.0 + sin(2 * Math.PI * i / (24 * 60)) + addNoise(0.0, 27.0)
        temperatures.add(temperature)

        val pH = 7.5 + 0.5 * sin(2 * Math.PI * i / (24 * 60)) + addNoise(0.0, 8.0)
        pHs.add(pH)

        val conductivity =
            51000.0 + 1000 * sin(2 * Math.PI * i / (24 * 60)) + addNoise(0.0, 52000.0)
        conductivities.add(conductivity)

        val ambientTemperature =
            23.5 + 1.5 * sin(2 * Math.PI * i / (24 * 60)) + addNoise(0.0, 25.0)
        ambientTemperatures.add(ambientTemperature)

        val ambientHumidity = 55.0 + 5 * sin(2 * Math.PI * i / 60) + addNoise(0.0, 60.0)
        ambientHumidities.add(ambientHumidity)

        // Other values
        val refillInProgress = if (i % 70 == 0) 1 else 0
        val mainTankLevelSwitchStatus = if (i % 60 == 0) 0 else 1
        val surfaceVentilation = if (i in 12 * 60 until 17 * 60) 1 else 0 // 12:00 to 17:00
        val heater = if (i in 9 * 60 until 20 * 60) 0 else 1 // 9:00 to 20:00

        val timeDataSample = TimeDataSample(
            timestamp = timestamp,
            temperature = temperature,
            temperatureFiltered = temperature,
            pH = pH,
            pHFiltered = pH,
            conductivity = conductivity,
            conductivityFiltered = conductivity,
            ambientTemperature = ambientTemperature,
            ambientHumidity = ambientHumidity,
            refillInProgress = refillInProgress,
            mainTankLevelSwitchStatus = mainTankLevelSwitchStatus,
            surfaceVentilation = surfaceVentilation,
            heater = heater,
        )

        samples.add(
            timeDataSample
        )
    }

    // Apply low-pass filter
    val filteredTemperatures = lowPassFilter(temperatures, 10)
    val filteredPHs = lowPassFilter(pHs, 10)
    val filteredConductivities = lowPassFilter(conductivities, 10)

    // Update samples with filtered values
    for (i in samples.indices) {
        samples[i] = samples[i].copy(
            temperatureFiltered = filteredTemperatures[i],
            pHFiltered = filteredPHs[i],
            conductivityFiltered = filteredConductivities[i]
        )
    }

    return samples
}

@Preview(showBackground = true, heightDp = 1250, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(showBackground = true, heightDp = 1250, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    device = "spec:width=1600dp,height=1200dp,orientation=landscape"
)
@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO,
    device = "spec:width=1600dp,height=1200dp,orientation=landscape"
)
@Composable
internal fun TimeDataScreenPreview_Success() {
    TimeDataScreen(mockTimeDataUiState = Success(TimeDataSets(generateMockTimeData())))
}

@Preview(showBackground = true, heightDp = 250, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(showBackground = true, heightDp = 250, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    device = "spec:width=250dp,height=350dp,orientation=landscape"
)
@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO,
    device = "spec:width=250dp,height=350dp,orientation=landscape"
)
@Composable
internal fun TimeDataScreenPreview_Error() {
    TimeDataScreen(mockTimeDataUiState = Error("Error message"))
}

@Preview(showBackground = true, heightDp = 250, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(showBackground = true, heightDp = 250, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    device = "spec:width=250dp,height=350dp,orientation=landscape"
)
@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO,
    device = "spec:width=250dp,height=350dp,orientation=landscape"
)
@Composable
internal fun TimeDataScreenPreview_Loading() {
    TimeDataScreen(mockTimeDataUiState = Loading)
}
