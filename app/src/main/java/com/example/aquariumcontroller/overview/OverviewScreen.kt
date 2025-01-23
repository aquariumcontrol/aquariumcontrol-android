package com.example.aquariumcontroller.overview

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
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
import androidx.navigation.NavHostController
import com.example.aquariumcontroller.ApiUrls
import com.example.aquariumcontroller.MainDropdownMenuScreen
import com.example.aquariumcontroller.MainViewModel
import com.example.aquariumcontroller.R
import com.example.aquariumcontroller.ViewNavigationRoutes
import com.example.aquariumcontroller.controller.ControllerProfile
import com.example.aquariumcontroller.ui.theme.AquariumControllerTheme
import kotlinx.coroutines.Dispatchers
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

    val coroutineScope = rememberCoroutineScope()

    val controllerProfileName: String?
    val controllerAddress: String?
    val controllerUsername: String?
    val controllerPassword: String?
    val controllerPort: Int?
    val url: String?
    var overviewData by remember { mutableStateOf(mockOverviewData) }
    var refreshing by remember { mutableStateOf(false) }

    if (mockController == null) {
        controllerProfileName = mainViewModel!!.getControllerName()
        controllerAddress = mainViewModel.getControllerAddress()
        controllerUsername = mainViewModel.getControllerUser()
        controllerPassword = mainViewModel.getControllerPassword()
        controllerPort = mainViewModel.getControllerPort()
        url = "http://" + mainViewModel.getControllerAddress() + ApiUrls.OVERVIEW_GET_DATA
        val overviewDataReader = OverviewDataReader(url)
        LaunchedEffect(Unit) {
            withContext(Dispatchers.IO) {
                overviewData = overviewDataReader.readOverviewData()
            }
        }
    } else {
        controllerProfileName = mockController.name
        controllerAddress = mockController.address
        controllerUsername = mockController.user
        controllerPassword = mockController.password
        controllerPort = mockController.port
    }

    // This will be called when the user pulls down the list
    fun refresh() = coroutineScope.launch {
        refreshing = true
        Log.d(this::class.simpleName, "Refreshing")
        /*
        val overviewDataReader = OverviewDataReader(url).also {
            overviewData = it.readOverviewData()
        }*/
        refreshing = false
    }

    val state = rememberPullRefreshState(refreshing, ::refresh)

    AquariumControllerTheme {
        // A surface container using the 'background' color from the theme
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Box(Modifier.pullRefresh(state)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    MainDropdownMenuScreen(
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
                    Row(Modifier
                        .fillMaxWidth()
                        .height(16.dp)) {}
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
                }
            }
            PullRefreshIndicator(refreshing, state)
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
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