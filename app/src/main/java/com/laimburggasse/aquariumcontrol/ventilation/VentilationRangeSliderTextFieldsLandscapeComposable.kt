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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.laimburggasse.aquariumcontrol.lib_composables.NumericDoubleOutlinedTextFieldComposable
import com.laimburggasse.aquariumcontrol.ui.theme.AquariumControllerTheme

@Composable
fun VentilationRangeSliderTextFieldsLandscapeComposable(
    ventilationSwitchOffTemp: Double,
    ventilationSwitchOnTemp: Double,
    onLowerValueChanged: (Double) -> Unit,
    onUpperValueChanged: (Double) -> Unit,
    onSliderValueChanged: (Float, Float) -> Unit
) {
    // This composable displays a slider and two text fields.
    // The slider is arrange in between the two text fields in one row.
    // This composable shall be exclusively used for landscape mode.

    val sliderRange: ClosedFloatingPointRange<Float> = ventilationSwitchOffTemp.toFloat()..ventilationSwitchOnTemp.toFloat()

    Row(verticalAlignment = Alignment.CenterVertically) {
        NumericDoubleOutlinedTextFieldComposable(
            value = ventilationSwitchOffTemp,
            onValueChange = { newStringValue ->
                if (newStringValue.isNotEmpty() && newStringValue.isNotBlank()) {
                    onLowerValueChanged(newStringValue.toDouble())
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
                onSliderValueChanged(newValue.start, newValue.endInclusive)
            },
            valueRange = 20f..30f,
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.6f)
        )
        Spacer(modifier = Modifier.width(8.dp))
        NumericDoubleOutlinedTextFieldComposable(
            value = ventilationSwitchOnTemp,
            onValueChange = { newStringValue ->
                if (newStringValue.isNotEmpty() && newStringValue.isNotBlank()) {
                    onUpperValueChanged(newStringValue.toDouble())
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
                ventilationSwitchOffTemp = 23.0,
                ventilationSwitchOnTemp = 27.0,
                onLowerValueChanged = {},
                onUpperValueChanged = {},
                onSliderValueChanged = {lowerValue, upperValue -> }
            )
        }
    }
}