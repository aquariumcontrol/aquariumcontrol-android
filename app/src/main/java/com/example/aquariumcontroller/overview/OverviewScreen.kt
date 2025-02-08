package com.example.aquariumcontroller.overview

import android.content.res.Configuration
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.aquariumcontroller.ApiUrls
import com.example.aquariumcontroller.MainDropdownMenuComposable
import com.example.aquariumcontroller.MainViewModel
import com.example.aquariumcontroller.R
import com.example.aquariumcontroller.ViewNavigationRoutes
import com.example.aquariumcontroller.controller.ControllerProfile
import com.example.aquariumcontroller.ui.theme.AquariumControllerTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterialApi::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun OverviewScreen(
    navHostController: NavHostController? = null,
    mockController: ControllerProfile? = null,
    mockOverviewData: OverviewData? = null,
    mainViewModel: MainViewModel? = null,
) {
    // This screen displays the overview data of the aquarium.
    var isRefreshing by remember {
        mutableStateOf(
            mockOverviewData != null
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
            Log.d("OverviewScreen", "onRefresh called")
            isRefreshing = true

            coroutineScope.launch {
                if (isControllerAddressValid) {
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
        }
    )

    AquariumControllerTheme {
        // A surface container using the 'background' color from the theme
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {

            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                MainDropdownMenuComposable(
                    navHostController = navHostController,
                    navigationRouteSource = ViewNavigationRoutes.overView,
                    mainViewModel = mainViewModel
                )
                Text(
                    text = stringResource(id = R.string.Overview),
                    style = MaterialTheme.typography.headlineLarge,
                    modifier = Modifier
                        .padding(16.dp)
                        .align(Alignment.CenterHorizontally)
                )
                Box(
                    modifier = Modifier
                        .pullRefresh(pullRefreshState)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        modifier = Modifier
                            .padding(16.dp)
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
                        Row(Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.weight(1f)) {
                                Row(modifier = Modifier.align(Alignment.Start)) {
                                    Text(text = "Controller Name")
                                }
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Row(modifier = Modifier.align(Alignment.End)) {
                                    Text(text = controllerProfileName!!)
                                }
                            }
                        }
                        Row(Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.weight(1f)) {
                                Row(modifier = Modifier.align(Alignment.Start)) {
                                    Text(text = "Controller Address")
                                }
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Row(modifier = Modifier.align(Alignment.End)) {
                                    Text(text = controllerAddress!!)
                                }
                            }
                        }
                        Row(Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.weight(1f)) {
                                Row(modifier = Modifier.align(Alignment.Start)) {
                                    Text(text = "Controller Port")
                                }
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Row(modifier = Modifier.align(Alignment.End)) {
                                    Text(text = controllerPort!!.toString())
                                }
                            }
                        }
                        Row(Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.weight(1f)) {
                                Row(modifier = Modifier.align(Alignment.Start)) {
                                    Text(text = "User name")
                                }
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Row(modifier = Modifier.align(Alignment.End)) {
                                    Text(text = controllerUsername!!)
                                }
                            }
                        }
                        Row(Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.weight(1f)) {
                                Row(modifier = Modifier.align(Alignment.Start)) {
                                    Text(text = "Password")
                                }
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Row(modifier = Modifier.align(Alignment.End)) {
                                    Text(text = controllerPassword!!)
                                }
                            }
                        }
                        Row(Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.weight(1f)) {
                                Row(modifier = Modifier.align(Alignment.Start)) {
                                    Text(text = "Timestamp")
                                }
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Row(modifier = Modifier.align(Alignment.End)) {
                                    Text(text = "${overviewData?.timestamp}")
                                }
                            }
                        }
                        Row(Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.weight(1f)) {
                                Row(modifier = Modifier.align(Alignment.Start)) {
                                    Text(text = "Water temperature")
                                }
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Row(modifier = Modifier.align(Alignment.End)) {
                                    Text(text = "${overviewData?.temperatureWater} °C")
                                }
                            }
                        }
                        Row(Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.weight(1f)) {
                                Row(modifier = Modifier.align(Alignment.Start)) {
                                    Text(text = "Water temperature filtered")
                                }
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Row(modifier = Modifier.align(Alignment.End)) {
                                    Text(text = "${overviewData?.temperatureWaterFiltered} °C")
                                }
                            }
                        }
                        Row(Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.weight(1f)) {
                                Row(modifier = Modifier.align(Alignment.Start)) {
                                    Text(text = "pH")
                                }
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Row(modifier = Modifier.align(Alignment.End)) {
                                    Text(text = "${overviewData?.pH}")
                                }
                            }
                        }
                        Row(Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.weight(1f)) {
                                Row(modifier = Modifier.align(Alignment.Start)) {
                                    Text(text = "pH filtered")
                                }
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Row(modifier = Modifier.align(Alignment.End)) {
                                    Text(text = "${overviewData?.pHFiltered} °C")
                                }
                            }
                        }
                        Row(Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.weight(1f)) {
                                Row(modifier = Modifier.align(Alignment.Start)) {
                                    Text(text = "Conductivity")
                                }
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Row(modifier = Modifier.align(Alignment.End)) {
                                    Text(text = "${overviewData?.conductivity} mS/cm")
                                }
                            }
                        }
                        Row(Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.weight(1f)) {
                                Row(modifier = Modifier.align(Alignment.Start)) {
                                    Text(text = "Conductivity filtered")
                                }
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Row(modifier = Modifier.align(Alignment.End)) {
                                    Text(text = "${overviewData?.conductivityFiltered} mS/cm")
                                }
                            }
                        }
                        Row(Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.weight(1f)) {
                                Row(modifier = Modifier.align(Alignment.Start)) {
                                    Text(text = "Tank level switch position")
                                }
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Row(modifier = Modifier.align(Alignment.End)) {
                                    Text(text = "${overviewData?.tankLevelSwitchPosition}")
                                }
                            }
                        }
                        Row(Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.weight(1f)) {
                                Row(modifier = Modifier.align(Alignment.Start)) {
                                    Text(text = "Ventilation")
                                }
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Row(modifier = Modifier.align(Alignment.End)) {
                                    Text(text = "${overviewData?.surfaceVentilation}")
                                }
                            }
                        }
                        Row(Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.weight(1f)) {
                                Row(modifier = Modifier.align(Alignment.Start)) {
                                    Text(text = "Heating")
                                }
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Row(modifier = Modifier.align(Alignment.End)) {
                                    Text(text = "${overviewData?.heater}")
                                }
                            }
                        }
                        Row(Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.weight(1f)) {
                                Row(modifier = Modifier.align(Alignment.Start)) {
                                    Text(text = "Ambient temperature")
                                }
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Row(modifier = Modifier.align(Alignment.End)) {
                                    Text(text = "${overviewData?.temperatureAmbient} °C")
                                }
                            }
                        }
                        Row(Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.weight(1f)) {
                                Row(modifier = Modifier.align(Alignment.Start)) {
                                    Text(text = "Ambient humidity")
                                }
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Row(modifier = Modifier.align(Alignment.End)) {
                                    Text(text = "${overviewData?.humidityAmbient} %")
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
                                text = stringResource(id = R.string.UpdateOverviewData),
                                fontSize = 16.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
private suspend fun fetchOverviewData(
    mainViewModel: MainViewModel,
    updateOverviewData: (Any) -> Unit,
) {
    withContext(Dispatchers.IO) {
        val url = "http://" + mainViewModel.getControllerAddress() + ApiUrls.OVERVIEW_GET_DATA
        val reader = OverviewDataReader(url)
        val overviewData = reader.readOverviewData()
        if (overviewData != null) {
            updateOverviewData(overviewData) // Call the setter lambda
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true, heightDp = 1000, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun OverviewViewPreview() {
    val mockControllerProfile = ControllerProfile(
        name = "Controller 1",
        address = "192.168.1.1",
        port = 80,
        user = "user",
        password = "password",
        isDefault = true
    )

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

    AquariumControllerTheme {
        OverviewScreen(mockController = mockControllerProfile, mockOverviewData = mockOverviewData)
    }
}