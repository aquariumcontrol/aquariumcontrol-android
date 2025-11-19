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
import com.laimburggasse.aquariumcontrol.common.presentation.MainScaffold
import com.laimburggasse.aquariumcontrol.common.presentation.ScreenHeadlineComposable
import com.laimburggasse.aquariumcontrol.common.presentation.ViewNavigationRoutes
import com.laimburggasse.aquariumcontrol.timedata.R
import com.laimburggasse.aquariumcontrol.timedata.preview.TimeDataPreviewDataImpl.mockTimeDataUiStateEmpty
import com.laimburggasse.aquariumcontrol.timedata.preview.TimeDataPreviewDataImpl.mockTimeDataUiStateError
import com.laimburggasse.aquariumcontrol.timedata.preview.TimeDataPreviewDataImpl.mockTimeDataUiStateSuccess

const val chartHeight = 200
const val axisGranularityOnOff = 1.0f
const val axisGranularityTime = 14400f

@Composable
fun TimeDataScreen(timeDataUiState: TimeDataUiState, onTimeDataEvent: (TimeDataUiEvent) -> Unit) {
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
            onRefresh = { onTimeDataEvent(TimeDataUiEvent.Refresh) },
            indicator = {
                if (timeDataUiState is TimeDataUiState.Loading && displayCircularProgressIndicatorInPreview) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .size(size = 64.dp)
                            .fillMaxWidth(),
                        color = MaterialTheme.colorScheme.primary,
                        strokeWidth = 5.dp
                    )
                }
            }
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(state = rememberScrollState())
            ) {
                when (timeDataUiState) {
                    is TimeDataUiState.Empty -> {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(all = 16.dp),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = stringResource(id = R.string.no_time_data_available),
                                style = MaterialTheme.typography.titleMedium
                            )
                            Spacer(modifier = Modifier.height(height = 8.dp))
                            Button(onClick = { onTimeDataEvent(TimeDataUiEvent.Refresh) }) {
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
                            Button(onClick = { onTimeDataEvent(TimeDataUiEvent.Refresh) }) {
                                Text(text = stringResource(id = R.string.retry))
                            }
                        }
                    }
                    is TimeDataUiState.Loading -> {
                        Text(text = stringResource(id = R.string.loading_time_data))
                        CircularProgressIndicator(
                            modifier = Modifier.size(size = 150.dp),
                            color = MaterialTheme.colorScheme.primary
                        )
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
