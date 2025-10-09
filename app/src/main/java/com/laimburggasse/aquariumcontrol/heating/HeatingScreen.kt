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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun HeatingScreen(
    navHostController: NavHostController? = null,
    mainViewModel: MainViewModel,
    heatingSetValsViewModel: HeatingSetValsViewModelInterface = viewModel<HeatingSetValsViewModel>(),
    heatingStatsViewModel: HeatingStatsViewModelInterface = viewModel<HeatingStatsViewModel>(),
    darkTheme: Boolean? = null
) {
    // This screen displays
    // - a button to update the heating controller values
    // - a button to reset the heating controller values
    // - a range slider for setting the control target temperatures
    // - numeric input fields for entering the control target temperatures
    // - the heating statistics incl. refresh button

    val isControllerAddressValid: Boolean = !mainViewModel.getControllerAddress().isNullOrBlank()

    var heatingSetValsUiState: HeatingSetValsUiState by remember { heatingSetValsViewModel::heatingSetValsUiState }
    var heatingStatsUiState: HeatingStatsUiState by remember { heatingStatsViewModel::heatingStatsUiState }

    val coroutineScope: CoroutineScope = rememberCoroutineScope()

    val configuration: Configuration = LocalConfiguration.current

    val heatingInitialSetVals = HeatingSetVals(
        heatingSwitchOffTemp = 28.0,
        heatingSwitchOnTemp = 22.0
    )

    @Suppress("UNCHECKED_CAST")
    LaunchedEffect(key1 = isControllerAddressValid) { // Key on controller address validity
        if (isControllerAddressValid) { // Check for null and validity
            heatingSetValsViewModel.loadData(mainViewModel = mainViewModel)
            heatingStatsViewModel.loadData(mainViewModel = mainViewModel)
        }
    }

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
                        snackbarHost = { SnackbarHost(remember { SnackbarHostState() })}
                    ) { innerPadding ->
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .padding(innerPadding)
                        ) {
                            when (heatingSetValsUiState) {
                                is HeatingSetValsUiState.Success -> {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally) {
                                        when (configuration.orientation) {
                                            Configuration.ORIENTATION_LANDSCAPE -> {
                                                Row {
                                                    HeatingButtonsComposable(
                                                        heatingSetValsViewModel = heatingSetValsViewModel,
                                                        mainViewModel = mainViewModel,
                                                    )
                                                }
                                                Row {
                                                    HeatingRangeSliderTextFieldsLandscapeComposable(
                                                        heatingSetValsViewModel = heatingSetValsViewModel,
                                                        heatingInitialSetVals = heatingInitialSetVals
                                                    )
                                                }
                                            }

                                            else -> {
                                                HeatingButtonsComposable(
                                                    heatingSetValsViewModel = heatingSetValsViewModel,
                                                    mainViewModel = mainViewModel,
                                                )
                                                Spacer(
                                                    modifier = Modifier.size(
                                                        width = 10.dp,
                                                        height = 10.dp
                                                    )
                                                )
                                                HeatingRangeSliderTextFieldsPortraitComposable(
                                                    heatingSetValsViewModel = heatingSetValsViewModel,
                                                    heatingInitialSetVals = heatingInitialSetVals
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
                                    val heatingStats = (heatingStatsUiState as HeatingStatsUiState.Success).heatingStats
                                    HeatingStatsComposable(heatingStats = heatingStats, updateData =
                                        {
                                            // @todo: replace this code with message passing
                                            coroutineScope.launch {
                                                if (isControllerAddressValid) {
                                                    try {
                                                        heatingStatsUiState = HeatingStatsUiState.Loading
                                                        // @ todo: update with view model
                                                    } finally {
                                                        // do nothing
                                                    }
                                                }
                                            }
                                        }
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
                    navHostController = navHostController,
                    navigationRouteSource = ViewNavigationRoutes.heating,
                    mainViewModel = mainViewModel
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
    val mockHeatingSetValsUiState = HeatingSetValsUiState.Success(
        HeatingSetVals(
            heatingSwitchOffTemp = 27.5,
            heatingSwitchOnTemp = 22.5
        )
    )
    val mockHeatingSetValsViewModel = MockHeatingSetValsViewModel(mockHeatingSetValsUiState)
    val mockHeatingStatsUiState = HeatingStatsUiState.Success(heatingStats = createMockStatsData())
    val mockHeatingStatsViewModel = MockHeatingStatsViewModel(mockHeatingStatsUiState)

    HeatingScreen(
        mainViewModel = mockMainViewModel,
        heatingStatsViewModel = mockHeatingStatsViewModel,
        heatingSetValsViewModel = mockHeatingSetValsViewModel
    )
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
fun HeatingScreenPreviewError() {
    val mockHeatingSetValsViewModel = MockHeatingSetValsViewModel(
        heatingSetValsUiState = HeatingSetValsUiState.Error(errorMessage = "Error message"),
    )
    val mockHeatingStatsViewModel = MockHeatingStatsViewModel(
        heatingStatsUiState = HeatingStatsUiState.Error(errorMessage = "Error message")
    )
    HeatingScreen(
        mainViewModel = mockMainViewModel,
        heatingStatsViewModel = mockHeatingStatsViewModel,
        heatingSetValsViewModel = mockHeatingSetValsViewModel
    )
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
fun HeatingScreenPreviewLoading() {
    val mockHeatingSetValsViewModel = MockHeatingSetValsViewModel(
        heatingSetValsUiState = HeatingSetValsUiState.Loading,
    )
    val mockHeatingStatsViewModel = MockHeatingStatsViewModel(
        heatingStatsUiState = HeatingStatsUiState.Loading
    )
    HeatingScreen(
        mainViewModel = mockMainViewModel,
        heatingStatsViewModel = mockHeatingStatsViewModel,
        heatingSetValsViewModel = mockHeatingSetValsViewModel
    )
}
