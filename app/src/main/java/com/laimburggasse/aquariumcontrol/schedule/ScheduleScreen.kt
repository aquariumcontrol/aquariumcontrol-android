package com.laimburggasse.aquariumcontrol.schedule

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.laimburggasse.aquariumcontrol.MainViewModel
import com.laimburggasse.aquariumcontrol.R
import com.laimburggasse.aquariumcontrol.ViewNavigationRoutes
import com.laimburggasse.aquariumcontrol.lib_composables.ScreenHeadlineComposable
import com.laimburggasse.aquariumcontrol.maindropdownmenu.MainDropdownMenuComposable
import com.laimburggasse.aquariumcontrol.ui.theme.AquariumControllerTheme
import com.laimburggasse.aquariumcontrol.webapi.DataFetchResult
import com.laimburggasse.aquariumcontrol.webapi.WebApiUrls
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun ScheduleScreen(
    navHostController: NavHostController? = null,
    mockScheduleUiState: ScheduleUiState? = null,
    mainViewModel: MainViewModel? = null,
    darkTheme: Boolean? = null
) {
    // This screen displays for each of (Balling, Refill, schedule, Heating)
    // - a range slider for setting the start and stop time

    /*
    var scheduleSetVals = mockScheduleSetVals ?: ScheduleSetVals(
        ballingStartTime = 0F, ballingStopTime = 24F, ballingLimitationActive = false,
        refillStartTime = 0F, refillStopTime = 24F, refillLimitationActive = false,
        ventilationStartTime = 0F, ventilationStopTime = 24F, ventilationLimitationActive = false,
        heatingStartTime = 0F, heatingStopTime = 24F, heatingLimitationActive = false
    )
    */

    val isControllerAddressValid: Boolean = !mainViewModel?.getControllerAddress().isNullOrBlank()

    var scheduleUiState by remember { mutableStateOf(mockScheduleUiState ?: ScheduleUiState.Loading) }

    val coroutineScope = rememberCoroutineScope()

    val configuration = LocalConfiguration.current

    val ballingScheduleSetValsViewModel: ScheduleSetValsViewModel = viewModel(
        factory = ScheduleSetValsViewModelFactory(
            setValStartTime = 0F,
            setValStopTime = 24F,
            setValLimitationActive = false
        ),
        key = ScheduleType.BALLING.key // Unique key for Balling ViewModel
    )

    val refillScheduleSetValsViewModel: ScheduleSetValsViewModel = viewModel(
        factory = ScheduleSetValsViewModelFactory(
            setValStartTime = 0F,
            setValStopTime = 24F,
            setValLimitationActive = false
        ),
        key = ScheduleType.REFILL.key // Unique key for Refill
    )

    val ventilationScheduleSetValsViewModel: ScheduleSetValsViewModel = viewModel(
        factory = ScheduleSetValsViewModelFactory(
            setValStartTime = 0F,
            setValStopTime = 24F,
            setValLimitationActive = false
        ),
        key = ScheduleType.VENTILATION.key // Unique key for Ventilation
    )

    val heatingScheduleSetValsViewModel: ScheduleSetValsViewModel = viewModel(
        factory = ScheduleSetValsViewModelFactory(
            setValStartTime = 0F,
            setValStopTime = 24F,
            setValLimitationActive = false
        ),
        key = ScheduleType.HEATING.key // Unique key for Heating
    )

    fun loadData() {
        coroutineScope.launch {
            if (mockScheduleUiState != null) { // For preview and testing
                scheduleUiState = mockScheduleUiState
            } else {
                if (mainViewModel != null) {
                    fetchScheduleSetVals(
                        mainViewModel,
                    ) { newScheduleSetUiState ->
                        scheduleUiState = newScheduleSetUiState
                        when (newScheduleSetUiState) {
                            is ScheduleUiState.Success -> {
                                // Update the view models with the data
                                val scheduleSetVals = newScheduleSetUiState.scheduleSetVals

                                ballingScheduleSetValsViewModel.setValues(
                                    newSetValStartTime = scheduleSetVals.ballingStartTime,
                                    newSetValStopTime = scheduleSetVals.ballingStopTime,
                                    newSetValLimitationActive = scheduleSetVals.ballingLimitationActive,
                                )
                                refillScheduleSetValsViewModel.setValues(
                                    newSetValStartTime = scheduleSetVals.refillStartTime,
                                    newSetValStopTime = scheduleSetVals.refillStopTime,
                                    newSetValLimitationActive = scheduleSetVals.refillLimitationActive
                                )
                                ventilationScheduleSetValsViewModel.setValues(
                                    newSetValStartTime = scheduleSetVals.ventilationStartTime,
                                    newSetValStopTime = scheduleSetVals.ventilationStopTime,
                                    newSetValLimitationActive = scheduleSetVals.ventilationLimitationActive
                                )
                                heatingScheduleSetValsViewModel.setValues(
                                    newSetValStartTime = scheduleSetVals.heatingStartTime,
                                    newSetValStopTime = scheduleSetVals.heatingStopTime,
                                    newSetValLimitationActive = scheduleSetVals.heatingLimitationActive
                                )
                            }
                            is ScheduleUiState.Error -> {
                                // Do nothing
                            }
                            ScheduleUiState.Loading -> {
                                // Do nothing
                            }
                        }
                    }
                }
            }
        }
    }

    LaunchedEffect(key1 = isControllerAddressValid) { // Key on controller address validity
        if (mainViewModel != null && isControllerAddressValid) { // Check for null and validity
            loadData()
        }
    }

    AquariumControllerTheme(darkTheme) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Box {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    ScreenHeadlineComposable(
                        text = stringResource(R.string.actuator_schedule),
                    )
                    when (scheduleUiState) {
                        is ScheduleUiState.Success -> {
                            val scheduleSetVals = (scheduleUiState as ScheduleUiState.Success).scheduleSetVals
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
                                    Scaffold (
                                    topBar = { },
                            bottomBar = { },
                            floatingActionButton = {},
                            snackbarHost = { SnackbarHost(remember { SnackbarHostState() }) }
                                ) { innerPadding ->
                                        Column(
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            modifier = Modifier.padding(innerPadding)
                                        ) {

                                            Spacer(
                                                modifier = Modifier.size(
                                                    width = 10.dp,
                                                    height = 10.dp
                                                )
                                            )
                                            Column(
                                                modifier = Modifier.verticalScroll(
                                                    rememberScrollState()
                                                )
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
                        is ScheduleUiState.Error -> {
                            Column(
                                modifier = Modifier.fillMaxSize().padding(16.dp),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text("Error loading data: ${(scheduleUiState as ScheduleUiState.Error).errorMessage}")
                            }
                        }
                        ScheduleUiState.Loading -> {
                            Column(
                                modifier = Modifier.fillMaxSize().padding(16.dp),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text("Loading...")
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
    updateScheduleUiState: (ScheduleUiState) -> Unit,
) {
    updateScheduleUiState(ScheduleUiState.Loading)
    withContext(Dispatchers.IO) {
        val url =
            "http://" + mainViewModel.getControllerAddress() + WebApiUrls.ACTUATOR_SCHEDULE_GET_VALS

        val scheduleSetValsReader = ScheduleSetValsReader(url)
        when (val result = scheduleSetValsReader.readScheduleSetVals()) {
            is DataFetchResult.Success -> {
                val scheduleStringSetValsList = result.data
                if (!scheduleStringSetValsList.isNotEmpty()) {
                    // Convert the list of TimeDataStringSample to a list of TimeDataSample
                    val scheduleSetValsList =
                        scheduleStringSetValsList.map { scheduleStringSetVal ->
                            scheduleStringSetVal.transformToSetVal()
                        }
                    val scheduleSetVals = ScheduleSetValsFactory(scheduleSetValsList).create()
                    withContext(Dispatchers.Main) {
                        updateScheduleUiState(ScheduleUiState.Success(scheduleSetVals))
                    }
                }
                else {
                    withContext(context = Dispatchers.Main) {
                        updateScheduleUiState(ScheduleUiState.Error("No data available."))
                    }
                }
            }
            is DataFetchResult.Error -> {
                withContext(context = Dispatchers.Main) {
                    updateScheduleUiState(ScheduleUiState.Error(result.errorMessage))
                }
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
internal fun ScheduleScreenPreviewSuccess() {
    val mockScheduleUiState = ScheduleUiState.Success(
        ScheduleSetVals(
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
    ScheduleScreen(
        mockScheduleUiState = mockScheduleUiState
    )
}

@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO,
    device = "spec:width=1200dp,height=950dp,orientation=landscape"
)
@Composable
internal fun ScheduleScreenPreviewError() {
    val mockScheduleUiState = ScheduleUiState.Error("Error message")
    ScheduleScreen(
        mockScheduleUiState = mockScheduleUiState
    )
}

@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO,
    device = "spec:width=1200dp,height=950dp,orientation=landscape"
)
@Composable
internal fun ScheduleScreenPreviewLoading() {
    val mockScheduleUiState = ScheduleUiState.Loading
    ScheduleScreen(
        mockScheduleUiState = mockScheduleUiState
    )
}