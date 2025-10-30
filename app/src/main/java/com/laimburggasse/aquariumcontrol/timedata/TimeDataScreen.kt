package com.laimburggasse.aquariumcontrol.timedata

import android.content.res.Configuration
import android.graphics.Color
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.laimburggasse.aquariumcontrol.MainScaffold
import com.laimburggasse.aquariumcontrol.R
import com.laimburggasse.aquariumcontrol.ViewNavigationRoutes
import com.laimburggasse.aquariumcontrol.lib_composables.ScreenHeadlineComposable
import java.sql.Timestamp
import java.time.LocalDateTime
import java.time.ZoneOffset
import kotlin.math.sin
import kotlin.random.Random

const val chartHeight = 200
const val axisGranularityOnOff = 1.0f
const val axisGranularityTime = 14400f

@Composable
fun TimeDataScreen(timeDataUiState: TimeDataUiState, onTimeDataEvent: (TimeDataEvent) -> Unit) {
    // This screen displays time data charts without data flow back to server.
    val isDarkMode: Boolean = isSystemInDarkTheme()
    val textColor: Int = if (isDarkMode) Color.WHITE else Color.BLACK
    val displayCircularProgressIndicatorInPreview: Boolean =
        LocalConfiguration.current.orientation != Configuration.ORIENTATION_LANDSCAPE

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        ScreenHeadlineComposable(text = stringResource(id = R.string.time_data_series))
        PullToRefreshBox(
            modifier = Modifier.fillMaxWidth(),
            state = rememberPullToRefreshState(),
            isRefreshing = timeDataUiState is TimeDataUiState.Loading,
            contentAlignment = Alignment.Center,
            onRefresh = { onTimeDataEvent(TimeDataEvent.Refresh) },
            indicator = {
                if (timeDataUiState is TimeDataUiState.Loading && displayCircularProgressIndicatorInPreview) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(size = 64.dp).fillMaxWidth(),
                        color = MaterialTheme.colorScheme.primary,
                        strokeWidth = 5.dp
                    )
                }
            }
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxSize().verticalScroll(state = rememberScrollState())
            ) {
                when (timeDataUiState) {
                    is TimeDataUiState.Empty -> {
                        Column(
                            modifier = Modifier.fillMaxSize().padding(all = 16.dp),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "No data available.",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Spacer(modifier = Modifier.height(height = 8.dp))
                            Button(onClick = { onTimeDataEvent(TimeDataEvent.Refresh) }) {
                                Text(text = "Retry")
                            }
                        }
                    }
                    is TimeDataUiState.Error -> {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(all = 16.dp),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Error loading data:",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Spacer(modifier = Modifier.height(height = 8.dp))
                            Text(
                                text = timeDataUiState.errorMessage,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.error
                            )
                            Spacer(modifier = Modifier.height(height = 16.dp))
                            Button(onClick = { onTimeDataEvent(TimeDataEvent.Refresh) }) {
                                Text("Retry")
                            }
                        }
                    }
                    is TimeDataUiState.Loading -> {
                        Text(text = "Loading...")
                    }
                    is TimeDataUiState.Success -> {
                        val timeDataSets: TimeDataSets = timeDataUiState.timeDataSets
                        TimeDataTemperatureChartComposable(
                            temperatureDataSet = timeDataSets.dataTemperature,
                            textColor = textColor
                        )

                        TimeDatapHChartComposable(
                            pHDataSet = timeDataSets.datapH,
                            textColor = textColor
                        )

                        TimeDataConductivityChartComposable(
                            conductivityDataSet = timeDataSets.dataConductivity,
                            textColor = textColor
                        )

                        TimeDataTankLevelChartComposable(
                            tankLevelDataSet = timeDataSets.dataTankLevel,
                            textColor = textColor
                        )

                        TimeDataAmbientChartComposable(
                            ambientDataSet = timeDataSets.dataAmbient,
                            textColor = textColor
                        )

                    }
                }
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

val mockTimeDataSets = TimeDataSets(timeData = generateMockTimeData())

val mockTimeDataUiStateSuccess = TimeDataUiState.Success(timeDataSets = mockTimeDataSets)

val mockTimeDataUiStateError = TimeDataUiState.Error(errorMessage = "Error loading data")

val mockTimeDataUiStateEmpty = TimeDataUiState.Empty

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
internal fun TimeDataScreenPreviewSuccess() {
    MainScaffold(
        isControllerLoaded = true,
        navigationRouteSource = ViewNavigationRoutes.timeData,
        screenContent = {
            TimeDataScreen(
                timeDataUiState = mockTimeDataUiStateSuccess,
                onTimeDataEvent = {}
            )
        },
        onNavigate = {},
        darkTheme = isSystemInDarkTheme()
    )
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
internal fun TimeDataScreenPreviewError() {
    MainScaffold(
        isControllerLoaded = true,
        navigationRouteSource = ViewNavigationRoutes.timeData,
        screenContent = { TimeDataScreen(
            timeDataUiState = mockTimeDataUiStateError,
            onTimeDataEvent = {}
        ) },
        onNavigate = {},
        darkTheme = isSystemInDarkTheme()
    )
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
internal fun TimeDataScreenPreviewEmpty() {
    MainScaffold(
        isControllerLoaded = true,
        navigationRouteSource = ViewNavigationRoutes.timeData,
        screenContent = {
            TimeDataScreen(
                timeDataUiState = mockTimeDataUiStateEmpty,
                onTimeDataEvent = {}
            )
        },
        onNavigate = {},
        darkTheme = isSystemInDarkTheme()
    )
}
