package com.example.aquariumcontroller.balling.edit

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.aquariumcontroller.R
import com.example.aquariumcontroller.lib_composables.NumericDoubleOutlinedTextFieldComposable
import com.example.aquariumcontroller.ui.theme.AquariumControllerTheme

@Composable
fun BallingProfileEditTextFieldSliderComposable(
    sliderPositionNewDosingVolume: Double,
    ballingProfileEditViewModel: BallingProfileEditViewModel? = null
) {
    // This composable contains content separated from BallingProfileEditScreen to allow
    // dynamic placement depending on screen orientation:
    // - numerical text field
    // - slider

    NumericDoubleOutlinedTextFieldComposable(
        value = sliderPositionNewDosingVolume,
        onValueChange = { newStringValue ->
            if (newStringValue.isNotEmpty() && newStringValue.isNotBlank()) {
                val newNumericValue = newStringValue.toDouble()
                ballingProfileEditViewModel?.onVolumeChange(newNumericValue)
            }
        },
        label = stringResource(R.string.new_dosing_volume_ml_h),
        textAlign = TextAlign.Center,
        modifier = Modifier
            .width(200.dp)
            .padding(top = 16.dp),
    )
    Slider(
        value = sliderPositionNewDosingVolume.toFloat(),
        modifier = Modifier.padding(16.dp),
        onValueChange = {
            ballingProfileEditViewModel?.onVolumeChange(it.toDouble())
        },
        valueRange = BallingProfileEditSliderRange.RANGE
    )
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun BallingProfileEditTextFieldSliderComposablePortraitPreview() {
    AquariumControllerTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                BallingProfileEditTextFieldSliderComposable(
                    sliderPositionNewDosingVolume = 2.0,
                )
            }
        }
    }
}

@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    device = "spec:width=600dp,height=400dp,orientation=landscape"
)
@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO,
    device = "spec:width=600dp,height=400dp,orientation=landscape"
)
@Composable
fun BallingProfileEditTextFieldSliderComposableLandscapePreview() {
    AquariumControllerTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            Row {
                BallingProfileEditTextFieldSliderComposable(
                    sliderPositionNewDosingVolume = 2.0,
                )
            }
        }
    }
}