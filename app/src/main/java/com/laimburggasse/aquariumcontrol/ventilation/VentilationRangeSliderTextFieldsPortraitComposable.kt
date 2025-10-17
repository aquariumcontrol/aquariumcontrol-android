package com.laimburggasse.aquariumcontrol.ventilation

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.laimburggasse.aquariumcontrol.lib_composables.NumericDoubleOutlinedTextFieldComposable
import com.laimburggasse.aquariumcontrol.ui.theme.AquariumControllerTheme

@Composable
fun VentilationRangeSliderTextFieldsPortraitComposable(
    ventilationSwitchOffTemp: Double,
    ventilationSwitchOnTemp: Double,
    onLowerValueChanged: (Double) -> Unit,
    onUpperValueChanged: (Double) -> Unit,
    onSliderValueChanged: (Float, Float) -> Unit
) {
    // This composable displays a slider and two text fields.
    // The slider is arrange on top of the two text fields.
    // This composable shall be exclusively used for portrait mode.

    val sliderRange: ClosedFloatingPointRange<Float> = ventilationSwitchOffTemp.toFloat()..ventilationSwitchOnTemp.toFloat()

    RangeSlider(
        value = sliderRange,
        onValueChange = { newValue ->
            onSliderValueChanged(newValue.start, newValue.endInclusive)
        },
        valueRange = 20f..30f,
        modifier = Modifier
            .fillMaxWidth()
    )
    Row {
        NumericDoubleOutlinedTextFieldComposable(
            value = ventilationSwitchOffTemp,
            onValueChange = { newStringValue ->
                if (newStringValue.isNotEmpty() && newStringValue.isNotBlank()) {
                    onLowerValueChanged(newStringValue.toDouble())
                }
            },
            label = "Switch on at",
            modifier = Modifier
                .padding(top = 16.dp)
                .size(width = 128.dp, height = 72.dp),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.size(width = 72.dp, height = 10.dp))
        NumericDoubleOutlinedTextFieldComposable(
            value = ventilationSwitchOnTemp,
            onValueChange = { newStringValue ->
                if (newStringValue.isNotEmpty() && newStringValue.isNotBlank()) {
                    onUpperValueChanged(newStringValue.toDouble())
                }
            },
            label = "Switch off at",
            modifier = Modifier
                .padding(top = 16.dp)
                .size(width = 128.dp, height = 72.dp),
            textAlign = TextAlign.Center
        )
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
internal fun VentilationRangeSliderTextFieldsPortraitComposablePreview() {
    AquariumControllerTheme {
        Surface {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                VentilationRangeSliderTextFieldsPortraitComposable(
                    ventilationSwitchOffTemp = 23.0,
                    ventilationSwitchOnTemp = 27.0,
                    onLowerValueChanged = {},
                    onUpperValueChanged = {},
                    onSliderValueChanged = {lowerValue, upperValue -> }
                )
            }
        }
    }
}


