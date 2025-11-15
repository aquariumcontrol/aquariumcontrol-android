package com.laimburggasse.aquariumcontrol.overview.presentation

import android.content.res.Configuration
import androidx.compose.foundation.isSystemInDarkTheme
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
import com.laimburggasse.aquariumcontrol.MainScaffold
import com.laimburggasse.aquariumcontrol.R
import com.laimburggasse.aquariumcontrol.ViewNavigationRoutes
import com.laimburggasse.aquariumcontrol.controller.domain.ControllerProfile
import com.laimburggasse.aquariumcontrol.lib_composables.ScreenHeadlineComposable
import com.laimburggasse.aquariumcontrol.overview.domain.model.OverviewData

@Composable
fun OverviewScreen(
    controllerProfile: ControllerProfile?,
    overviewUiState: OverviewUiState,
    onOverviewEvent: (OverviewUiEvent) -> Unit,
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
        ScreenHeadlineComposable(text = stringResource(id = R.string.overview))
        PullToRefreshBox(
            modifier = Modifier.fillMaxWidth(),
            state = rememberPullToRefreshState(),
            isRefreshing = overviewUiState == OverviewUiState.Loading,
            contentAlignment = Alignment.Center,
            onRefresh = { onOverviewEvent(OverviewUiEvent.Refresh) },
            indicator = {
                if (overviewUiState == OverviewUiState.Loading) {
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
                when (overviewUiState) {
                    is OverviewUiState.Error -> {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(all = 16.dp),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = stringResource(id = R.string.error_loading_data) +
                                    overviewUiState.errorMessage
                            )
                        }
                    }
                    OverviewUiState.Loading -> {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(all = 16.dp),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(text = stringResource(id = R.string.loading_overview_data))
                        }
                    }
                    is OverviewUiState.Success -> {
                        val overviewData: OverviewData = overviewUiState.overviewData
                        when (configuration.orientation) {
                            Configuration.ORIENTATION_LANDSCAPE -> {
                                Row {
                                    Column(modifier = Modifier.weight(weight = 0.5f)) {
                                        OverviewControllerComposable(
                                            controllerProfileName = controllerProfile?.name
                                                ?: "***",
                                            controllerAddress = controllerProfile?.address ?: "***",
                                            controllerPort = controllerProfile?.port ?: 0,
                                            controllerUsername = controllerProfile?.user ?: "***"
                                        )
                                        OverviewAmbientComposable(overviewData = overviewData)
                                    }
                                    Column(modifier = Modifier.weight(weight = 0.5f)) {
                                        OverviewTimestampComposable(overviewData = overviewData)
                                        OverviewWaterSignalsComposable(overviewData = overviewData)
                                        OverviewActuatorsComposable(overviewData = overviewData)
                                    }
                                }
                            }

                            else -> {
                                OverviewControllerComposable(
                                    controllerProfileName = controllerProfile?.name ?: "***",
                                    controllerAddress = controllerProfile?.address ?: "***",
                                    controllerPort = controllerProfile?.port ?: 0,
                                    controllerUsername = controllerProfile?.user ?: "***"
                                )
                                OverviewTimestampComposable(overviewData = overviewData)
                                OverviewWaterSignalsComposable(overviewData = overviewData)
                                OverviewActuatorsComposable(overviewData = overviewData)
                                OverviewAmbientComposable(overviewData = overviewData)
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.padding(vertical = 8.dp))
                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    onClick = { onOverviewEvent(OverviewUiEvent.Refresh) },
                ) {
                    Text(
                        text = stringResource(id = R.string.update_overview_data),
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}

val mockOverviewData = OverviewData(
    timestamp = "2023-05-01 12:00:00",
    temperatureWater = 25.0,
    temperatureWaterFiltered = 25.5,
    pH = 7.0,
    pHFiltered = 7.5,
    conductivity = 1000.0,
    conductivityFiltered = 1005.0,
    tankLevelSwitchPosition = "HIGH",
    surfaceVentilation = "ON",
    temperatureAmbient = 22.0,
    humidityAmbient = 60.0,
    heater = "OFF"
)

val mockControllerProfile = ControllerProfile(
    name = "Controller 1",
    address = "192.168.1.1",
    port = 80,
    user = "user",
)

val mockOverviewUiStateSuccess = OverviewUiState.Success(mockOverviewData)
val mockOverviewUiStateError = OverviewUiState.Error(errorMessage = "Mock Error loading data")

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    device = "spec:width=800dp,height=400dp,orientation=landscape"
)
@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO,
    device = "spec:width=800dp,height=400dp,orientation=landscape"
)
@Composable
fun OverviewScreenPreviewSuccess() {
    MainScaffold(
        isControllerLoaded = true,
        navigationRouteSource = ViewNavigationRoutes.overView,
        screenContent = {
            OverviewScreen(
                controllerProfile = mockControllerProfile,
                overviewUiState = mockOverviewUiStateSuccess,
                onOverviewEvent = {},
            )
        },
        onNavigate = {},
        darkTheme = isSystemInDarkTheme()
    )
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
fun OverviewScreenPreviewError() {
    MainScaffold(
        isControllerLoaded = true,
        navigationRouteSource = ViewNavigationRoutes.overView,
        screenContent = {
            OverviewScreen(
                controllerProfile = mockControllerProfile,
                overviewUiState = mockOverviewUiStateError,
                onOverviewEvent = {},
            )
        },
        onNavigate = {},
        darkTheme = isSystemInDarkTheme()
    )
}