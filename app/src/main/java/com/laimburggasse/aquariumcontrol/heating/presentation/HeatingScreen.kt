package com.laimburggasse.aquariumcontrol.heating.presentation

import android.content.res.Configuration
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.laimburggasse.aquariumcontrol.MainScaffold
import com.laimburggasse.aquariumcontrol.R
import com.laimburggasse.aquariumcontrol.ViewNavigationRoutes
import com.laimburggasse.aquariumcontrol.heating.domain.model.HeatingSetVals
import com.laimburggasse.aquariumcontrol.heating.domain.model.HeatingStatsEntry
import com.laimburggasse.aquariumcontrol.heating.data.remote.dto.HeatingStatsStringEntry
import com.laimburggasse.aquariumcontrol.lib_composables.presentation.ScreenHeadlineComposable

@Composable
fun HeatingScreen(
    heatingSetValsUiState: HeatingSetValsUiState,
    heatingSetValsValid: State<Boolean?>,
    onHeatingSetValsEvent: (HeatingSetValsUiEvent) -> Unit,
    heatingStatsUiState: HeatingStatsUiState,
    onHeatingStatsEvent: (HeatingStatsUiEvent) -> Unit
) {
    // This screen displays
    // - a button to update the heating controller values
    // - a button to reset the heating controller values
    // - a range slider for setting the control target temperatures
    // - numeric input fields for entering the control target temperatures
    // - the heating statistics incl. refresh button

    val configuration: Configuration = LocalConfiguration.current

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        ScreenHeadlineComposable(text = stringResource(id = R.string.heating_control))
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
                        val heatingSetVals: HeatingSetVals = heatingSetValsUiState.heatingSetVals
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            when (configuration.orientation) {
                                Configuration.ORIENTATION_LANDSCAPE -> {
                                    Row {
                                        HeatingButtonsComposable(
                                            onUpdateClicked = {
                                                onHeatingSetValsEvent(
                                                    HeatingSetValsUiEvent.Update
                                                )
                                            },
                                            onResetClicked = {
                                                onHeatingSetValsEvent(
                                                    HeatingSetValsUiEvent.Reset
                                                )
                                            },
                                            heatingSetValsValid
                                        )
                                    }
                                    Row {
                                        HeatingRangeSliderTextFieldsLandscapeComposable(
                                            heatingSwitchOnTemp = heatingSetVals.heatingSwitchOnTemp,
                                            heatingSwitchOffTemp = heatingSetVals.heatingSwitchOffTemp,
                                            onLowerValueChanged = { lowerValue ->
                                                onHeatingSetValsEvent(
                                                    HeatingSetValsUiEvent.ChangeLowerValue(
                                                        lowerValue = lowerValue.toFloat()
                                                    )
                                                )
                                            },
                                            onUpperValueChanged = { upperValue ->
                                                onHeatingSetValsEvent(
                                                    HeatingSetValsUiEvent.ChangeUpperValue(
                                                        upperValue = upperValue.toFloat()
                                                    )
                                                )
                                            },
                                            onSliderValueChanged = { newLowerValue, newUpperValue ->
                                                onHeatingSetValsEvent(
                                                    HeatingSetValsUiEvent.ChangeRange(
                                                        newLowerValue, newUpperValue
                                                    )
                                                )
                                            },
                                            onRefresh = { onHeatingSetValsEvent(HeatingSetValsUiEvent.Refresh) }
                                        )
                                    }
                                }
                                else -> {
                                    HeatingButtonsComposable(
                                        onUpdateClicked = {
                                            onHeatingSetValsEvent(
                                                HeatingSetValsUiEvent.Update
                                            )
                                        },
                                        onResetClicked = { onHeatingSetValsEvent(HeatingSetValsUiEvent.Reset) },
                                        heatingSetValsValid
                                    )
                                    Spacer(modifier = Modifier.size(width = 10.dp,height = 10.dp))
                                    HeatingRangeSliderTextFieldsPortraitComposable(
                                        heatingSwitchOnTemp = heatingSetVals.heatingSwitchOnTemp,
                                        heatingSwitchOffTemp = heatingSetVals.heatingSwitchOffTemp,
                                        onLowerValueChanged = { lowerValue ->
                                            onHeatingSetValsEvent(
                                                HeatingSetValsUiEvent.ChangeLowerValue(
                                                    lowerValue = lowerValue.toFloat()
                                                )
                                            )
                                        },
                                        onUpperValueChanged = { upperValue ->
                                            onHeatingSetValsEvent(
                                                HeatingSetValsUiEvent.ChangeUpperValue(
                                                    upperValue = upperValue.toFloat()
                                                )
                                            )
                                        },
                                        onSliderValueChanged = { newLowerValue, newUpperValue ->
                                            onHeatingSetValsEvent(
                                                HeatingSetValsUiEvent.ChangeRange(
                                                    newLowerValue, newUpperValue
                                                )
                                            )
                                        },
                                        onRefresh = { onHeatingSetValsEvent(HeatingSetValsUiEvent.Refresh) }
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
                                Text(text = "Error loading set value data: ${heatingSetValsUiState.errorMessage}")
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
                                CircularProgressIndicator(
                                    modifier = Modifier.size(size = 150.dp),
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(text = stringResource(id = R.string.loading_set_value_data))
                            }
                        }
                    }
                    is HeatingSetValsUiState.Empty -> {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(all = 16.dp),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(text = stringResource(id = R.string.no_set_values_configured_check_server_configuration))
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
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(all = 16.dp),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = stringResource(id = R.string.error_loading_statistical_data) +
                                        heatingStatsUiState.errorMessage
                                )
                            }
                        }
                    }
                    HeatingStatsUiState.Loading -> {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(all = 16.dp),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(size = 150.dp),
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(text = stringResource(id = R.string.loading_statistical_data))
                            }
                        }
                    }
                    is HeatingStatsUiState.Success -> {
                        val heatingStats: List<HeatingStatsEntry> = heatingStatsUiState.heatingStats
                        HeatingStatsComposable(
                            heatingStats = heatingStats,
                            updateData = { onHeatingStatsEvent(HeatingStatsUiEvent.Refresh) }
                        )
                    }
                    is HeatingStatsUiState.Empty -> {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(all = 16.dp),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(text = stringResource(id = R.string.no_heating_stats_available))
                            }
                        }
                    }
                }
            }
        }
    }
}

fun createMockStatsData(): List<HeatingStatsEntry> {
    val mockHeatingStatsStringEntries: List<HeatingStatsStringEntry> = listOf(
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

val mockHeatingStatsUiState = HeatingStatsUiState.Success(
    heatingStats = createMockStatsData()
)

val mockHeatingSetVals = HeatingSetVals(
    heatingSwitchOffTemp = 23.5,
    heatingSwitchOnTemp = 27.5
)

val mockHeatingSetValsUiState = HeatingSetValsUiState.Success(
    heatingSetVals = mockHeatingSetVals
)

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
    MainScaffold(
        isControllerLoaded = true,
        navigationRouteSource = ViewNavigationRoutes.heating,
        screenContent = {
            HeatingScreen(
                heatingSetValsUiState = mockHeatingSetValsUiState,
                onHeatingSetValsEvent = {},
                heatingStatsUiState = mockHeatingStatsUiState,
                onHeatingStatsEvent = {},
                heatingSetValsValid = remember { mutableStateOf(value = true) }
            )
        },
        onNavigate = {},
        darkTheme = isSystemInDarkTheme()
    )
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
fun HeatingScreenPreviewError() {
    MainScaffold(
        isControllerLoaded = true,
        navigationRouteSource = ViewNavigationRoutes.heating,
        screenContent = {
            HeatingScreen(
                heatingSetValsUiState = mockHeatingSetValsUiState,
                onHeatingSetValsEvent = {},
                heatingStatsUiState = mockHeatingStatsUiState,
                onHeatingStatsEvent = {},
                heatingSetValsValid = remember { mutableStateOf(value = true) }
            )
        },
        onNavigate = {},
        darkTheme = isSystemInDarkTheme()
    )
}
