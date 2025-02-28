package com.example.aquariumcontroller.ventilation

import android.content.res.Configuration
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.aquariumcontroller.ApiParams
import com.example.aquariumcontroller.ApiUrls
import com.example.aquariumcontroller.lib_composables.MainDropdownMenuComposable
import com.example.aquariumcontroller.MainViewModel
import com.example.aquariumcontroller.lib_composables.NumericDoubleOutlinedTextFieldComposable
import com.example.aquariumcontroller.PostRequest
import com.example.aquariumcontroller.R
import com.example.aquariumcontroller.ViewNavigationRoutes
import com.example.aquariumcontroller.ui.theme.AquariumControllerTheme
import com.example.aquariumcontroller.webApiPostRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun VentilationScreen(
    navHostController: NavHostController? = null,
    mockVentilationSetVals: VentilationSetVals? = null,
    mainViewModel: MainViewModel? = null
) {
    // This screen displays
    // - a range slider for setting the control target temperatures
    // - numeric input fields for entering the control target temperatures
    val ventilationInitialSetVals = mockVentilationSetVals ?: VentilationSetVals(
        ventilationSwitchOffTemp = 23.0, ventilationSwitchOnTemp = 27.0
    )

    val isControllerAddressValid: Boolean = !mainViewModel?.getControllerAddress().isNullOrBlank()

    val coroutineScope = rememberCoroutineScope()

    val postRequest = PostRequest()

    val context = LocalContext.current // context for web API request and Toasts

    val ventilationSetValsViewModel: VentilationSetValsViewModel = viewModel(
        factory = VentilationSetValsViewModelFactory(ventilationInitialSetVals)
    )

    // Use derived state for slider position
    val sliderRange by ventilationSetValsViewModel.sliderRange.observeAsState(
        initial =
        ventilationInitialSetVals.ventilationSwitchOffTemp.toFloat()..
                ventilationInitialSetVals.ventilationSwitchOnTemp.toFloat()
    )

    // Use a separate mutable state for the text field values
    val lowerBoundValue by ventilationSetValsViewModel.lowerBoundValue.observeAsState(
        initial = ventilationInitialSetVals.ventilationSwitchOnTemp
    )
    val upperBoundValue by ventilationSetValsViewModel.upperBoundValue.observeAsState(
        ventilationInitialSetVals.ventilationSwitchOffTemp
    )

    LaunchedEffect(key1 = isControllerAddressValid) { // Key on controller address validity
        if (mainViewModel != null && isControllerAddressValid) { // Check for null and validity
            fetchVentilationSetVals(
                mainViewModel,
            ) { newVentilationSetVals ->
                val newVentilationSetValsCasted = newVentilationSetVals as VentilationSetVals
                Log.d("VentilationScreen", "New ventilation set vals: $newVentilationSetValsCasted")
                ventilationSetValsViewModel.setValues(newVentilationSetValsCasted)
            }
        }
    }

    AquariumControllerTheme {
        // A surface container using the 'background' color from the theme
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Scaffold { innerPadding ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .padding(24.dp)
                        .padding(innerPadding)
                ) {
                    MainDropdownMenuComposable(
                        navHostController = navHostController,
                        navigationRouteSource = ViewNavigationRoutes.ventilation,
                        mainViewModel = mainViewModel
                    )
                    Text(
                        text = stringResource(R.string.VentilationControl),
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier.padding(16.dp)
                    )
                    Button(
                        onClick = {
                            ventilationSetValsViewModel.updateValues()
                            if (mainViewModel != null) {
                                webApiPostRequest(
                                    mainViewModel = mainViewModel,
                                    apiUrl = ApiUrls.VENTILATION_POST_SET_VALS,
                                    coroutineScope = coroutineScope,
                                    postRequest = postRequest,
                                    context = context,
                                    optionalGetParams = mapOf(
                                        ApiParams.VENTILATION_SWITCH_ON_TEMPERATURE to ventilationSetValsViewModel.getVentilationSwitchOnTempString(),
                                        ApiParams.VENTILATION_SWITCH_OFF_TEMPERATURE to ventilationSetValsViewModel.getVentilationSwitchOffTempString()
                                    ),
                                )
                                // Show the Toast after the API call
                                Toast.makeText(
                                    context,
                                    "Sent request to controller for updating ventilation set values",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    ) {
                        Text(text = "Update Ventilation Controller Values")
                    }
                    Button(
                        onClick = { ventilationSetValsViewModel.resetValues() }
                    ) {
                        Text(text = "Reset Ventilation Controller Values")
                    }
                    Spacer(modifier = Modifier.size(width = 10.dp, height = 10.dp))
                    Column {
                        RangeSlider(
                            value = sliderRange,
                            onValueChange = { newValue ->
                                ventilationSetValsViewModel.onRangeChangedBySlider(
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
                                    ventilationSetValsViewModel.onLowerValueChanged(newNumericValue)
                                }
                            },
                            label = "Switch off at",
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
                                    ventilationSetValsViewModel.onUpperValueChanged(newNumericValue)
                                }
                            },
                            label = "Switch on at",
                            modifier = Modifier
                                .padding(top = 16.dp)
                                .size(width = 128.dp, height = 72.dp)
                        )
                    }
                }
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
            "http://" + mainViewModel.getControllerAddress() + ApiUrls.VENTILATION_GET_SET_VALS
        val ventilationSetValsList = VentilationSetValsReader(url).readVentilationSetVals()
        if (!ventilationSetValsList.isNullOrEmpty()) {
            Log.d("VentilationScreen", "Fetched ventilation set vals: $ventilationSetValsList")
            withContext(Dispatchers.Main) {
                updateVentilationSetVals(ventilationSetValsList.first())
            }
        } else {
            Log.d("VentilationScreen", "No ventilation set vals fetched")
        }
    }
}

@Preview(showBackground = true, heightDp = 800, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun VentilationScreenPreview() {
    VentilationScreen(
        mockVentilationSetVals = VentilationSetVals(
            ventilationSwitchOffTemp = 28.5,
            ventilationSwitchOnTemp = 21.5
        )
    )
}