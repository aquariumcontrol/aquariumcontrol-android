package com.example.aquariumcontroller.schedule

import android.content.res.Configuration
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.aquariumcontroller.ApiUrls
import com.example.aquariumcontroller.MainDropdownMenuComposable
import com.example.aquariumcontroller.MainViewModel
import com.example.aquariumcontroller.PostRequest
import com.example.aquariumcontroller.R
import com.example.aquariumcontroller.ViewNavigationRoutes
import com.example.aquariumcontroller.ui.theme.AquariumControllerTheme
import com.example.aquariumcontroller.webApiPostRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun ScheduleScreen(
    navHostController: NavHostController? = null,
    mockScheduleSetVals: ScheduleSetVals? = null,
    mainViewModel: MainViewModel? = null
) {
    // This screen displays for each of (Balling, Refill, schedule, Heating)
    // - a range slider for setting the start and stop time

    var scheduleSetVals = mockScheduleSetVals ?: ScheduleSetVals(
        ballingStartTime = 0F, ballingStopTime = 24F, ballingLimitationActive = false,
        refillStartTime = 0F, refillStopTime = 24F, refillLimitationActive = false,
        ventilationStartTime = 0F, ventilationStopTime = 24F, ventilationLimitationActive = false,
        heatingStartTime = 0F, heatingStopTime = 24F, heatingLimitationActive = false
    )

    val isControllerAddressValid: Boolean = !mainViewModel?.getControllerAddress().isNullOrBlank()

    val coroutineScope = rememberCoroutineScope()

    val postRequest = PostRequest()

    val context = LocalContext.current // context for web API request and Toasts

    val ballingScheduleSetValsViewModel: ScheduleSetValsViewModel = viewModel(
        factory = ScheduleSetValsViewModelFactory(
            setValStartTime = scheduleSetVals.ballingStartTime,
            setValStopTime = scheduleSetVals.ballingStopTime,
            setValLimitationActive = scheduleSetVals.ballingLimitationActive
        ),
        key = ScheduleType.BALLING.key // Unique key for Balling ViewModel
    )

    val refillScheduleSetValsViewModel: ScheduleSetValsViewModel = viewModel(
        factory = ScheduleSetValsViewModelFactory(
            setValStartTime = scheduleSetVals.refillStartTime,
            setValStopTime = scheduleSetVals.refillStopTime,
            setValLimitationActive = scheduleSetVals.refillLimitationActive
        ),
        key = ScheduleType.REFILL.key // Unique key for Refill
    )

    val ventilationScheduleSetValsViewModel: ScheduleSetValsViewModel = viewModel(
        factory = ScheduleSetValsViewModelFactory(
            setValStartTime = scheduleSetVals.ventilationStartTime,
            setValStopTime = scheduleSetVals.ventilationStopTime,
            setValLimitationActive = scheduleSetVals.ventilationLimitationActive
        ),
        key = ScheduleType.VENTILATION.key // Unique key for Ventilation
    )

    val heatingScheduleSetValsViewModel: ScheduleSetValsViewModel = viewModel(
        factory = ScheduleSetValsViewModelFactory(
            setValStartTime = scheduleSetVals.heatingStartTime,
            setValStopTime = scheduleSetVals.heatingStopTime,
            setValLimitationActive = scheduleSetVals.heatingLimitationActive
        ),
        key = ScheduleType.HEATING.key // Unique key for Heating
    )

    LaunchedEffect(key1 = isControllerAddressValid) { // Key on controller address validity
        if (mainViewModel != null && isControllerAddressValid) { // Check for null and validity
            fetchScheduleSetVals(
                mainViewModel,
            ) { newScheduleSetVals ->
                val newScheduleSetValsCasted = newScheduleSetVals as ScheduleSetVals
                scheduleSetVals = newScheduleSetValsCasted
                Log.d("scheduleScreen", "New schedule set vals: $newScheduleSetValsCasted")
                ballingScheduleSetValsViewModel.setValues(
                    newSetValStartTime = newScheduleSetValsCasted.ballingStartTime,
                    newSetValStopTime = newScheduleSetValsCasted.ballingStopTime,
                    newSetValLimitationActive = newScheduleSetValsCasted.ballingLimitationActive,
                )
                refillScheduleSetValsViewModel.setValues(
                    newSetValStartTime = newScheduleSetValsCasted.refillStartTime,
                    newSetValStopTime = newScheduleSetValsCasted.refillStopTime,
                    newSetValLimitationActive = newScheduleSetValsCasted.refillLimitationActive
                )
                ventilationScheduleSetValsViewModel.setValues(
                    newSetValStartTime = newScheduleSetValsCasted.ventilationStartTime,
                    newSetValStopTime = newScheduleSetValsCasted.ventilationStopTime,
                    newSetValLimitationActive = newScheduleSetValsCasted.ventilationLimitationActive
                )
                heatingScheduleSetValsViewModel.setValues(
                    newSetValStartTime = newScheduleSetValsCasted.heatingStartTime,
                    newSetValStopTime = newScheduleSetValsCasted.heatingStopTime,
                    newSetValLimitationActive = newScheduleSetValsCasted.heatingLimitationActive
                )
            }
        }
    }

    AquariumControllerTheme {
        // A surface container using the 'background' color from the theme
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Scaffold { innerPadding ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .padding(24.dp)
                        .padding(innerPadding)
                ) {
                    MainDropdownMenuComposable(
                        navHostController = navHostController,
                        navigationRouteSource = ViewNavigationRoutes.schedule,
                        mainViewModel = mainViewModel
                    )
                    Text(
                        text = stringResource(R.string.actuatorSchedule),
                        style = MaterialTheme.typography.headlineMedium,
                        modifier = Modifier.padding(8.dp)
                    )
                    Button(
                        onClick = {
                            ballingScheduleSetValsViewModel.updateValues()
                            refillScheduleSetValsViewModel.updateValues()
                            ventilationScheduleSetValsViewModel.updateValues()
                            heatingScheduleSetValsViewModel.updateValues()
                            scheduleSetVals.updateFromViewModels(
                                ballingScheduleSetValsViewModel,
                                refillScheduleSetValsViewModel,
                                ventilationScheduleSetValsViewModel,
                                heatingScheduleSetValsViewModel
                            )
                            if (mainViewModel != null) {
                                webApiPostRequest(
                                    mainViewModel = mainViewModel,
                                    apiUrl = ApiUrls.ACTUATOR_SCHEDULE_POST_UPDATE,
                                    coroutineScope = coroutineScope,
                                    postRequest = postRequest,
                                    context = context,
                                    optionalGetParams = scheduleSetVals.getApiParams(),
                                )
                                // Show the Toast after the API call
                                Toast.makeText(
                                    context,
                                    "Sent request to controller for updating schedule set values",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    ) {
                        Text(text = "Update actuator schedules")
                    }
                    Button(
                        onClick = {
                            ballingScheduleSetValsViewModel.resetValues()
                            refillScheduleSetValsViewModel.resetValues()
                            ventilationScheduleSetValsViewModel.resetValues()
                            heatingScheduleSetValsViewModel.resetValues()
                        }
                    ) {
                        Text(text = "Reset actuator schedules")
                    }
                    Spacer(modifier = Modifier.size(width = 10.dp, height = 10.dp))
                    Column(
                        modifier = Modifier.verticalScroll(rememberScrollState())
                    ) {
                        ScheduleAdjustmentComposable(
                            myViewModel = ballingScheduleSetValsViewModel,
                            title = "Balling dosing schedule"
                        )
                        ScheduleAdjustmentComposable(
                            myViewModel = refillScheduleSetValsViewModel,
                            title = "Refill schedule"
                        )
                        ScheduleAdjustmentComposable(
                            myViewModel = ventilationScheduleSetValsViewModel,
                            title = "Ventilation schedule"
                        )
                        ScheduleAdjustmentComposable(
                            myViewModel = heatingScheduleSetValsViewModel,
                            title = "Heating schedule"
                        )
                    }
                }
            }
        }
    }
}

private suspend fun fetchScheduleSetVals(
    mainViewModel: MainViewModel,
    updateScheduleSetVals: (Any) -> Unit,
) {
    withContext(Dispatchers.IO) {
        val url =
            "http://" + mainViewModel.getControllerAddress() + ApiUrls.ACTUATOR_SCHEDULE_GET_VALS

        val scheduleSetValsReader = ScheduleSetValsReader(url)
        val scheduleStringSetValsList = scheduleSetValsReader.readScheduleSetVals()

        if (!scheduleStringSetValsList.isNullOrEmpty()) {
            Log.d("scheduleScreen", "Fetched schedule string set vals: $scheduleStringSetValsList")
            // Convert the list of TimeDataStringSample to a list of TimeDataSample
            val scheduleSetValsList =
                scheduleStringSetValsList.map { scheduleStringSetVal ->
                    scheduleStringSetVal.transformToSetVal()
                }
            val scheduleSetVals = ScheduleSetValsFactory(scheduleSetValsList).create()
            withContext(Dispatchers.Main) {
                updateScheduleSetVals(scheduleSetVals)
            }
        } else {
            Log.d("scheduleScreen", "No schedule set vals fetched")
        }
    }
}

@Preview(showBackground = true, heightDp = 1300, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun ScheduleScreenPreview() {
    ScheduleScreen(
        mockScheduleSetVals = ScheduleSetVals(
            ballingStartTime = 2F,
            ballingStopTime = 22F,
            ballingLimitationActive = false,
            refillStartTime = 3F,
            refillStopTime = 21F,
            refillLimitationActive = true,
            ventilationStartTime = 4F,
            ventilationStopTime = 20F,
            ventilationLimitationActive = false,
            heatingStartTime = 5F,
            heatingStopTime = 19F,
            heatingLimitationActive = true,
        )
    )
}