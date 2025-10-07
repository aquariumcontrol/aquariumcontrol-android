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

@Composable
fun ScheduleScreen(
    navHostController: NavHostController? = null,
    mainViewModel: MainViewModel,
    scheduleViewModel: ScheduleViewModelInterface = viewModel<ScheduleViewModel>(),
    darkTheme: Boolean? = null,
) {
    // This screen displays for each of (Balling, Refill, schedule, Heating)
    // - a range slider for setting the start and stop time

    val isControllerAddressValid: Boolean = !mainViewModel.getControllerAddress().isNullOrBlank()

    val configuration = LocalConfiguration.current

    val ballingScheduleEntryViewModel: ScheduleEntryViewModel = viewModel(
        factory = ScheduleEntryViewModelFactory(
            setValStartTime = 0F,
            setValStopTime = 24F,
            setValLimitationActive = false
        ),
        key = ScheduleType.BALLING.key // Unique key for Balling ViewModel
    )

    val refillScheduleEntryViewModel: ScheduleEntryViewModel = viewModel(
        factory = ScheduleEntryViewModelFactory(
            setValStartTime = 0F,
            setValStopTime = 24F,
            setValLimitationActive = false
        ),
        key = ScheduleType.REFILL.key // Unique key for Refill
    )

    val ventilationScheduleEntryViewModel: ScheduleEntryViewModel = viewModel(
        factory = ScheduleEntryViewModelFactory(
            setValStartTime = 0F,
            setValStopTime = 24F,
            setValLimitationActive = false
        ),
        key = ScheduleType.VENTILATION.key // Unique key for Ventilation
    )

    val heatingScheduleEntryViewModel: ScheduleEntryViewModel = viewModel(
        factory = ScheduleEntryViewModelFactory(
            setValStartTime = 0F,
            setValStopTime = 24F,
            setValLimitationActive = false
        ),
        key = ScheduleType.HEATING.key // Unique key for Heating
    )

    val scheduleUiState: ScheduleUiState by remember { scheduleViewModel::scheduleUiState }

    LaunchedEffect(key1 = isControllerAddressValid) { // Key on controller address validity
        if (isControllerAddressValid) { // Check for null and validity
            scheduleViewModel.loadData(
                mainViewModel = mainViewModel,
                ballingScheduleEntryViewModel = ballingScheduleEntryViewModel,
                refillScheduleEntryViewModel = refillScheduleEntryViewModel,
                ventilationScheduleEntryViewModel = ventilationScheduleEntryViewModel,
                heatingScheduleEntryViewModel = heatingScheduleEntryViewModel
            )
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
                            val scheduleEntries: ScheduleEntries = (scheduleUiState as ScheduleUiState.Success).scheduleEntries
                            when (configuration.orientation) {
                                Configuration.ORIENTATION_LANDSCAPE -> {
                                    Row {
                                        ScheduleUpdateResetButtonsComposable(
                                            ballingScheduleEntryViewModel = ballingScheduleEntryViewModel,
                                            refillScheduleEntryViewModel = refillScheduleEntryViewModel,
                                            ventilationScheduleEntryViewModel = ventilationScheduleEntryViewModel,
                                            heatingScheduleEntryViewModel = heatingScheduleEntryViewModel,
                                            scheduleEntries = scheduleEntries,
                                            scheduleViewModel = scheduleViewModel,
                                            mainViewModel = mainViewModel
                                        )
                                    }
                                }

                                else -> {
                                    ScheduleUpdateResetButtonsComposable(
                                        ballingScheduleEntryViewModel = ballingScheduleEntryViewModel,
                                        refillScheduleEntryViewModel = refillScheduleEntryViewModel,
                                        ventilationScheduleEntryViewModel = ventilationScheduleEntryViewModel,
                                        heatingScheduleEntryViewModel = heatingScheduleEntryViewModel,
                                        scheduleViewModel = scheduleViewModel,
                                        scheduleEntries = scheduleEntries,
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
                                                    myViewModel = ballingScheduleEntryViewModel,
                                                    title = stringResource(R.string.balling_dosing_schedule)
                                                )
                                                ScheduleAdjustmentComposable(
                                                    myViewModel = refillScheduleEntryViewModel,
                                                    title = stringResource(R.string.refill_schedule)
                                                )
                                                ScheduleAdjustmentComposable(
                                                    myViewModel = ventilationScheduleEntryViewModel,
                                                    title = stringResource(R.string.ventilation_schedule)
                                                )
                                                ScheduleAdjustmentComposable(
                                                    myViewModel = heatingScheduleEntryViewModel,
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

val mockScheduleUiStateSuccess = ScheduleUiState.Success(
    scheduleEntries = mockScheduleEntries
)

val mockScheduleViewModelSuccess = MockScheduleViewModel(scheduleUiState = mockScheduleUiStateSuccess)

val mockScheduleUiStateError = ScheduleUiState.Error(errorMessage = "Error message")

val mockScheduleViewModelError = MockScheduleViewModel(scheduleUiState = mockScheduleUiStateError)

val mockScheduleUiStateLoading = ScheduleUiState.Loading

val mockScheduleViewModelLoading = MockScheduleViewModel(scheduleUiState = mockScheduleUiStateLoading)

val mockScheduleUiStateEmpty = ScheduleUiState.Empty

val mockScheduleViewModelEmpty = MockScheduleViewModel(scheduleUiState = mockScheduleUiStateEmpty)

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
        scheduleViewModel = mockScheduleViewModelSuccess,
        mainViewModel = mockMainViewModel,
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
        scheduleViewModel = mockScheduleViewModelError,
        mainViewModel = mockMainViewModel,
    )
}

@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO,
    device = "spec:width=1200dp,height=950dp,orientation=landscape"
)
@Composable
internal fun ScheduleScreenPreviewLoading() {
    ScheduleScreen(
        scheduleViewModel = mockScheduleViewModelLoading,
        mainViewModel = mockMainViewModel,
    )
}

@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO,
    device = "spec:width=1200dp,height=950dp,orientation=landscape"
)
@Composable
internal fun ScheduleScreenPreviewEmpty() {
    ScheduleScreen(
        scheduleViewModel = mockScheduleViewModelEmpty,
        mainViewModel = mockMainViewModel,
    )
}