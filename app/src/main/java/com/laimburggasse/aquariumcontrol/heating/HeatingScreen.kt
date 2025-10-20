package com.laimburggasse.aquariumcontrol.heating

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
import com.laimburggasse.aquariumcontrol.MainViewModel
import com.laimburggasse.aquariumcontrol.MockMainViewModel
import com.laimburggasse.aquariumcontrol.R
import com.laimburggasse.aquariumcontrol.ViewNavigationRoutes
import com.laimburggasse.aquariumcontrol.lib_composables.ScreenHeadlineComposable
import com.laimburggasse.aquariumcontrol.maindropdownmenu.MainDropDownMenuNavigationEvent
import com.laimburggasse.aquariumcontrol.maindropdownmenu.MainDropdownMenuComposable
import com.laimburggasse.aquariumcontrol.ui.theme.AquariumControllerTheme
import com.laimburggasse.aquariumcontrol.webapi.DataFetchResult

@Composable
fun HeatingScreen(
    mainViewModel: MainViewModel,
    heatingSetValsViewModelFactory: HeatingSetValsViewModelFactory,
    heatingStatsViewModelFactory: HeatingStatsViewModelFactory,
    darkTheme: Boolean? = null,
    onNavigate: (MainDropDownMenuNavigationEvent) -> Unit
) {
    // This screen displays
    // - a button to update the heating controller values
    // - a button to reset the heating controller values
    // - a range slider for setting the control target temperatures
    // - numeric input fields for entering the control target temperatures
    // - the heating statistics incl. refresh button

    val heatingSetValsViewModel: HeatingSetValsViewModel = viewModel(factory = heatingSetValsViewModelFactory)
    val heatingStatsViewModel: HeatingStatsViewModel = viewModel(factory = heatingStatsViewModelFactory)

    val heatingSetValsUiState: HeatingSetValsUiState by heatingSetValsViewModel.heatingSetValsUiState.collectAsState()
    val heatingStatsUiState: HeatingStatsUiState by heatingStatsViewModel.heatingStatsUiState.collectAsState()

    val configuration: Configuration = LocalConfiguration.current

    AquariumControllerTheme(darkTheme) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Box {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    ScreenHeadlineComposable(
                        text = stringResource(id = R.string.heating_control)
                    )
                    Scaffold(
                        topBar = { },
                        bottomBar = { },
                        floatingActionButton = {},
                        snackbarHost = { SnackbarHost(hostState = remember { SnackbarHostState() })}
                    ) { innerPadding ->
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(paddingValues = innerPadding)
                        ) {
                            when (heatingSetValsUiState) {
                                is HeatingSetValsUiState.Success -> {
                                    val heatingSetVals: HeatingSetVals = (heatingSetValsUiState as HeatingSetValsUiState.Success).heatingSetVals
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        when (configuration.orientation) {
                                            Configuration.ORIENTATION_LANDSCAPE -> {
                                                Row {
                                                    HeatingButtonsComposable(
                                                        onUpdateClicked = { heatingSetValsViewModel.updateValues() },
                                                        onResetClicked = { heatingSetValsViewModel.resetValues() },                                                    )
                                                }
                                                Row {
                                                    HeatingRangeSliderTextFieldsLandscapeComposable(
                                                        heatingSwitchOnTemp = heatingSetVals.heatingSwitchOnTemp,
                                                        heatingSwitchOffTemp = heatingSetVals.heatingSwitchOffTemp,
                                                        onLowerValueChanged = { newValue -> heatingSetValsViewModel.onLowerValueChanged(value = newValue.toFloat()) },
                                                        onUpperValueChanged = { newValue -> heatingSetValsViewModel.onUpperValueChanged(value = newValue.toFloat()) },
                                                        onSliderValueChanged = { newLowerValue, newUpperValue -> heatingSetValsViewModel.onRangeChangedBySlider(newLowerValue, newUpperValue) }
                                                    )
                                                }
                                            }

                                            else -> {
                                                HeatingButtonsComposable(
                                                    onUpdateClicked = { heatingSetValsViewModel.updateValues() },
                                                    onResetClicked = { heatingSetValsViewModel.resetValues() },
                                                )
                                                Spacer(modifier = Modifier.size(width = 10.dp,height = 10.dp))
                                                HeatingRangeSliderTextFieldsPortraitComposable(
                                                    heatingSwitchOnTemp = heatingSetVals.heatingSwitchOnTemp,
                                                    heatingSwitchOffTemp = heatingSetVals.heatingSwitchOffTemp,
                                                    onLowerValueChanged = { newValue -> heatingSetValsViewModel.onLowerValueChanged(value = newValue.toFloat()) },
                                                    onUpperValueChanged = { newValue -> heatingSetValsViewModel.onUpperValueChanged(value = newValue.toFloat()) },
                                                    onSliderValueChanged = { newLowerValue, newUpperValue -> heatingSetValsViewModel.onRangeChangedBySlider(newLowerValue, newUpperValue) },
                                                )
                                            }
                                        }
                                    }
                                }
                                is HeatingSetValsUiState.Error -> {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.Center
                                    ) {
                                        Column(
                                            verticalArrangement = Arrangement.Center,
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Text("Error loading set value data: ${(heatingSetValsUiState as HeatingSetValsUiState.Error).errorMessage}")
                                        }
                                    }
                                }
                                is HeatingSetValsUiState.Loading -> {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.Center
                                    ) {
                                        Column(
                                            verticalArrangement = Arrangement.Center,
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Text("Loading set value data...")
                                        }
                                    }
                                }
                                is HeatingSetValsUiState.Empty -> {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.Center
                                    ) {
                                        Column(
                                            modifier = Modifier.fillMaxSize().padding(16.dp),
                                            verticalArrangement = Arrangement.Center,
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Text("No set values configured - check server configuration.")
                                        }
                                    }
                                }
                            }

                            when (heatingStatsUiState) {
                                is HeatingStatsUiState.Error -> {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.Center
                                    ) {
                                        Column(
                                            modifier = Modifier.fillMaxSize().padding(16.dp),
                                            verticalArrangement = Arrangement.Center,
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Text("Error loading statistical data: ${(heatingStatsUiState as HeatingStatsUiState.Error).errorMessage}")
                                        }
                                    }
                                }
                                HeatingStatsUiState.Loading -> {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.Center
                                    ) {
                                        Column(
                                            modifier = Modifier.fillMaxSize().padding(16.dp),
                                            verticalArrangement = Arrangement.Center,
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Text("Loading statistical data...")
                                        }
                                    }
                                }
                                is HeatingStatsUiState.Success -> {
                                    val heatingStats: List<HeatingStatsEntry> = (heatingStatsUiState as HeatingStatsUiState.Success).heatingStats
                                    HeatingStatsComposable(
                                        heatingStats = heatingStats,
                                        updateData = { heatingStatsViewModel.loadData() }
                                    )
                                }
                                is HeatingStatsUiState.Empty -> {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.Center
                                    ) {
                                        Column(
                                            modifier = Modifier.fillMaxSize().padding(16.dp),
                                            verticalArrangement = Arrangement.Center,
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Text("No heating stats available.")
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                MainDropdownMenuComposable(
                    navigationRouteSource = ViewNavigationRoutes.heating,
                    mainViewModel = mainViewModel,
                    onNavigate
                )
            }
        }
    }
}


fun createMockStatsData(): List<HeatingStatsEntry> {
    val mockHeatingStatsStringEntries = listOf(
        HeatingStatsStringEntry(
            dateString = "2025-1-12",
            energy = 1988.17,
            ambientTemperatureAverage = 22.6869,
            waterTemperatureAverage = 25.4176,
            heatingControlRuntime = 36235
        ),
        HeatingStatsStringEntry(
            dateString = "2025-1-25",
            energy = 18.7222,
            ambientTemperatureAverage = 18.9717,
            waterTemperatureAverage = 25.4007,
            heatingControlRuntime = 389
        )
    )

    return mockHeatingStatsStringEntries.map {
        it.transformToHeatingStatsEntry()
    }
}

val mockHeatingSetValsDataFetchResult = DataFetchResult.Success(
    data = HeatingSetVals(
        heatingSwitchOffTemp = 23.5,
        heatingSwitchOnTemp = 27.5
    )
)

val mockHeatingSetValsRepositorySuccess = MockHeatingSetValsRepository(
    mockHeatingSetValsDataFetchResult
)

val mockHeatingSetValsRepositoryError = MockHeatingSetValsRepository(
    mockHeatingSetValsDataFetchResult = DataFetchResult.Error(
        exception = null, errorMessage = "Error loading data"
    )
)

val mockHeatingSetValsViewModelFactorySuccess = HeatingSetValsViewModelFactory(
    heatingSetValsRepository = mockHeatingSetValsRepositorySuccess
)

val mockHeatingSetValsViewModelFactoryError = HeatingSetValsViewModelFactory(
    heatingSetValsRepository = mockHeatingSetValsRepositoryError
)

val mockHeatingStatsDataFetchResultSuccess = DataFetchResult.Success(data = createMockStatsData())
val mockHeatingStatsRepositorySuccess = MockHeatingStatsRepository(
    mockHeatingStatsDataFetchResult = mockHeatingStatsDataFetchResultSuccess
)
val mockHeatingStatsViewModelFactorySuccess = HeatingStatsViewModelFactory(
    heatingStatsRepository = mockHeatingStatsRepositorySuccess
)
val mockHeatingStatsDataFetchResultError = DataFetchResult.Error(exception = null, errorMessage = "Error message")
val mockHeatingStatsRepositoryError = MockHeatingStatsRepository(
    mockHeatingStatsDataFetchResult = mockHeatingStatsDataFetchResultError
)
val mockHeatingStatsViewModelFactoryError = HeatingStatsViewModelFactory(
    heatingStatsRepository = mockHeatingStatsRepositoryError
)

val mockMainViewModel = MockMainViewModel()

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
fun HeatingScreenPreviewSuccess() {
    HeatingScreen(
        mainViewModel = mockMainViewModel,
        heatingSetValsViewModelFactory = mockHeatingSetValsViewModelFactorySuccess,
        heatingStatsViewModelFactory = mockHeatingStatsViewModelFactorySuccess,
        onNavigate = {}
    )
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
fun HeatingScreenPreviewError() {
    HeatingScreen(
        mainViewModel = mockMainViewModel,
        heatingSetValsViewModelFactory = mockHeatingSetValsViewModelFactoryError,
        heatingStatsViewModelFactory = mockHeatingStatsViewModelFactoryError,
        onNavigate = {}
    )
}
