package com.example.aquariumcontroller.heating

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.aquariumcontroller.ApiUrls
import com.example.aquariumcontroller.MainDropdownMenuComposable
import com.example.aquariumcontroller.MainViewModel
import com.example.aquariumcontroller.NumericDoubleOutlinedTextFieldComposable
import com.example.aquariumcontroller.R
import com.example.aquariumcontroller.ViewNavigationRoutes
import com.example.aquariumcontroller.ui.theme.AquariumControllerTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun HeatingScreen(
    navHostController: NavHostController? = null,
    mockHeatingSetVals: HeatingSetVals? = null,
    mockHeatingStatEntries: List<HeatingStatsEntry>? = null,
    mainViewModel: MainViewModel? = null
) {
    // This screen displays
    // - a range slider for setting the control target temperatures
    // - the heating statistics
    val heatingSetVals = mockHeatingSetVals ?: HeatingSetVals(
        initialSwitchOffTemp = 28.0, initialSwitchOnTemp = 22.0
    )
    var isRefreshing by remember {
        mutableStateOf(
            mockHeatingStatEntries?.isNotEmpty() ?: false
        )
    }

    val isControllerAddressValid: Boolean = !mainViewModel?.getControllerAddress().isNullOrBlank()
    val coroutineScope = rememberCoroutineScope()

    var heatingStats by remember { mutableStateOf(mockHeatingStatEntries ?: emptyList()) }

    val heatingSetValsViewModel: HeatingSetValsViewModel = viewModel(
        factory = HeatingSetValsViewModelFactory(heatingSetVals)
    )

    // Use derived state for slider position
    val sliderRange by heatingSetValsViewModel.sliderRange.observeAsState(
        initial =
        heatingSetVals.heatingSwitchOnTemp.toFloat()..
                heatingSetVals.heatingSwitchOffTemp.toFloat()
    )

    // Use a separate mutable state for the text field values
    val lowerBoundValue by heatingSetValsViewModel.lowerBoundValue.observeAsState(
        initial = heatingSetVals.heatingSwitchOnTemp
    )
    val upperBoundValue by heatingSetValsViewModel.upperBoundValue.observeAsState(
        heatingSetVals.heatingSwitchOffTemp
    )

    @Suppress("UNCHECKED_CAST")
    LaunchedEffect(key1 = isControllerAddressValid) { // Key on controller address validity
        if (mainViewModel != null && isControllerAddressValid) { // Check for null and validity
            fetchHeatingStats(
                mainViewModel,
            ) { newHeatingStats ->
                heatingStats = newHeatingStats as List<HeatingStatsEntry>
            } // Pass the setter lambda
        }
    }

    AquariumControllerTheme {
        // A surface container using the 'background' color from the theme
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Scaffold() { innerPadding ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .padding(24.dp)
                        .padding(innerPadding)
                ) {
                    MainDropdownMenuComposable(
                        navHostController = navHostController,
                        navigationRouteSource = ViewNavigationRoutes.heating,
                        mainViewModel = mainViewModel
                    )
                    Text(
                        text = stringResource(id = R.string.HeatingControl),
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier.padding(16.dp)
                    )
                    Button(
                        onClick = { heatingSetValsViewModel.updateValues() }
                    ) {
                        Text(text = "Update Heating Controller Values")
                    }
                    Button(
                        onClick = { heatingSetValsViewModel.resetValues() }
                    ) {
                        Text(text = "Reset Heating Controller Values")
                    }
                    Spacer(modifier = Modifier.size(width = 10.dp, height = 10.dp))
                    Column {
                        RangeSlider(
                            value = sliderRange,
                            onValueChange = { newValue ->
                                heatingSetValsViewModel.onRangeChangedBySlider(
                                    newValue.start,
                                    newValue.endInclusive
                                )
                            },
                            valueRange = 20f..30f,
                            modifier = Modifier
                                .fillMaxWidth()

                        )
                    }
                    Spacer(modifier = Modifier.size(width = 10.dp, height = 10.dp))
                    Row {
                        NumericDoubleOutlinedTextFieldComposable(
                            value = lowerBoundValue,
                            onValueChange = { newStringValue ->
                                if (newStringValue.isNotEmpty() && newStringValue.isNotBlank()) {
                                    val newNumericValue = newStringValue.toFloat()
                                    heatingSetValsViewModel.onLowerValueChanged(newNumericValue)
                                }
                            },
                            label = "Switch on at",
                            modifier = Modifier
                                .padding(top = 16.dp)
                                .size(width = 128.dp, height = 72.dp)
                        )
                        Spacer(modifier = Modifier.size(width = 72.dp, height = 10.dp))
                        NumericDoubleOutlinedTextFieldComposable(
                            value = upperBoundValue,
                            onValueChange = { newStringValue ->
                                if (newStringValue.isNotEmpty() && newStringValue.isNotBlank()) {
                                    val newNumericValue = newStringValue.toFloat()
                                    heatingSetValsViewModel.onUpperValueChanged(newNumericValue)
                                }
                            },
                            label = "Switch off at",
                            modifier = Modifier
                                .padding(top = 16.dp)
                                .size(width = 128.dp, height = 72.dp)
                        )
                    }
                    if (isRefreshing) {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .size(150.dp),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    @Suppress("UNCHECKED_CAST")
                    HeatingStatsComposable(heatingStats = heatingStats, updateData =
                        {
                            coroutineScope.launch {
                                if (isControllerAddressValid) {
                                    isRefreshing = true
                                    try {
                                        fetchHeatingStats(
                                            mainViewModel!!
                                        ) { newHeatingStats ->
                                            heatingStats = newHeatingStats as List<HeatingStatsEntry>
                                        } // Pass the setter lambda
                                        delay(500)
                                    } finally {
                                        isRefreshing = false // Reset the refresh state
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

private suspend fun fetchHeatingStats(
    mainViewModel: MainViewModel,
    updateHeatingStats: (Any) -> Unit,
) {
    withContext(Dispatchers.IO) {
        val url =
            "http://" + mainViewModel.getControllerAddress() + ApiUrls.HEATING_GET_STATS
        val heatingStringsStats = HeatingStatsReader(url).readStatsEntry()
        val heatingStats =
            heatingStringsStats?.map { it.transformToHeatingStatsEntry() } ?: emptyList()
        updateHeatingStats(heatingStats)
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

@Preview(showBackground = true, heightDp = 800, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun HeatingViewPreview() {
    AquariumControllerTheme {
        HeatingScreen(
            mockHeatingSetVals = HeatingSetVals(
                initialSwitchOffTemp = 27.5,
                initialSwitchOnTemp = 22.5
            ),
            mockHeatingStatEntries = createMockStatsData()
        )
    }
}