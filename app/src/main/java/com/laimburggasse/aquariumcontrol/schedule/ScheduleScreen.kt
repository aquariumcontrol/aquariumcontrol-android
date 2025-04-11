package com.laimburggasse.aquariumcontrol.schedule

import android.content.res.Configuration
import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.laimburggasse.aquariumcontrol.MainViewModel
import com.laimburggasse.aquariumcontrol.R
import com.laimburggasse.aquariumcontrol.ViewNavigationRoutes
import com.laimburggasse.aquariumcontrol.maindropdownmenu.MainDropdownMenuComposable
import com.laimburggasse.aquariumcontrol.lib_composables.ScreenHeadlineComposable
import com.laimburggasse.aquariumcontrol.ui.theme.AquariumControllerTheme
import com.laimburggasse.aquariumcontrol.webapi.WebApiUrls
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

    val configuration = LocalConfiguration.current

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
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Box {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    ScreenHeadlineComposable(
                        text = stringResource(R.string.actuator_schedule)
                    )
                    when (configuration.orientation) {
                        Configuration.ORIENTATION_LANDSCAPE -> {
                            Row {
                                ScheduleUpdateResetButtonsComposable(
                                    ballingScheduleSetValsViewModel = ballingScheduleSetValsViewModel,
                                    refillScheduleSetValsViewModel = refillScheduleSetValsViewModel,
                                    ventilationScheduleSetValsViewModel = ventilationScheduleSetValsViewModel,
                                    heatingScheduleSetValsViewModel = heatingScheduleSetValsViewModel,
                                    scheduleSetVals = scheduleSetVals,
                                    mainViewModel = mainViewModel
                                )
                            }
                        }
                        else -> {
                            ScheduleUpdateResetButtonsComposable(
                                ballingScheduleSetValsViewModel = ballingScheduleSetValsViewModel,
                                refillScheduleSetValsViewModel = refillScheduleSetValsViewModel,
                                ventilationScheduleSetValsViewModel = ventilationScheduleSetValsViewModel,
                                heatingScheduleSetValsViewModel = heatingScheduleSetValsViewModel,
                                scheduleSetVals = scheduleSetVals,
                                mainViewModel = mainViewModel
                            )
                        }
                    }
                    Scaffold { innerPadding ->
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(innerPadding)
                        ) {

                            Spacer(modifier = Modifier.size(width = 10.dp, height = 10.dp))
                            Column(
                                modifier = Modifier.verticalScroll(rememberScrollState())
                            ) {
                                ScheduleAdjustmentComposable(
                                    myViewModel = ballingScheduleSetValsViewModel,
                                    title = stringResource(R.string.balling_dosing_schedule)
                                )
                                ScheduleAdjustmentComposable(
                                    myViewModel = refillScheduleSetValsViewModel,
                                    title = stringResource(R.string.refill_schedule)
                                )
                                ScheduleAdjustmentComposable(
                                    myViewModel = ventilationScheduleSetValsViewModel,
                                    title = stringResource(R.string.ventilation_schedule)
                                )
                                ScheduleAdjustmentComposable(
                                    myViewModel = heatingScheduleSetValsViewModel,
                                    title = stringResource(R.string.heating_schedule)
                                )
                            }
                        }
                    }
                }
                MainDropdownMenuComposable(
                    navHostController = navHostController,
                    navigationRouteSource = ViewNavigationRoutes.schedule,
                    mainViewModel = mainViewModel
                )
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
            "http://" + mainViewModel.getControllerAddress() + WebApiUrls.ACTUATOR_SCHEDULE_GET_VALS

        val scheduleSetValsReader = ScheduleSetValsReader(url)
        val scheduleStringSetValsList = scheduleSetValsReader.readScheduleSetVals()

        if (!scheduleStringSetValsList.isNullOrEmpty()) {
            // Convert the list of TimeDataStringSample to a list of TimeDataSample
            val scheduleSetValsList =
                scheduleStringSetValsList.map { scheduleStringSetVal ->
                    scheduleStringSetVal.transformToSetVal()
                }
            val scheduleSetVals = ScheduleSetValsFactory(scheduleSetValsList).create()
            withContext(Dispatchers.Main) {
                updateScheduleSetVals(scheduleSetVals)
            }
        }
    }
}

@Preview(showBackground = true, heightDp = 1200, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(showBackground = true, heightDp = 1200, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    device = "spec:width=1200dp,height=950dp,orientation=landscape"
)
@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO,
    device = "spec:width=1200dp,height=950dp,orientation=landscape"
)
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