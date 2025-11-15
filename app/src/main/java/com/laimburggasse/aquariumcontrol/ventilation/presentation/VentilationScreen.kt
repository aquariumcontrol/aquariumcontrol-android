package com.laimburggasse.aquariumcontrol.ventilation.presentation

import android.content.res.Configuration
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
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
import com.laimburggasse.aquariumcontrol.lib_composables.presentation.ScreenHeadlineComposable
import com.laimburggasse.aquariumcontrol.ventilation.domain.model.VentilationSetVals

@Composable
fun VentilationScreen(
    ventilationSetValsUiState: VentilationSetValsUiState,
    ventilationSetValsValid: State<Boolean?>,
    onVentilationSetValsEvent: (VentilationSetValsUiEvent) -> Unit
) {
    // This screen displays
    // - headline
    // - a range slider for setting the control target temperatures
    // - numeric input fields for entering the control target temperatures
    // - a button to update the control target temperatures
    // - a button to reset the control target temperatures

    val configuration: Configuration = LocalConfiguration.current

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        ScreenHeadlineComposable(text = stringResource(R.string.ventilation_control))
        Scaffold(
            topBar = { },
            bottomBar = { },
            floatingActionButton = {},
            snackbarHost = { SnackbarHost(hostState = remember { SnackbarHostState() })}
        ) { innerPadding ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(all = 24.dp).padding(paddingValues = innerPadding)
            ) {
                when (ventilationSetValsUiState) {
                    is VentilationSetValsUiState.Success -> {
                        val ventilationSetVals: VentilationSetVals = ventilationSetValsUiState.ventilationSetVals
                        when (configuration.orientation) {
                            Configuration.ORIENTATION_LANDSCAPE -> {
                                Row {
                                    VentilationButtonsComposable(
                                        onUpdateClicked = { onVentilationSetValsEvent(
                                            VentilationSetValsUiEvent.Update) },
                                        onResetClicked = { onVentilationSetValsEvent(
                                            VentilationSetValsUiEvent.Reset) },
                                        ventilationSetValsValid
                                    )
                                }
                                VentilationRangeSliderTextFieldsLandscapeComposable(
                                    ventilationSwitchOffTemp = ventilationSetVals.ventilationSwitchOffTemp,
                                    ventilationSwitchOnTemp = ventilationSetVals.ventilationSwitchOnTemp,
                                    onLowerValueChanged = { newValue ->
                                        onVentilationSetValsEvent(
                                            VentilationSetValsUiEvent.ChangeLowerValue(
                                                lowerValue = newValue.toFloat()
                                            )
                                        )
                                    },
                                    onUpperValueChanged = { newValue ->
                                        onVentilationSetValsEvent(
                                            VentilationSetValsUiEvent.ChangeUpperValue(
                                                upperValue = newValue.toFloat()
                                            )
                                        )
                                    },
                                    onSliderValueChanged = { newLowerValue, newUpperValue ->
                                        onVentilationSetValsEvent(
                                            VentilationSetValsUiEvent.ChangeRange(
                                                lowerValue = newLowerValue,
                                                upperValue = newUpperValue
                                            )
                                        )
                                    },
                                    onRefresh = { onVentilationSetValsEvent(
                                        VentilationSetValsUiEvent.Refresh
                                    )}
                                )
                            }
                            else -> {
                                VentilationButtonsComposable(
                                    onUpdateClicked = { onVentilationSetValsEvent(
                                        VentilationSetValsUiEvent.Update) },
                                    onResetClicked = { onVentilationSetValsEvent(
                                        VentilationSetValsUiEvent.Reset) },
                                    ventilationSetValsValid
                                )
                                VentilationRangeSliderTextFieldsPortraitComposable(
                                    ventilationSwitchOffTemp = ventilationSetVals.ventilationSwitchOffTemp,
                                    ventilationSwitchOnTemp = ventilationSetVals.ventilationSwitchOnTemp,
                                    onLowerValueChanged = { newValue ->
                                        onVentilationSetValsEvent(
                                            VentilationSetValsUiEvent.ChangeLowerValue(
                                                lowerValue = newValue.toFloat()
                                            )
                                        )
                                    },
                                    onUpperValueChanged = { newValue ->
                                        onVentilationSetValsEvent(
                                            VentilationSetValsUiEvent.ChangeUpperValue(
                                                upperValue = newValue.toFloat()
                                            )
                                        )
                                    },
                                    onSliderValueChanged = { newLowerValue, newUpperValue ->
                                        onVentilationSetValsEvent(
                                            VentilationSetValsUiEvent.ChangeRange(
                                                lowerValue = newLowerValue,
                                                upperValue = newUpperValue
                                            )
                                        )
                                    },
                                    onRefresh = { onVentilationSetValsEvent(
                                        VentilationSetValsUiEvent.Refresh
                                    )}
                                )
                            }
                        }
                    }
                    is VentilationSetValsUiState.Error -> {
                        Column(
                            modifier = Modifier.fillMaxSize().padding(all = 16.dp),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(text = "Error loading data: ${ventilationSetValsUiState.errorMessage}")
                        }
                    }
                    is VentilationSetValsUiState.Loading -> {
                        Column(
                            modifier = Modifier.fillMaxSize().padding(all = 16.dp),
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
                    is VentilationSetValsUiState.Empty -> {
                        Column(
                            modifier = Modifier.fillMaxSize().padding(all = 16.dp),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(text = stringResource(id = R.string.no_set_values_configured_check_server_configuration))
                        }
                    }
                }
            }
        }
    }
}

val mockVentilationSetValsUiStateSuccess = VentilationSetValsUiState.Success(
    ventilationSetVals = VentilationSetVals(
        ventilationSwitchOffTemp = 23.5,
        ventilationSwitchOnTemp = 27.5
    )
)

val mockVentilationSetValsUiStateError = VentilationSetValsUiState.Error(
    errorMessage = "Mock Error loading data"
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
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
internal fun VentilationScreenPreviewSuccess() {
    MainScaffold(
        isControllerLoaded = true,
        navigationRouteSource = ViewNavigationRoutes.ventilation,
        screenContent = {
            VentilationScreen(
                ventilationSetValsUiState = mockVentilationSetValsUiStateSuccess,
                onVentilationSetValsEvent = {},
                ventilationSetValsValid = remember {mutableStateOf(value = true)}
            )
        },
        onNavigate = {},
        darkTheme = isSystemInDarkTheme()
    )
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
internal fun VentilationScreenPreviewError() {
    MainScaffold(
        isControllerLoaded = true,
        navigationRouteSource = ViewNavigationRoutes.ventilation,
        screenContent = {
            VentilationScreen(
                ventilationSetValsUiState = mockVentilationSetValsUiStateError,
                ventilationSetValsValid = remember {mutableStateOf(value = true)},
                onVentilationSetValsEvent = {},
            )
        },
        onNavigate = {},
        darkTheme = isSystemInDarkTheme()
    )
}
