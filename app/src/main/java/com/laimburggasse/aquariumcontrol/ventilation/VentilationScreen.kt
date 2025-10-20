package com.laimburggasse.aquariumcontrol.ventilation

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
fun VentilationScreen(
    mainViewModel: MainViewModel,
    ventilationSetValsViewModelFactory: VentilationSetValsViewModelFactory,
    darkTheme: Boolean? = null,
    onNavigate: (MainDropDownMenuNavigationEvent) -> Unit
) {
    // This screen displays
    // - headline
    // - a range slider for setting the control target temperatures
    // - numeric input fields for entering the control target temperatures
    // - a button to update the control target temperatures
    // - a button to reset the control target temperatures

    val ventilationSetValsViewModel: VentilationSetValsViewModel = viewModel(factory = ventilationSetValsViewModelFactory)

    val ventilationSetValsUiState: VentilationSetValsUiState by ventilationSetValsViewModel.ventilationSetValsUiState.collectAsState()

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
                        text = stringResource(R.string.ventilation_control)
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
                                .padding(24.dp)
                                .padding(innerPadding)
                        ) {
                            when (ventilationSetValsUiState) {
                                is VentilationSetValsUiState.Success -> {
                                    val ventilationSetVals = (ventilationSetValsUiState as VentilationSetValsUiState.Success).ventilationSetVals
                                    when (configuration.orientation) {
                                        Configuration.ORIENTATION_LANDSCAPE -> {
                                            Row {
                                                VentilationButtonsComposable(
                                                    onUpdateClicked = { ventilationSetValsViewModel.updateValues() },
                                                    onResetClicked = { ventilationSetValsViewModel.resetValues() },
                                                )
                                            }
                                            VentilationRangeSliderTextFieldsLandscapeComposable(
                                                ventilationSwitchOffTemp = ventilationSetVals.ventilationSwitchOffTemp,
                                                ventilationSwitchOnTemp = ventilationSetVals.ventilationSwitchOnTemp,
                                                onLowerValueChanged = { newValue -> ventilationSetValsViewModel.onLowerValueChanged(value = newValue.toFloat()) },
                                                onUpperValueChanged = { newValue -> ventilationSetValsViewModel.onUpperValueChanged(value = newValue.toFloat()) },
                                                onSliderValueChanged = { newLowerValue, newUpperValue -> ventilationSetValsViewModel.onRangeChangedBySlider(newLowerValue, newUpperValue) },
                                            )
                                        }
                                        else -> {
                                            VentilationButtonsComposable(
                                                onUpdateClicked = { ventilationSetValsViewModel.updateValues() },
                                                onResetClicked = { ventilationSetValsViewModel.resetValues() },
                                            )
                                            VentilationRangeSliderTextFieldsPortraitComposable(
                                                ventilationSwitchOffTemp = ventilationSetVals.ventilationSwitchOffTemp,
                                                ventilationSwitchOnTemp = ventilationSetVals.ventilationSwitchOnTemp,
                                                onLowerValueChanged = { newValue -> ventilationSetValsViewModel.onLowerValueChanged(value = newValue.toFloat()) },
                                                onUpperValueChanged = { newValue -> ventilationSetValsViewModel.onUpperValueChanged(value = newValue.toFloat()) },
                                                onSliderValueChanged = { newLowerValue, newUpperValue -> ventilationSetValsViewModel.onRangeChangedBySlider(newLowerValue, newUpperValue) },
                                            )
                                        }
                                    }
                                }
                                is VentilationSetValsUiState.Error -> {
                                    Column(
                                        modifier = Modifier.fillMaxSize().padding(16.dp),
                                        verticalArrangement = Arrangement.Center,
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text("Error loading data: ${(ventilationSetValsUiState as VentilationSetValsUiState.Error).errorMessage}")
                                    }
                                }
                                is VentilationSetValsUiState.Loading -> {
                                    Column(
                                        modifier = Modifier.fillMaxSize().padding(16.dp),
                                        verticalArrangement = Arrangement.Center,
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text("Loading...")
                                    }
                                }
                                is VentilationSetValsUiState.Empty -> {
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
                    }
                }
                MainDropdownMenuComposable(
                    navigationRouteSource = ViewNavigationRoutes.ventilation,
                    mainViewModel = mainViewModel,
                    onNavigate
                )
            }
        }
    }
}

val mockMainViewModel = MockMainViewModel()

val mockVentilationSetValsDataFetchResult = DataFetchResult.Success(
    data = VentilationSetVals(
        ventilationSwitchOffTemp = 23.5,
        ventilationSwitchOnTemp = 27.5
    )
)

val mockVentilationSetValsRepositorySuccess = MockVentilationSetValsRepository(
    mockVentilationSetValsDataFetchResult
)

val mockVentilationSetValsRepositoryError = MockVentilationSetValsRepository(
    mockVentilationSetValsDataFetchResult = DataFetchResult.Error(
        exception = null, errorMessage = "Error loading data"
    )
)

val mockVentilationSetValsViewModelFactorySuccess = VentilationSetValsViewModelFactory(
    ventilationSetValsRepository = mockVentilationSetValsRepositorySuccess
)

val mockVentilationSetValsViewModelFactoryError = VentilationSetValsViewModelFactory(
    ventilationSetValsRepository = mockVentilationSetValsRepositoryError
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
    VentilationScreen(
        mainViewModel = mockMainViewModel,
        ventilationSetValsViewModelFactory = mockVentilationSetValsViewModelFactorySuccess,
        onNavigate = {}
    )
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
internal fun VentilationScreenPreviewError() {
    VentilationScreen(
        mainViewModel = mockMainViewModel,
        ventilationSetValsViewModelFactory = mockVentilationSetValsViewModelFactoryError,
        onNavigate = {}
    )
}
