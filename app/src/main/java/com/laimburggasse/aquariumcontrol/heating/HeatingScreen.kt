package com.laimburggasse.aquariumcontrol.heating

import android.content.res.Configuration
import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
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
import com.laimburggasse.aquariumcontrol.webapi.WebApiUrls
import com.laimburggasse.aquariumcontrol.maindropdownmenu.MainDropdownMenuComposable
import com.laimburggasse.aquariumcontrol.MainViewModel
import com.laimburggasse.aquariumcontrol.R
import com.laimburggasse.aquariumcontrol.ViewNavigationRoutes
import com.laimburggasse.aquariumcontrol.lib_composables.ScreenHeadlineComposable
import com.laimburggasse.aquariumcontrol.ui.theme.AquariumControllerTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun HeatingScreen(
    navHostController: NavHostController? = null,
    mockHeatingSetVals: HeatingSetVals? = null,
    mockHeatingStatEntries: List<HeatingStatsEntry>? = null,
    mainViewModel: MainViewModel? = null
) {
    // This screen displays
    // - a button to update the heating controller values
    // - a button to reset the heating controller values
    // - a range slider for setting the control target temperatures
    // - numeric input fields for entering the control target temperatures
    // - the heating statistics incl. refresh button


    val heatingInitialSetVals = mockHeatingSetVals ?: HeatingSetVals(
        heatingSwitchOffTemp = 28.0, heatingSwitchOnTemp = 22.0
    )

    val configuration = LocalConfiguration.current

    val displayCircularProgressIndicatorInPreview: Boolean = when (configuration.orientation) {
        Configuration.ORIENTATION_LANDSCAPE -> {
            false
        }
        else -> {
            mockHeatingStatEntries?.isNotEmpty() == true
        }
    }

    var isRefreshing by remember {
        mutableStateOf(
            displayCircularProgressIndicatorInPreview
        )
    }

    val isControllerAddressValid: Boolean = !mainViewModel?.getControllerAddress().isNullOrBlank()

    val coroutineScope = rememberCoroutineScope()

    var heatingStats by remember { mutableStateOf(mockHeatingStatEntries ?: emptyList()) }

    val heatingSetValsViewModel: HeatingSetValsViewModel = viewModel(
        factory = HeatingSetValsViewModelFactory(heatingInitialSetVals)
    )

    @Suppress("UNCHECKED_CAST")
    LaunchedEffect(key1 = isControllerAddressValid) { // Key on controller address validity
        if (mainViewModel != null && isControllerAddressValid) { // Check for null and validity
            fetchHeatingStats(
                mainViewModel,
            ) { newHeatingStats ->
                heatingStats = newHeatingStats as List<HeatingStatsEntry>
            }
            fetchHeatingSetVals(
                mainViewModel,
            ) { newHeatingSetVals ->
                val newHeatingSetValsCasted = newHeatingSetVals as HeatingSetVals
                Log.d("HeatingScreen", "New heating set vals: $newHeatingSetValsCasted")
                heatingSetValsViewModel.setValues(newHeatingSetValsCasted)
            }
        }
    }

    AquariumControllerTheme {
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
                                    Spacer(modifier = Modifier.size(width = 10.dp, height = 10.dp))
                                    HeatingRangeSliderTextFieldsPortraitComposable(
                                        myHeatingSetValsViewModel = heatingSetValsViewModel,
                                        heatingInitialSetVals = heatingInitialSetVals
                                    )
                                }
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
                                                heatingStats =
                                                    newHeatingStats as List<HeatingStatsEntry>
                                            }
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
    updateHeatingStats: (Any) -> Unit,
) {
    withContext(Dispatchers.IO) {
        val url =
            "http://" + mainViewModel.getControllerAddress() + WebApiUrls.HEATING_GET_STATS
        val heatingStringsStats = HeatingStatsReader(url).readStatsEntry()
        val heatingStats =
            heatingStringsStats?.map { it.transformToHeatingStatsEntry() } ?: emptyList()
        updateHeatingStats(heatingStats)
    }
}

private suspend fun fetchHeatingSetVals(
    mainViewModel: MainViewModel,
    updateHeatingSetVals: (Any) -> Unit,
) {
    withContext(Dispatchers.IO) {
        val url =
            "http://" + mainViewModel.getControllerAddress() + WebApiUrls.HEATING_GET_SET_VALS
        val heatingSetValsList = HeatingSetValsReader(url).readHeatingSetVals()
        if (!heatingSetValsList.isNullOrEmpty()) {
            Log.d("HeatingScreen", "Fetched heating set vals: $heatingSetValsList")
            withContext(Dispatchers.Main) {
                updateHeatingSetVals(heatingSetValsList.first())
            }
        } else {
            Log.d("HeatingScreen", "No heating set vals fetched")
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
fun HeatingScreenPreview() {
    HeatingScreen(
        mockHeatingSetVals = HeatingSetVals(
            heatingSwitchOffTemp = 27.5,
            heatingSwitchOnTemp = 22.5
        ),
        mockHeatingStatEntries = createMockStatsData()
    )
}