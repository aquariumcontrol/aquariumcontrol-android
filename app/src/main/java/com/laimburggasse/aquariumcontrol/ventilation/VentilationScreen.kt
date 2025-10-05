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
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun VentilationScreen(
    navHostController: NavHostController? = null,
    mockVentilationUiState: VentilationUiState? = null,
    mainViewModel: MainViewModel? = null,
    darkTheme: Boolean? = null
) {
    // This screen displays
    // - headline
    // - a range slider for setting the control target temperatures
    // - numeric input fields for entering the control target temperatures
    // - a button to update the control target temperatures
    // - a button to reset the control target temperatures

    val isControllerAddressValid: Boolean = !mainViewModel?.getControllerAddress().isNullOrBlank()

    var ventilationUiState by remember { mutableStateOf(mockVentilationUiState ?: VentilationUiState.Loading) }

    val coroutineScope = rememberCoroutineScope()

    val configuration = LocalConfiguration.current

    val ventilationInitialSetVals = VentilationSetVals(
        ventilationSwitchOffTemp = 23.5,
        ventilationSwitchOnTemp = 27.5
    )

    val ventilationSetValsViewModel: VentilationSetValsViewModel = viewModel(
        factory = VentilationSetValsViewModelFactory(ventilationInitialSetVals)
    )

    fun loadData() {
        coroutineScope.launch {
            if (mainViewModel != null) {
                fetchVentilationSetVals(
                    mainViewModel,
                ) { newVentilationUiState ->
                    ventilationUiState = newVentilationUiState
                    when (newVentilationUiState) {
                        is VentilationUiState.Success -> {
                            // Update the view model with the data
                            val newVentilationSetVals = newVentilationUiState.ventilationSetVals
                            ventilationSetValsViewModel.setValues(newVentilationSetVals)
                        }
                        is VentilationUiState.Error -> {
                            // Do nothing
                        }
                        VentilationUiState.Loading -> {
                            // Do nothing
                        }
                    }
                }
            }
        }
    }

    LaunchedEffect(key1 = isControllerAddressValid) { // Key on controller address validity
        if (mainViewModel != null && isControllerAddressValid) { // Check for null and validity
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
                            when (ventilationUiState) {
                                is VentilationUiState.Success -> {
                                    when (configuration.orientation) {
                                        Configuration.ORIENTATION_LANDSCAPE -> {
                                            Row {
                                                VentilationButtonsComposable(
                                                    ventilationSetValsViewModel = ventilationSetValsViewModel,
                                                    mainViewModel = mainViewModel,
                                                    coroutineScope = coroutineScope,
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
                                                coroutineScope = coroutineScope,
                                            )
                                            VentilationRangeSliderTextFieldsPortraitComposable(
                                                myVentilationSetValsViewModel = ventilationSetValsViewModel,
                                                ventilationInitialSetVals = ventilationInitialSetVals
                                            )
                                        }
                                    }
                                }
                                is VentilationUiState.Error -> {
                                    Column(
                                        modifier = Modifier.fillMaxSize().padding(16.dp),
                                        verticalArrangement = Arrangement.Center,
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text("Error loading data: ${(ventilationUiState as VentilationUiState.Error).errorMessage}")
                                    }
                                }
                                is VentilationUiState.Loading -> {
                                    Column(
                                        modifier = Modifier.fillMaxSize().padding(16.dp),
                                        verticalArrangement = Arrangement.Center,
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text("Loading...")
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

private suspend fun fetchVentilationSetVals(
    mainViewModel: MainViewModel,
    updateVentilationSetVals: (VentilationUiState) -> Unit,
) {
    withContext(Dispatchers.IO) {
        val url =
            "http://" + mainViewModel.getControllerAddress() + WebApiUrls.VENTILATION_GET_SET_VALS
        val ventilationSetValsReader = VentilationSetValsReader(url)
        when (val result = ventilationSetValsReader.readVentilationSetVals()) {
            is DataFetchResult.Success -> {
                val ventilationSetValsList = result.data
                updateVentilationSetVals(VentilationUiState.Success(ventilationSetValsList.first()))
            }
            is DataFetchResult.Error -> {
                updateVentilationSetVals(VentilationUiState.Error(result.errorMessage))
            }
        }
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
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
internal fun VentilationScreenPreviewSuccess() {
    val mockVentilationUiState = VentilationUiState.Success(
        VentilationSetVals(
            ventilationSwitchOffTemp = 23.5,
            ventilationSwitchOnTemp = 27.5
        )
    )
    VentilationScreen(
        mockVentilationUiState = mockVentilationUiState
    )
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
internal fun VentilationScreenPreviewError() {
    VentilationScreen(
        mockVentilationUiState = VentilationUiState.Error(
            errorMessage = "Error message"
        )
    )
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
internal fun VentilationScreenPreviewLoading() {
    VentilationScreen(mockVentilationUiState = VentilationUiState.Loading)
}
