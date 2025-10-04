package com.laimburggasse.aquariumcontrol.ventilation

import android.content.res.Configuration
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
import com.laimburggasse.aquariumcontrol.R
import com.laimburggasse.aquariumcontrol.ViewNavigationRoutes
import com.laimburggasse.aquariumcontrol.lib_composables.ScreenHeadlineComposable
import com.laimburggasse.aquariumcontrol.maindropdownmenu.MainDropdownMenuComposable
import com.laimburggasse.aquariumcontrol.ui.theme.AquariumControllerTheme
import com.laimburggasse.aquariumcontrol.webapi.WebApiUrls
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun VentilationScreen(
    navHostController: NavHostController? = null,
    mockVentilationSetVals: VentilationSetVals? = null,
    mainViewModel: MainViewModel? = null,
    darkTheme: Boolean? = null
) {
    // This screen displays
    // - headline
    // - a range slider for setting the control target temperatures
    // - numeric input fields for entering the control target temperatures
    // - a button to update the control target temperatures
    // - a button to reset the control target temperatures

    val ventilationInitialSetVals = mockVentilationSetVals ?: VentilationSetVals(
        ventilationSwitchOffTemp = 23.0, ventilationSwitchOnTemp = 27.0
    )

    val isControllerAddressValid: Boolean = !mainViewModel?.getControllerAddress().isNullOrBlank()

    val coroutineScope = rememberCoroutineScope()

    val configuration = LocalConfiguration.current

    val ventilationSetValsViewModel: VentilationSetValsViewModel = viewModel(
        factory = VentilationSetValsViewModelFactory(ventilationInitialSetVals)
    )

    LaunchedEffect(key1 = isControllerAddressValid) { // Key on controller address validity
        if (mainViewModel != null && isControllerAddressValid) { // Check for null and validity
            fetchVentilationSetVals(
                mainViewModel,
            ) { newVentilationSetVals ->
                val newVentilationSetValsCasted = newVentilationSetVals as VentilationSetVals
                ventilationSetValsViewModel.setValues(newVentilationSetValsCasted)
            }
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
    updateVentilationSetVals: (Any) -> Unit,
) {
    withContext(Dispatchers.IO) {
        val url =
            "http://" + mainViewModel.getControllerAddress() + WebApiUrls.VENTILATION_GET_SET_VALS
        val ventilationSetValsList = VentilationSetValsReader(url).readVentilationSetVals()
        if (!ventilationSetValsList.isNullOrEmpty()) {
            withContext(Dispatchers.Main) {
                updateVentilationSetVals(ventilationSetValsList.first())
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
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)@Composable
internal fun VentilationScreenPreview() {
    VentilationScreen(
        mockVentilationSetVals = VentilationSetVals(
            ventilationSwitchOffTemp = 23.5,
            ventilationSwitchOnTemp = 27.5
        )
    )
}