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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.laimburggasse.aquariumcontrol.MainViewModel
import com.laimburggasse.aquariumcontrol.MockMainViewModel
import com.laimburggasse.aquariumcontrol.R
import com.laimburggasse.aquariumcontrol.ViewNavigationRoutes
import com.laimburggasse.aquariumcontrol.lib_composables.ScreenHeadlineComposable
import com.laimburggasse.aquariumcontrol.maindropdownmenu.MainDropdownMenuComposable
import com.laimburggasse.aquariumcontrol.ui.theme.AquariumControllerTheme
import com.laimburggasse.aquariumcontrol.webapi.DataFetchResult

@Composable
fun ScheduleScreen(
    navHostController: NavHostController? = null,
    mainViewModel: MainViewModel,
    scheduleViewModelFactory: ScheduleViewModelFactory,
    darkTheme: Boolean? = null,
) {
    // This screen displays for each of (Balling, Refill, schedule, Heating)
    // - a range slider for setting the start and stop time
    val scheduleViewModel: ScheduleViewModel = viewModel(factory = scheduleViewModelFactory)

    val configuration: Configuration = LocalConfiguration.current

    val scheduleUiState: ScheduleUiState by scheduleViewModel.scheduleUiState.collectAsState()

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
                            val scheduleEntries: ScheduleEntries = (scheduleUiState as ScheduleUiState.Success).scheduleEntries
                            when (configuration.orientation) {
                                Configuration.ORIENTATION_LANDSCAPE -> {
                                    Row {
                                        ScheduleUpdateResetButtonsComposable(
                                            scheduleEntries = scheduleEntries,
                                            scheduleViewModel = scheduleViewModel
                                        )
                                    }
                                }

                                else -> {
                                    ScheduleUpdateResetButtonsComposable(
                                        scheduleViewModel = scheduleViewModel,
                                        scheduleEntries = scheduleEntries
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
                                                // --- Balling Adjustment ---
                                                ScheduleAdjustmentComposable(
                                                    title = "Balling",
                                                    startTime = scheduleEntries.ballingStartTime,
                                                    stopTime = scheduleEntries.ballingStopTime,
                                                    isLimitationActive = scheduleEntries.ballingLimitationActive,
                                                    onStartTimeChanged = { newTime ->
                                                        scheduleViewModel.onTimeChange(ScheduleType.BALLING, TimeType.START, newTime)
                                                    },
                                                    onStopTimeChanged = { newTime ->
                                                        scheduleViewModel.onTimeChange(ScheduleType.BALLING, TimeType.STOP, newTime)
                                                    },
                                                    onLimitationActiveChanged = { isActive ->
                                                        scheduleViewModel.onLimitationActiveChange(ScheduleType.BALLING, isActive)
                                                    }
                                                )

                                                // --- Refill Adjustment ---
                                                ScheduleAdjustmentComposable(
                                                    title = "Refill",
                                                    startTime = scheduleEntries.refillStartTime,
                                                    stopTime = scheduleEntries.refillStopTime,
                                                    isLimitationActive = scheduleEntries.refillLimitationActive,
                                                    onStartTimeChanged = { newTime ->
                                                        scheduleViewModel.onTimeChange(ScheduleType.REFILL, TimeType.START, newTime)
                                                    },
                                                    onStopTimeChanged = { newTime ->
                                                        scheduleViewModel.onTimeChange(ScheduleType.REFILL, TimeType.STOP, newTime)
                                                    },
                                                    onLimitationActiveChanged = { isActive ->
                                                        scheduleViewModel.onLimitationActiveChange(ScheduleType.REFILL, isActive)
                                                    }
                                                )

                                                // --- Ventilation Adjustment ---
                                                ScheduleAdjustmentComposable(
                                                    title = "Ventilation",
                                                    startTime = scheduleEntries.ventilationStartTime,
                                                    stopTime = scheduleEntries.ventilationStopTime,
                                                    isLimitationActive = scheduleEntries.ventilationLimitationActive,
                                                    onStartTimeChanged = { newTime ->
                                                        scheduleViewModel.onTimeChange(ScheduleType.VENTILATION, TimeType.START, newTime)
                                                    },
                                                    onStopTimeChanged = { newTime ->
                                                        scheduleViewModel.onTimeChange(ScheduleType.VENTILATION, TimeType.STOP, newTime)
                                                    },
                                                    onLimitationActiveChanged = { isActive ->
                                                        scheduleViewModel.onLimitationActiveChange(ScheduleType.VENTILATION, isActive)
                                                    }
                                                )

                                                // --- Ventilation Adjustment ---
                                                ScheduleAdjustmentComposable(
                                                    title = "Heating",
                                                    startTime = scheduleEntries.heatingStartTime,
                                                    stopTime = scheduleEntries.heatingStopTime,
                                                    isLimitationActive = scheduleEntries.heatingLimitationActive,
                                                    onStartTimeChanged = { newTime ->
                                                        scheduleViewModel.onTimeChange(ScheduleType.HEATING, TimeType.START, newTime)
                                                    },
                                                    onStopTimeChanged = { newTime ->
                                                        scheduleViewModel.onTimeChange(ScheduleType.HEATING, TimeType.STOP, newTime)
                                                    },
                                                    onLimitationActiveChanged = { isActive ->
                                                        scheduleViewModel.onLimitationActiveChange(ScheduleType.HEATING, isActive)
                                                    }
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
                        ScheduleUiState.Empty -> {
                            Column(
                                modifier = Modifier.fillMaxSize().padding(16.dp),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text("No schedule configured - check server configuration.")
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

val mockScheduleEntries = ScheduleEntries(
    ballingStartTime = 2F,
    ballingStopTime = 22F,
    ballingLimitationActive = false,
    ballingScheduleLoaded = true,
    refillStartTime = 3F,
    refillStopTime = 21F,
    refillLimitationActive = true,
    refillScheduleLoaded = true,
    ventilationStartTime = 4F,
    ventilationStopTime = 20F,
    ventilationLimitationActive = false,
    ventilationScheduleLoaded = true,
    heatingStartTime = 5F,
    heatingStopTime = 19F,
    heatingLimitationActive = true,
    heatingScheduleLoaded = true
)

val mockScheduleRepositorySuccess = MockScheduleRepository(
    mockScheduleDataFetchResult = DataFetchResult.Success(data = mockScheduleEntries)
)

val mockScheduleViewModelFactorySuccess = ScheduleViewModelFactory(
    scheduleRepository = mockScheduleRepositorySuccess
)

val mockScheduleRepositoryError = MockScheduleRepository(
    mockScheduleDataFetchResult = DataFetchResult.Error(
        exception = null,
        errorMessage = "Error loading data",
    )
)

val mockScheduleViewModelFactoryError = ScheduleViewModelFactory(
    scheduleRepository = mockScheduleRepositoryError
)

val mockMainViewModel = MockMainViewModel()

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
    ScheduleScreen(
        mainViewModel = mockMainViewModel,
        scheduleViewModelFactory = mockScheduleViewModelFactorySuccess
    )
}

@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO,
    device = "spec:width=1200dp,height=950dp,orientation=landscape"
)
@Composable
internal fun ScheduleScreenPreviewError() {
    ScheduleScreen(
        mainViewModel = mockMainViewModel,
        scheduleViewModelFactory = mockScheduleViewModelFactoryError
    )
}