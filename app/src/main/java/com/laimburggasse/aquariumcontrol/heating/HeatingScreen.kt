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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun HeatingScreen(
    navHostController: NavHostController? = null,
    mockHeatingSetValueUiState: HeatingSetValueUiState? = null,
    mockHeatingStatsUiState: HeatingStatsUiState? = null,
    mainViewModel: MainViewModel? = null,
    darkTheme: Boolean? = null
) {
    // This screen displays
    // - a button to update the heating controller values
    // - a button to reset the heating controller values
    // - a range slider for setting the control target temperatures
    // - numeric input fields for entering the control target temperatures
    // - the heating statistics incl. refresh button

    val isControllerAddressValid: Boolean = !mainViewModel?.getControllerAddress().isNullOrBlank()

    var heatingSetValueUiState by remember { mutableStateOf(mockHeatingSetValueUiState ?: HeatingSetValueUiState.Loading) }

    val coroutineScope = rememberCoroutineScope()

    val configuration = LocalConfiguration.current

    val heatingInitialSetVals = HeatingSetVals(
        heatingSwitchOffTemp = 28.0,
        heatingSwitchOnTemp = 22.0
    )

    var heatingStatsUiState by remember { mutableStateOf(mockHeatingStatsUiState ?: HeatingStatsUiState.Loading) }

    val heatingSetValsViewModel: HeatingSetValsViewModel = viewModel(
        factory = HeatingSetValsViewModelFactory(heatingInitialSetVals)
    )
    
    fun loadData() {
        coroutineScope.launch {
            if (mainViewModel != null) {
                fetchHeatingSetVals(
                    mainViewModel,
                ) { newHeatingUiState ->
                    heatingSetValueUiState = newHeatingUiState
                    when (newHeatingUiState) {
                        is HeatingSetValueUiState.Success -> {
                            // Update the view model with the data
                            val newHeatingSetVals = newHeatingUiState.heatingSetVals
                            heatingSetValsViewModel.setValues(newHeatingSetVals)
                        }
                        is HeatingSetValueUiState.Error -> {
                            // Do nothing
                        }
                        HeatingSetValueUiState.Loading -> {
                            // Do nothing
                        }
                    }
                }
            }
        }
    }
    @Suppress("UNCHECKED_CAST")
    LaunchedEffect(key1 = isControllerAddressValid) { // Key on controller address validity
        if (mainViewModel != null && isControllerAddressValid) { // Check for null and validity
            fetchHeatingStats(
                mainViewModel,
            ) { newHeatingStatsUiState ->
                heatingStatsUiState = newHeatingStatsUiState
            }
            loadData()
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
                            when (heatingSetValueUiState) {
                                is HeatingSetValueUiState.Success -> {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally) {
                                        when (configuration.orientation) {
                                            Configuration.ORIENTATION_LANDSCAPE -> {
                                                Row {
                                                    HeatingButtonsComposable(
                                                        heatingSetValsViewModel = heatingSetValsViewModel,
                                                        mainViewModel = mainViewModel,
                                                        coroutineScope = coroutineScope
                                                    )
                                                }
                                                Row {
                                                    HeatingRangeSliderTextFieldsLandscapeComposable(
                                                        myHeatingSetValsViewModel = heatingSetValsViewModel,
                                                        heatingInitialSetVals = heatingInitialSetVals
                                                    )
                                                }
                                            }

                                            else -> {
                                                HeatingButtonsComposable(
                                                    heatingSetValsViewModel = heatingSetValsViewModel,
                                                    mainViewModel = mainViewModel,
                                                    coroutineScope = coroutineScope
                                                )
                                                Spacer(
                                                    modifier = Modifier.size(
                                                        width = 10.dp,
                                                        height = 10.dp
                                                    )
                                                )
                                                HeatingRangeSliderTextFieldsPortraitComposable(
                                                    myHeatingSetValsViewModel = heatingSetValsViewModel,
                                                    heatingInitialSetVals = heatingInitialSetVals
                                                )
                                            }
                                        }
                                    }
                                }
                                is HeatingSetValueUiState.Error -> {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.Center
                                    ) {
                                        Text("Error loading set value data: ${(heatingSetValueUiState as HeatingSetValueUiState.Error).errorMessage}")
                                    }
                                }
                                is HeatingSetValueUiState.Loading -> {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.Center
                                    ) {
                                        Text("Loading set value data...")
                                    }
                                }
                            }

                            when (heatingStatsUiState) {
                                is HeatingStatsUiState.Error -> {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.Center
                                    ) {
                                        Text("Error loading statistical data: ${(heatingStatsUiState as HeatingStatsUiState.Error).errorMessage}")
                                    }
                                }
                                HeatingStatsUiState.Loading -> {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.Center
                                    ) {
                                        Text("Loading statistical data...")
                                    }
                                }
                                is HeatingStatsUiState.Success -> {
                                    val heatingStats = (heatingStatsUiState as HeatingStatsUiState.Success).heatingStats
                                    HeatingStatsComposable(heatingStats = heatingStats, updateData =
                                        {
                                            coroutineScope.launch {
                                                if (isControllerAddressValid) {
                                                    try {
                                                        heatingStatsUiState = HeatingStatsUiState.Loading
                                                        delay(500)
                                                        fetchHeatingStats(
                                                            mainViewModel
                                                        ) { newHeatingStatsUiState ->
                                                            heatingStatsUiState = newHeatingStatsUiState
                                                        }
                                                    } finally {
                                                        // do nothing
                                                    }
                                                }
                                            }
                                        }
                                    )
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

private suspend fun fetchHeatingStats(
    mainViewModel: MainViewModel,
    updateHeatingStats: (HeatingStatsUiState) -> Unit,
) {
    withContext(Dispatchers.IO) {
        val url =
            "http://" + mainViewModel.getControllerAddress() + WebApiUrls.HEATING_GET_STATS
        val heatingStatsReader = HeatingStatsReader(url)
        when (val result = heatingStatsReader.readStatsEntry()) {
            is DataFetchResult.Success -> {
                val heatingStringsStats = result.data
                val heatingStats = heatingStringsStats.map { it.transformToHeatingStatsEntry() }
                withContext(context = Dispatchers.Main) {
                    updateHeatingStats(HeatingStatsUiState.Success(heatingStats))
                }
            }
            is DataFetchResult.Error -> {
                withContext(context = Dispatchers.Main) {
                    updateHeatingStats(HeatingStatsUiState.Error(result.errorMessage))
                }
            }
        }
    }
}

private suspend fun fetchHeatingSetVals(
    mainViewModel: MainViewModel,
    updateHeatingSetVals: (HeatingSetValueUiState) -> Unit,
) {
    withContext(Dispatchers.IO) {
        val url =
            "http://" + mainViewModel.getControllerAddress() + WebApiUrls.HEATING_GET_SET_VALS
        val heatingSetValsReader = HeatingSetValsReader(url)
        when (val result = heatingSetValsReader.readHeatingSetVals()) {
            is DataFetchResult.Success -> {
                val heatingSetValsList = result.data
                withContext(context = Dispatchers.Main) {
                    updateHeatingSetVals(HeatingSetValueUiState.Success(heatingSetValsList.first()))
                }
            }
            is DataFetchResult.Error -> {
                withContext(context = Dispatchers.Main) {
                    updateHeatingSetVals(HeatingSetValueUiState.Error(result.errorMessage))
                }
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
    val mockHeatingSetValueUiState = HeatingSetValueUiState.Success(
        HeatingSetVals(
            heatingSwitchOffTemp = 27.5,
            heatingSwitchOnTemp = 22.5
        )
    )
    val mockHeatingStatsUiState = HeatingStatsUiState.Success(createMockStatsData())
    HeatingScreen(
        mockHeatingSetValueUiState = mockHeatingSetValueUiState,
        mockHeatingStatsUiState = mockHeatingStatsUiState
    )
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
fun HeatingScreenPreviewError() {
    HeatingScreen(
        mockHeatingSetValueUiState = HeatingSetValueUiState.Error(errorMessage = "Error message"),
        mockHeatingStatsUiState = HeatingStatsUiState.Error(errorMessage = "Error message")
    )
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
fun HeatingScreenPreviewLoading() {
    HeatingScreen(
        mockHeatingSetValueUiState = HeatingSetValueUiState.Loading,
        mockHeatingStatsUiState = HeatingStatsUiState.Loading
    )
}
