package com.laimburggasse.aquariumcontrol.overview

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.laimburggasse.aquariumcontrol.MainViewModel
import com.laimburggasse.aquariumcontrol.MockMainViewModel
import com.laimburggasse.aquariumcontrol.R
import com.laimburggasse.aquariumcontrol.ViewNavigationRoutes
import com.laimburggasse.aquariumcontrol.controller.ControllerProfile
import com.laimburggasse.aquariumcontrol.lib_composables.ScreenHeadlineComposable
import com.laimburggasse.aquariumcontrol.maindropdownmenu.MainDropdownMenuComposable
import com.laimburggasse.aquariumcontrol.ui.theme.AquariumControllerTheme

@Composable
fun OverviewScreen(
    navHostController: NavHostController? = null,
    mainViewModel: MainViewModel,
    overviewViewModel: OverviewViewModelInterface = viewModel<OverviewViewModel>(),
    darkTheme: Boolean? = null
) {
    // This screen displays
    // - headline
    // - the overview data of the aquarium: Text for signal description and current value
    // - a button to update the overview data

    val configuration: Configuration = LocalConfiguration.current
    val isControllerAddressValid: Boolean = !mainViewModel.getControllerAddress().isNullOrBlank()
    val isRefreshing: Boolean by remember { overviewViewModel::isRefreshing }
    val overviewUiState: OverviewUiState by remember { overviewViewModel::overviewUiState }

    val controllerProfileName: String? = mainViewModel.getControllerName()
    val controllerAddress: String? = mainViewModel.getControllerAddress()
    val controllerUsername: String? = mainViewModel.getControllerUser()
    val controllerPort: Int? = mainViewModel.getControllerPort()


    // Use a LaunchedEffect to fetch data on initial composition
    LaunchedEffect(key1 = isControllerAddressValid) { // Key on controller address validity
        if (isControllerAddressValid) {
            overviewViewModel.loadData(mainViewModel)
        }
    }

    AquariumControllerTheme(darkTheme) {
        // A surface container using the 'background' color from the theme
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Box {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    ScreenHeadlineComposable(
                        text = stringResource(id = R.string.overview)
                    )
                    PullToRefreshBox(
                        modifier = Modifier.fillMaxWidth(),
                        state = rememberPullToRefreshState(),
                        isRefreshing = isRefreshing,
                        contentAlignment = Alignment.Center,
                        onRefresh = { overviewViewModel.loadData(mainViewModel) },
                        indicator = {
                            if (isRefreshing) {
                                CircularProgressIndicator(
                                    modifier = Modifier
                                        .size(150.dp),
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    )
                    {
                        Column(
                            modifier = Modifier.fillMaxWidth().verticalScroll(state = rememberScrollState()),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            when (overviewUiState) {
                                is OverviewUiState.Error -> {
                                    Column(
                                        modifier = Modifier.fillMaxSize().padding(16.dp),
                                        verticalArrangement = Arrangement.Center,
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text("Error loading data: ${(overviewUiState as OverviewUiState.Error).errorMessage}")
                                    }                                }
                                OverviewUiState.Loading -> {
                                    Column(
                                        modifier = Modifier.fillMaxSize().padding(16.dp),
                                        verticalArrangement = Arrangement.Center,
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text("Loading...")
                                    }
                                }
                                is OverviewUiState.Success -> {
                                    val overviewData = (overviewUiState as OverviewUiState.Success).overviewData
                                    when (configuration.orientation) {
                                        Configuration.ORIENTATION_LANDSCAPE -> {
                                            Row {
                                                Column(modifier = Modifier.weight(0.5f)) {
                                                    OverviewControllerComposable(
                                                        controllerProfileName = controllerProfileName!!,
                                                        controllerAddress = controllerAddress!!,
                                                        controllerPort = controllerPort!!,
                                                        controllerUsername = controllerUsername!!
                                                    )
                                                    OverviewAmbientComposable(overviewData = overviewData)
                                                }
                                                Column(modifier = Modifier.weight(0.5f)) {
                                                    OverviewTimestampComposable(overviewData = overviewData)
                                                    OverviewWaterSignalsComposable(overviewData = overviewData)
                                                    OverviewActuatorsComposable(overviewData = overviewData)
                                                }
                                            }
                                        }

                                        else -> {
                                            OverviewControllerComposable(
                                                controllerProfileName = controllerProfileName!!,
                                                controllerAddress = controllerAddress!!,
                                                controllerPort = controllerPort!!,
                                                controllerUsername = controllerUsername!!
                                            )
                                            OverviewTimestampComposable(overviewData = overviewData)
                                            OverviewWaterSignalsComposable(overviewData = overviewData)
                                            OverviewActuatorsComposable(overviewData = overviewData)
                                            OverviewAmbientComposable(overviewData = overviewData)
                                        }
                                    }
                                }
                            }
                            Spacer(
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                            Button(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 8.dp),
                                onClick = {
                                    if (isControllerAddressValid) {
                                        overviewViewModel.loadData(mainViewModel)
                                    }
                                },
                            ) {
                                Text(
                                    text = stringResource(id = R.string.update_overview_data),
                                    fontSize = 16.sp
                                )
                            }
                        }
                    }
                }
                MainDropdownMenuComposable(
                    navHostController = navHostController,
                    navigationRouteSource = ViewNavigationRoutes.overView,
                    mainViewModel = mainViewModel
                )
            }
        }
    }
}

fun createMockOverviewData(): OverviewData {
    return OverviewData(
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
}

val mockControllerProfile = ControllerProfile(
    name = "Controller 1",
    address = "192.168.1.1",
    port = 80,
    user = "user",
)
val mockOverviewViewModelSuccess = MockOverviewViewModel(
    mockOverviewUiState = OverviewUiState.Success(overviewData = createMockOverviewData())
)
val mockOverviewViewModelError = MockOverviewViewModel(
    mockOverviewUiState = OverviewUiState.Error(errorMessage = "Error message")
)
val mockOverviewViewModelLoading = MockOverviewViewModel(
    mockOverviewUiState = OverviewUiState.Loading
)
val mockMainViewModel = MockMainViewModel(controller = mockControllerProfile)

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
    OverviewScreen(
        mainViewModel = mockMainViewModel,
        overviewViewModel = mockOverviewViewModelSuccess,
    )
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
fun OverviewScreenPreviewError() {
    OverviewScreen(
        mainViewModel = mockMainViewModel,
        overviewViewModel = mockOverviewViewModelError,
    )
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
fun OverviewScreenPreviewLoading() {
    OverviewScreen(
        mainViewModel = mockMainViewModel,
        overviewViewModel = mockOverviewViewModelLoading,
    )
}