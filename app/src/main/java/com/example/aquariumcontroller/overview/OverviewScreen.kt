package com.example.aquariumcontroller.overview

import android.content.res.Configuration
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.aquariumcontroller.MainViewModel
import com.example.aquariumcontroller.R
import com.example.aquariumcontroller.ViewNavigationRoutes
import com.example.aquariumcontroller.controller.ControllerProfile
import com.example.aquariumcontroller.lib_composables.ScreenHeadlineComposable
import com.example.aquariumcontroller.maindropdownmenu.MainDropdownMenuComposable
import com.example.aquariumcontroller.ui.theme.AquariumControllerTheme
import com.example.aquariumcontroller.webapi.WebApiUrls
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun OverviewScreen(
    navHostController: NavHostController? = null,
    mockController: ControllerProfile? = null,
    mockOverviewData: OverviewData? = null,
    mainViewModel: MainViewModel? = null,
) {
    // This screen displays
    // - headline
    // - the overview data of the aquarium: Text for signal description and current value
    // - a button to update the overview data

    val configuration = LocalConfiguration.current

    val displayCircularProgressIndicatorInPreview: Boolean = when (configuration.orientation) {
        Configuration.ORIENTATION_LANDSCAPE -> {
            false
        }

        else -> {
            mockOverviewData == null // display circular progress indicator in preview for portrait mode
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
    val controllerPassword: String?
    val controllerPort: Int?
    var overviewData by remember { mutableStateOf(mockOverviewData) }

    if (mockController == null) {
        controllerProfileName = mainViewModel!!.getControllerName()
        controllerAddress = mainViewModel.getControllerAddress()
        controllerUsername = mainViewModel.getControllerUser()
        controllerPassword = mainViewModel.getControllerPassword()
        controllerPort = mainViewModel.getControllerPort()
        LaunchedEffect(Unit) {
            fetchOverviewData(
                mainViewModel
            ) { newOverviewData ->
                overviewData = newOverviewData as OverviewData
            } // Pass the setter lambda
        }
    } else {
        controllerProfileName = mockController.name
        controllerAddress = mockController.address
        controllerUsername = mockController.user
        controllerPassword = mockController.password
        controllerPort = mockController.port
    }

    // Use a LaunchedEffect to fetch data on initial composition
    LaunchedEffect(key1 = isControllerAddressValid) { // Key on controller address validity
        if (mainViewModel != null && isControllerAddressValid) { // Check for null and validity
            isRefreshing = true
            fetchOverviewData(
                mainViewModel,
            ) { newOverviewData ->
                overviewData = newOverviewData as OverviewData
            } // Pass the setter lambda
            isRefreshing = false
        }
    }

    val pullRefreshState = rememberPullRefreshState(
        refreshing = isRefreshing,
        onRefresh = {
            isRefreshing = true

            coroutineScope.launch {
                if (isControllerAddressValid) {
                    try {
                        fetchOverviewData(
                            mainViewModel!!
                        ) { newOverviewData ->
                            overviewData = newOverviewData as OverviewData
                        } // Pass the setter lambda
                        delay(timeMillis = 500)
                    } finally {
                        isRefreshing = false // Reset the refresh state
                    }
                }
            }
        }
    )

    AquariumControllerTheme {
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
                    Box(
                        modifier = Modifier
                            .pullRefresh(pullRefreshState)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            if (isRefreshing) {
                                CircularProgressIndicator(
                                    modifier = Modifier
                                        .size(150.dp),
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }

                            when (configuration.orientation) {
                                Configuration.ORIENTATION_LANDSCAPE -> {
                                    Row {
                                        Column(modifier = Modifier.weight(0.5f)) {
                                            OverviewControllerComposable(
                                                controllerProfileName = controllerProfileName!!,
                                                controllerAddress = controllerAddress!!,
                                                controllerPort = controllerPort!!,
                                                controllerUsername = controllerUsername!!,
                                                controllerPassword = controllerPassword!!
                                            )
                                            OverviewAmbientComposable(overviewData = overviewData!!)
                                        }
                                        Column(modifier = Modifier.weight(0.5f)) {
                                            OverviewTimestampComposable(overviewData = overviewData!!)
                                            OverviewWaterSignalsComposable(overviewData = overviewData!!)
                                            OverviewActuatorsComposable(overviewData = overviewData!!)
                                        }
                                    }
                                }

                                else -> {
                                    OverviewControllerComposable(
                                        controllerProfileName = controllerProfileName!!,
                                        controllerAddress = controllerAddress!!,
                                        controllerPort = controllerPort!!,
                                        controllerUsername = controllerUsername!!,
                                        controllerPassword = controllerPassword!!
                                    )
                                    OverviewTimestampComposable(overviewData = overviewData!!)
                                    OverviewWaterSignalsComposable(overviewData = overviewData!!)
                                    OverviewActuatorsComposable(overviewData = overviewData!!)
                                    OverviewAmbientComposable(overviewData = overviewData!!)
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
                                    coroutineScope.launch {
                                        if (isControllerAddressValid) {
                                            isRefreshing = true
                                            try {
                                                fetchOverviewData(
                                                    mainViewModel!!
                                                ) { newOverviewData ->
                                                    overviewData = newOverviewData as OverviewData
                                                } // Pass the setter lambda
                                                delay(500)
                                            } finally {
                                                isRefreshing = false // Reset the refresh state
                                            }
                                        }
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

private suspend fun fetchOverviewData(
    mainViewModel: MainViewModel,
    updateOverviewData: (Any) -> Unit,
) {
    withContext(Dispatchers.IO) {
        val url = "http://" + mainViewModel.getControllerAddress() + WebApiUrls.OVERVIEW_GET_DATA
        val reader = OverviewDataReader(url)
        val overviewData = reader.readOverviewData()
        if (overviewData != null) {
            updateOverviewData(overviewData) // Call the setter lambda
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
fun OverviewScreenPreview() {
    val mockControllerProfile = ControllerProfile(
        name = "Controller 1",
        address = "192.168.1.1",
        port = 80,
        user = "user",
        password = "password",
    )

    OverviewScreen(
        mockController = mockControllerProfile,
        mockOverviewData = createMockOverviewData()
    )
}