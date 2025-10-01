package com.laimburggasse.aquariumcontrol.ventilation

import android.content.res.Configuration
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
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
import com.laimburggasse.aquariumcontrol.lib_composables.NumericDoubleOutlinedTextFieldComposable
import com.laimburggasse.aquariumcontrol.ui.theme.AquariumControllerTheme

@Composable
fun VentilationRangeSliderTextFieldsLandscapeComposable(
    myVentilationSetValsViewModel: VentilationSetValsViewModel?,
    ventilationInitialSetVals: VentilationSetVals
) {
    // This composable displays a slider and two text fields.
    // The slider is arrange in between the two text fields in one row.
    // This composable shall be exclusively used for landscape mode.

    val ventilationSetValsViewModel = myVentilationSetValsViewModel ?: viewModel(
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
        initial = ventilationInitialSetVals.ventilationSwitchOffTemp
    )
    val upperBoundValue by ventilationSetValsViewModel.upperBoundValue.observeAsState(
        ventilationInitialSetVals.ventilationSwitchOnTemp
    )

    Row(verticalAlignment = Alignment.CenterVertically) {
        NumericDoubleOutlinedTextFieldComposable(
            value = lowerBoundValue,
            onValueChange = { newStringValue ->
                if (newStringValue.isNotEmpty() && newStringValue.isNotBlank()) {
                    val newNumericValue = newStringValue.toFloat()
                    ventilationSetValsViewModel.onLowerValueChanged(
                        newNumericValue
                    )
                }
            },
            label = "Switch off at",
            modifier = Modifier
                .padding(top = 16.dp)
                .size(width = 128.dp, height = 72.dp)
                .weight(0.2f),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.width(8.dp))
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
                .weight(0.6f)
        )
        Spacer(modifier = Modifier.width(8.dp))
        NumericDoubleOutlinedTextFieldComposable(
            value = upperBoundValue,
            onValueChange = { newStringValue ->
                if (newStringValue.isNotEmpty() && newStringValue.isNotBlank()) {
                    val newNumericValue = newStringValue.toFloat()
                    ventilationSetValsViewModel.onUpperValueChanged(
                        newNumericValue
                    )
                }
            },
            label = "Switch on at",
            modifier = Modifier
                .padding(top = 16.dp)
                .size(width = 128.dp, height = 72.dp)
                .weight(0.2f),
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
internal fun VentilationRangeSliderTextFieldsLandscapeComposablePreview() {
    AquariumControllerTheme {
        Surface {
            VentilationRangeSliderTextFieldsLandscapeComposable(
                myVentilationSetValsViewModel = null,
                ventilationInitialSetVals = VentilationSetVals(
                    ventilationSwitchOffTemp = 23.0, ventilationSwitchOnTemp = 27.0
                )
            )
        }
    }
}