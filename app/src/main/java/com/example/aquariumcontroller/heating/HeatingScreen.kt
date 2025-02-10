package com.example.aquariumcontroller.heating

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.aquariumcontroller.MainDropdownMenuComposable
import com.example.aquariumcontroller.MainViewModel
import com.example.aquariumcontroller.NumericDoubleOutlinedTextFieldComposable
import com.example.aquariumcontroller.R
import com.example.aquariumcontroller.ViewNavigationRoutes
import com.example.aquariumcontroller.ui.theme.AquariumControllerTheme

fun calculateLabelPosition(
    value: Float,
    trackWidth: Int,
    minValue: Float,
    maxValue: Float
): Int {
    val range = maxValue - minValue
    if (range == 0f) return 0 // Avoid division by zero
    val positionRatio = (value - minValue) / range
    val labelX = positionRatio * trackWidth
    return labelX.toInt()
}

@Composable
fun HeatingScreen(
    navHostController: NavHostController? = null,
    mockHeatingSetVals: HeatingSetVals? = null,
    // mockHeatingStatEntries: List<HeatingStatEntry>? = null,
    mainViewModel: MainViewModel? = null
) {
    // This screen displays
    // - a range slider for setting the control target temperatures
    // - the heating statistics
    val heatingSetVals = mockHeatingSetVals ?: HeatingSetVals(
        initialSwitchOffTemp = 28.0, initialSwitchOnTemp = 22.0
    )

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
    var trackWidth by remember { mutableStateOf(0) } // Store track width

    /*
    LaunchedEffect(sliderRange) {
        heatingSetValsViewModel.onRangeChangedBySlider(
            sliderRange.,
            sliderPosition.endInclusive
        )
    }*/

    AquariumControllerTheme {
        // A surface container using the 'background' color from the theme
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(24.dp)
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
                Column() {
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
                            .weight(1f)
                            .onGloballyPositioned { coordinates ->
                                trackWidth = coordinates.size.width
                            }
                    )
                    // Positioned Start Label
                    Row(Modifier.fillMaxWidth()) { // Row to position absolutely
                        Text(
                            text = sliderRange.start.toInt().toString(),
                            modifier = Modifier
                                .padding(
                                    start = calculateLabelPosition(
                                        sliderRange.start,
                                        trackWidth,
                                        20f,
                                        30f
                                    ).dp
                                ) // Dynamic padding
                        )
                    }
                }
                Spacer(modifier = Modifier.size(width = 10.dp, height = 10.dp))
                Row() {
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
            }
        }
    }
}


@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun HeatingViewPreview() {
    AquariumControllerTheme {
        HeatingScreen(
            mockHeatingSetVals = HeatingSetVals(
                initialSwitchOffTemp = 27.5,
                initialSwitchOnTemp = 22.5
            )
        )
    }
}