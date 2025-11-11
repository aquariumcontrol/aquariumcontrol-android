package com.laimburggasse.aquariumcontrol.heating.presentation

import android.content.res.Configuration
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import com.laimburggasse.aquariumcontrol.lib_composables.RoundRefreshButtonComposable
import com.laimburggasse.aquariumcontrol.ui.theme.AquariumControllerTheme

@Composable
fun HeatingRangeSliderTextFieldsLandscapeComposable(
    heatingSwitchOnTemp: Double,
    heatingSwitchOffTemp: Double,
    onLowerValueChanged: (Double) -> Unit,
    onUpperValueChanged: (Double) -> Unit,
    onSliderValueChanged: (Float, Float) -> Unit,
    onRefresh: () -> Unit
) {
    // This composable displays a slider and two text fields.
    // The slider is arrange in between the two text fields in one row.
    // This composable shall be exclusively used for landscape mode.

    val sliderRange: ClosedFloatingPointRange<Float> = heatingSwitchOnTemp.toFloat()..heatingSwitchOffTemp.toFloat()

    Row(verticalAlignment = Alignment.CenterVertically) {
        NumericDoubleOutlinedTextFieldComposable(
            value = heatingSwitchOnTemp,
            onValueChange = { newStringValue ->
                if (newStringValue.isNotEmpty() && newStringValue.isNotBlank()) {
                    onLowerValueChanged(newStringValue.toDouble())
                }
            },
            label = "Switch on at",
            modifier = Modifier
                .padding(top = 16.dp)
                .size(width = 128.dp, height = 72.dp)
                .weight(weight = 0.25f),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.width(10.dp))
        RangeSlider(
            value = sliderRange,
            onValueChange = { newValue ->
                onSliderValueChanged(newValue.start, newValue.endInclusive)
            },
            valueRange = 20f..30f,
            modifier = Modifier.weight(weight = 1f)
        )
        Spacer(modifier = Modifier.width(width = 10.dp))
        NumericDoubleOutlinedTextFieldComposable(
            value = heatingSwitchOffTemp,
            onValueChange = { newStringValue ->
                if (newStringValue.isNotEmpty() && newStringValue.isNotBlank()) {
                    onUpperValueChanged(newStringValue.toDouble())
                }
            },
            label = "Switch off at",
            modifier = Modifier
                .padding(top = 16.dp)
                .size(width = 128.dp, height = 72.dp)
                .weight(weight = 0.25f),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.size(width = 24.dp, height = 12.dp))
        RoundRefreshButtonComposable { onRefresh() }
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
internal fun HeatingRangeSliderTextFieldsLandscapeComposablePreview() {
    AquariumControllerTheme {
        Surface {
            HeatingRangeSliderTextFieldsLandscapeComposable(
                heatingSwitchOffTemp = 23.0,
                heatingSwitchOnTemp = 27.0,
                onLowerValueChanged = {},
                onUpperValueChanged = {},
                onSliderValueChanged = {lowerValue, upperValue -> },
                onRefresh = {}
            )
        }
    }
}


