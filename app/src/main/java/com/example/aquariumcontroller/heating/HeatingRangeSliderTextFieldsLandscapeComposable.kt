package com.example.aquariumcontroller.heating

import android.content.res.Configuration
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.aquariumcontroller.lib_composables.NumericDoubleOutlinedTextFieldComposable
import com.example.aquariumcontroller.ui.theme.AquariumControllerTheme

@Composable
fun HeatingRangeSliderTextFieldsLandscapeComposable(
    myHeatingSetValsViewModel: HeatingSetValsViewModel?,
    heatingInitialSetVals: HeatingSetVals
) {
    // This composable displays a slider and two text fields.
    // The slider is arrange in between the two text fields in one row.
    // This composable shall be exclusively used for landscape mode.

    val heatingSetValsViewModel = myHeatingSetValsViewModel ?: viewModel(
        factory = HeatingSetValsViewModelFactory(heatingInitialSetVals)
    )

    // Use derived state for slider position
    val sliderRange by heatingSetValsViewModel.sliderRange.observeAsState(
        initial =
        heatingInitialSetVals.heatingSwitchOnTemp.toFloat()..
                heatingInitialSetVals.heatingSwitchOffTemp.toFloat()
    )

    // Use a separate mutable state for the text field values
    val lowerBoundValue by heatingSetValsViewModel.lowerBoundValue.observeAsState(
        initial = heatingInitialSetVals.heatingSwitchOnTemp
    )
    val upperBoundValue by heatingSetValsViewModel.upperBoundValue.observeAsState(
        heatingInitialSetVals.heatingSwitchOffTemp
    )

    Row(verticalAlignment = Alignment.CenterVertically) {
        NumericDoubleOutlinedTextFieldComposable(
            value = lowerBoundValue,
            onValueChange = { newStringValue ->
                if (newStringValue.isNotEmpty() && newStringValue.isNotBlank()) {
                    val newNumericValue = newStringValue.toFloat()
                    heatingSetValsViewModel.onLowerValueChanged(
                        newNumericValue
                    )
                }
            },
            label = "Switch on at",
            modifier = Modifier
                .padding(top = 16.dp)
                .size(width = 128.dp, height = 72.dp)
                .weight(0.25f),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.width(10.dp))
        RangeSlider(
            value = sliderRange,
            onValueChange = { newValue ->
                heatingSetValsViewModel.onRangeChangedBySlider(
                    newValue.start,
                    newValue.endInclusive
                )
            },
            valueRange = 20f..30f,
            modifier = Modifier.weight(1f)
        )
        Spacer(modifier = Modifier.width(10.dp))
        NumericDoubleOutlinedTextFieldComposable(
            value = upperBoundValue,
            onValueChange = { newStringValue ->
                if (newStringValue.isNotEmpty() && newStringValue.isNotBlank()) {
                    val newNumericValue = newStringValue.toFloat()
                    heatingSetValsViewModel.onUpperValueChanged(
                        newNumericValue
                    )
                }
            },
            label = "Switch off at",
            modifier = Modifier
                .padding(top = 16.dp)
                .size(width = 128.dp, height = 72.dp)
                .weight(0.25f),
            textAlign = TextAlign.Center
        )
    }
}

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
fun HeatingRangeSliderTextFieldsLandscapeComposablePreview() {
    AquariumControllerTheme {
        Surface {
            HeatingRangeSliderTextFieldsLandscapeComposable(
                myHeatingSetValsViewModel = null,
                heatingInitialSetVals = HeatingSetVals(
                    heatingSwitchOffTemp = 28.0, heatingSwitchOnTemp = 22.0
                )
            )
        }
    }
}


