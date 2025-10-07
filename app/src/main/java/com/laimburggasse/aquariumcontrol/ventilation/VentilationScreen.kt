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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.laimburggasse.aquariumcontrol.MockMainViewModel
import com.laimburggasse.aquariumcontrol.R
import com.laimburggasse.aquariumcontrol.ViewNavigationRoutes
import com.laimburggasse.aquariumcontrol.lib_composables.ScreenHeadlineComposable
import com.laimburggasse.aquariumcontrol.maindropdownmenu.MainDropdownMenuComposable
import com.laimburggasse.aquariumcontrol.ui.theme.AquariumControllerTheme

@Composable
fun VentilationScreen(
    navHostController: NavHostController? = null,
    mockVentilationSetValsUiState: VentilationSetValsUiState? = null,
    mainViewModel: MainViewModel,
    darkTheme: Boolean? = null
) {
    // This screen displays
    // - headline
    // - a range slider for setting the control target temperatures
    // - numeric input fields for entering the control target temperatures
    // - a button to update the control target temperatures
    // - a button to reset the control target temperatures

    val isControllerAddressValid: Boolean = !mainViewModel.getControllerAddress().isNullOrBlank()

    var ventilationSetValsUiState by remember { mutableStateOf(mockVentilationSetValsUiState ?: VentilationSetValsUiState.Loading) }

    val configuration = LocalConfiguration.current

    val ventilationInitialSetVals = VentilationSetVals(
        ventilationSwitchOffTemp = 23.5,
        ventilationSwitchOnTemp = 27.5
    )

    val ventilationSetValsViewModel: VentilationSetValsViewModel = viewModel(
        factory = VentilationSetValsViewModelFactory(ventilationInitialSetVals)
    )

    LaunchedEffect(key1 = isControllerAddressValid) { // Key on controller address validity
        if (isControllerAddressValid) {
            ventilationSetValsViewModel.loadData(mainViewModel = mainViewModel)
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
                                    when (configuration.orientation) {
                                        Configuration.ORIENTATION_LANDSCAPE -> {
                                            Row {
                                                VentilationButtonsComposable(
                                                    ventilationSetValsViewModel = ventilationSetValsViewModel,
                                                    mainViewModel = mainViewModel,
                                                )
                                            }
                                            VentilationRangeSliderTextFieldsLandscapeComposable(
                                                myVentilationSetValsViewModel = ventilationSetValsViewModel,
                                                ventilationInitialSetVals = ventilationInitialSetVals
                                            )
                                        }
                                        else -> {
                                            VentilationButtonsComposable(
                                                ventilationSetValsViewModel = ventilationSetValsViewModel,
                                                mainViewModel = mainViewModel,
                                            )
                                            VentilationRangeSliderTextFieldsPortraitComposable(
                                                myVentilationSetValsViewModel = ventilationSetValsViewModel,
                                                ventilationInitialSetVals = ventilationInitialSetVals
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
                    navHostController = navHostController,
                    navigationRouteSource = ViewNavigationRoutes.ventilation,
                    mainViewModel = mainViewModel
                )
            }
        }
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
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
internal fun VentilationScreenPreviewSuccess() {
    val mockVentilationSetValsUiState = VentilationSetValsUiState.Success(
        VentilationSetVals(
            ventilationSwitchOffTemp = 23.5,
            ventilationSwitchOnTemp = 27.5
        )
    )
    VentilationScreen(
        mainViewModel = mockMainViewModel,
        mockVentilationSetValsUiState = mockVentilationSetValsUiState,
    )
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
internal fun VentilationScreenPreviewError() {
    VentilationScreen(
        mainViewModel = mockMainViewModel,
        mockVentilationSetValsUiState = VentilationSetValsUiState.Error(
            errorMessage = "Error message"
        )
    )
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
internal fun VentilationScreenPreviewLoading() {
    VentilationScreen(
        mainViewModel = mockMainViewModel,
        mockVentilationSetValsUiState = VentilationSetValsUiState.Loading
    )
}
