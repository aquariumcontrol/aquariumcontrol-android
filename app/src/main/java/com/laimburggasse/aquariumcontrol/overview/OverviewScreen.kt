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
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.laimburggasse.aquariumcontrol.MainViewModel
import com.laimburggasse.aquariumcontrol.R
import com.laimburggasse.aquariumcontrol.ViewNavigationRoutes
import com.laimburggasse.aquariumcontrol.controller.ControllerProfile
import com.laimburggasse.aquariumcontrol.lib_composables.ScreenHeadlineComposable
import com.laimburggasse.aquariumcontrol.maindropdownmenu.MainDropdownMenuComposable
import com.laimburggasse.aquariumcontrol.ui.theme.AquariumControllerTheme
import com.laimburggasse.aquariumcontrol.webapi.DataFetchResult
import com.laimburggasse.aquariumcontrol.webapi.WebApiUrls
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun OverviewScreen(
    navHostController: NavHostController? = null,
    mockController: ControllerProfile? = null,
    mockOverviewUiState: OverviewUiState? = null,
    mainViewModel: MainViewModel? = null,
    darkTheme: Boolean? = null
) {
    // This screen displays
    // - headline
    // - the overview data of the aquarium: Text for signal description and current value
    // - a button to update the overview data

    val configuration = LocalConfiguration.current

    // display circular progress indicator in preview for portrait mode
    val displayCircularProgressIndicatorInPreview: Boolean = when (configuration.orientation) {
        Configuration.ORIENTATION_LANDSCAPE -> {
            false
        }
        else -> {
            mockOverviewUiState == OverviewUiState.Loading
        }
    }

    var isRefreshing by remember {
        mutableStateOf(
            displayCircularProgressIndicatorInPreview
        )
    }

    val coroutineScope = rememberCoroutineScope()
    val isControllerAddressValid: Boolean = !mainViewModel?.getControllerAddress().isNullOrBlank()

    val controllerProfileName: String?
    val controllerAddress: String?
    val controllerUsername: String?
    val controllerPort: Int?
    var overviewUiState by remember { mutableStateOf(mockOverviewUiState ?: OverviewUiState.Loading) }

    fun loadData(onFinished: () -> Unit = {}) {
        coroutineScope.launch {
            isRefreshing = true
            if (mainViewModel != null) {
                val result = fetchOverviewData(mainViewModel)
                // Switch to the main thread to update the UI
                withContext(Dispatchers.Main) {
                    overviewUiState = when (result) {
                        is DataFetchResult.Error -> {
                            OverviewUiState.Error(errorMessage = result.errorMessage)
                        }
                        is DataFetchResult.Success -> {
                            OverviewUiState.Success(overviewData = result.data)
                        }
                    }
                }
            }
            // onFinished may alter the UI, so also switch to main thread
            withContext(Dispatchers.Main) {
                onFinished()
            }
        }
    }

    if (mockController == null) {
        controllerProfileName = mainViewModel!!.getControllerName()
        controllerAddress = mainViewModel.getControllerAddress()
        controllerUsername = mainViewModel.getControllerUser()
        controllerPort = mainViewModel.getControllerPort()
        //LaunchedEffect(Unit) {
        //    loadData { isRefreshing = false }
        //}
    } else {
        controllerProfileName = mockController.name
        controllerAddress = mockController.address
        controllerUsername = mockController.user
        controllerPort = mockController.port
    }

    // Use a LaunchedEffect to fetch data on initial composition
    LaunchedEffect(key1 = isControllerAddressValid) { // Key on controller address validity
        if (mainViewModel != null && isControllerAddressValid) { // Check for null and validity
            loadData { isRefreshing = false }
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
                        onRefresh = { loadData { isRefreshing = false } },
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
                            modifier = Modifier
                                .fillMaxWidth(),
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
                                        loadData { isRefreshing = false }
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

private suspend fun fetchOverviewData(mainViewModel: MainViewModel): DataFetchResult<OverviewData> {
    return withContext(context = Dispatchers.IO) {
        val url =
            "http://" + mainViewModel.getControllerAddress() + WebApiUrls.OVERVIEW_GET_DATA
        val reader = OverviewDataReader(url)
        reader.readOverviewData()
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
        mockController = mockControllerProfile,
        mockOverviewUiState = OverviewUiState.Success(createMockOverviewData())
    )
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
fun OverviewScreenPreviewError() {
    OverviewScreen(
        mockController = mockControllerProfile,
        mockOverviewUiState = OverviewUiState.Error("Error message")
    )
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
fun OverviewScreenPreviewLoading() {
    OverviewScreen(
        mockController = mockControllerProfile,
        mockOverviewUiState = OverviewUiState.Loading
    )
}