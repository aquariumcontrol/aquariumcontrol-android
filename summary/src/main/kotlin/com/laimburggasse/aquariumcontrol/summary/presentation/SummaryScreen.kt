/*
 *  Copyright 2026 Uwe Martin
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.laimburggasse.aquariumcontrol.summary.presentation

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.unit.sp
import com.laimburggasse.aquariumcontrol.common.domain.ControllerProfile
import com.laimburggasse.aquariumcontrol.common.presentation.MainScaffold
import com.laimburggasse.aquariumcontrol.common.presentation.ScreenHeadlineComposable
import com.laimburggasse.aquariumcontrol.common.presentation.ViewNavigationRoutes
import com.laimburggasse.aquariumcontrol.summary.R
import com.laimburggasse.aquariumcontrol.summary.domain.model.SummaryData
import com.laimburggasse.aquariumcontrol.summary.preview.SummaryPreviewDataImpl.mockControllerProfile
import com.laimburggasse.aquariumcontrol.summary.preview.SummaryPreviewDataImpl.mockSummaryUiStateError
import com.laimburggasse.aquariumcontrol.summary.preview.SummaryPreviewDataImpl.mockSummaryUiStateSuccess

@Composable
fun SummaryScreen(
    controllerProfile: ControllerProfile?,
    summaryUiState: SummaryUiState,
    onSummaryEvent: (SummaryUiEvent) -> Unit,
) {
    // This screen displays
    // - headline
    // - the overview data of the aquarium: Text for signal description and current value
    // - a button to update the overview data

    val configuration: Configuration = LocalConfiguration.current

    Column(
        modifier = Modifier.padding(all = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ScreenHeadlineComposable(text = stringResource(id = R.string.summary))
        PullToRefreshBox(
            modifier = Modifier.fillMaxWidth(),
            state = rememberPullToRefreshState(),
            isRefreshing = summaryUiState == SummaryUiState.Loading,
            contentAlignment = Alignment.Center,
            onRefresh = { onSummaryEvent(SummaryUiEvent.Refresh) },
            indicator = {
                if (summaryUiState == SummaryUiState.Loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(size = 150.dp),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(state = rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                when (summaryUiState) {
                    is SummaryUiState.Error -> {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(all = 16.dp),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = stringResource(id = R.string.error_loading_summary_data) +
                                    summaryUiState.errorMessage
                            )
                        }
                    }
                    SummaryUiState.Loading -> {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(all = 16.dp),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(text = stringResource(id = R.string.loading_summary_data))
                        }
                    }
                    is SummaryUiState.Success -> {
                        val summaryData: SummaryData = summaryUiState.summaryData
                        when (configuration.orientation) {
                            Configuration.ORIENTATION_LANDSCAPE -> {
                                Row {
                                    Column(modifier = Modifier.weight(weight = 0.5f)) {
                                        SummaryControllerComposable(
                                            controllerProfileName = controllerProfile?.name
                                                ?: "***",
                                            controllerAddress = controllerProfile?.address ?: "***",
                                            controllerPort = controllerProfile?.port ?: 0,
                                            controllerUsername = controllerProfile?.user ?: "***"
                                        )
                                        SummaryAmbientComposable(summaryData = summaryData)
                                    }
                                    Column(modifier = Modifier.weight(weight = 0.5f)) {
                                        SummaryTimestampComposable(summaryData = summaryData)
                                        SummaryWaterSignalsComposable(summaryData = summaryData)
                                        SummaryActuatorsComposable(summaryData = summaryData)
                                    }
                                }
                            }

                            else -> {
                                SummaryControllerComposable(
                                    controllerProfileName = controllerProfile?.name ?: "***",
                                    controllerAddress = controllerProfile?.address ?: "***",
                                    controllerPort = controllerProfile?.port ?: 0,
                                    controllerUsername = controllerProfile?.user ?: "***"
                                )
                                SummaryTimestampComposable(summaryData = summaryData)
                                SummaryWaterSignalsComposable(summaryData = summaryData)
                                SummaryActuatorsComposable(summaryData = summaryData)
                                SummaryAmbientComposable(summaryData = summaryData)
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.padding(vertical = 8.dp))
                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    onClick = { onSummaryEvent(SummaryUiEvent.Refresh) },
                ) {
                    Text(
                        text = stringResource(id = R.string.update_summary_data),
                        fontSize = 16.sp
                    )
                }
            }
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
fun SummaryScreenSuccessDarkPreview() {
    MainScaffold(
        isControllerLoaded = true,
        navigationRouteSource = ViewNavigationRoutes.summary,
        screenContent = {
            SummaryScreen(
                controllerProfile = mockControllerProfile,
                summaryUiState = mockSummaryUiStateSuccess,
                onSummaryEvent = {},
            )
        },
        onNavigate = {},
        darkTheme = true
    )
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO,
    device = "spec:width=800dp,height=400dp,orientation=landscape"
)
@Composable
fun SummaryScreenSuccessLightPreview() {
    MainScaffold(
        isControllerLoaded = true,
        navigationRouteSource = ViewNavigationRoutes.summary,
        screenContent = {
            SummaryScreen(
                controllerProfile = mockControllerProfile,
                summaryUiState = mockSummaryUiStateSuccess,
                onSummaryEvent = {},
            )
        },
        onNavigate = {},
        darkTheme = false
    )
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun SummaryScreenErrorDarkPreview() {
    MainScaffold(
        isControllerLoaded = true,
        navigationRouteSource = ViewNavigationRoutes.summary,
        screenContent = {
            SummaryScreen(
                controllerProfile = mockControllerProfile,
                summaryUiState = mockSummaryUiStateError,
                onSummaryEvent = {},
            )
        },
        onNavigate = {},
        darkTheme = true
    )
}


@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
fun SummaryScreenErrorLightPreview() {
    MainScaffold(
        isControllerLoaded = true,
        navigationRouteSource = ViewNavigationRoutes.summary,
        screenContent = {
            SummaryScreen(
                controllerProfile = mockControllerProfile,
                summaryUiState = mockSummaryUiStateError,
                onSummaryEvent = {},
            )
        },
        onNavigate = {},
        darkTheme = false
    )
}