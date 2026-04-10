/*
 * Copyright 2025 Uwe Martin
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.laimburggasse.aquariumcontrol.balling.presentation.edit

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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.laimburggasse.aquariumcontrol.balling.R
import com.laimburggasse.aquariumcontrol.common.presentation.NumericDoubleOutlinedTextFieldComposable
import com.laimburggasse.aquariumcontrol.common.presentation.theme.AquariumControllerTheme

@Composable
fun BallingProfileEditTextFieldSliderComposable(
    sliderPositionNewDosingVolume: Double,
    onEvent: (BallingProfileEditUiEvent) -> Unit = {},
) {
    // This composable contains content separated from BallingProfileEditScreen to allow
    // dynamic placement depending on screen orientation:
    // - numerical text field
    // - slider

    NumericDoubleOutlinedTextFieldComposable(
        value = sliderPositionNewDosingVolume,
        onValueChange = { newStringValue ->
            if (newStringValue.isNotEmpty() && newStringValue.isNotBlank()) {
                val newNumericValue: Double = newStringValue.toDouble()
                onEvent(BallingProfileEditUiEvent.ChangeDosingVolume(newDosingValue = newNumericValue))
            }
        },
        label = stringResource(R.string.new_dosing_volume_ml_h),
        modifier = Modifier
            .width(width = 200.dp)
            .padding(top = 16.dp),
    )
    Slider(
        value = sliderPositionNewDosingVolume.toFloat(),
        modifier = Modifier.padding(all = 16.dp),
        onValueChange = {
            onEvent(BallingProfileEditUiEvent.ChangeDosingVolume(newDosingValue = it.toDouble()))
        },
        valueRange = BallingProfileEditSliderRange.RANGE
    )
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
internal fun BallingProfileEditTextFieldSliderPortraitComposableDarkPreview() {
    AquariumControllerTheme(darkTheme = true) {
        Surface(color = MaterialTheme.colorScheme.background) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                BallingProfileEditTextFieldSliderComposable(
                    sliderPositionNewDosingVolume = 2.0,
                )
            }
        }
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
internal fun BallingProfileEditTextFieldSliderPortraitComposableLightPreview() {
    AquariumControllerTheme(darkTheme = false) {
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
@Composable
internal fun BallingProfileEditTextFieldSliderLandscapeComposableDarkPreview() {
    AquariumControllerTheme(darkTheme = true) {
        Surface(color = MaterialTheme.colorScheme.background) {
            Row {
                BallingProfileEditTextFieldSliderComposable(
                    sliderPositionNewDosingVolume = 2.0,
                )
            }
        }
    }
}

@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO,
    device = "spec:width=600dp,height=400dp,orientation=landscape"
)
@Composable
internal fun BallingProfileEditTextFieldSliderLandscapeComposableLightPreview() {
    AquariumControllerTheme(darkTheme = false) {
        Surface(color = MaterialTheme.colorScheme.background) {
            Row {
                BallingProfileEditTextFieldSliderComposable(
                    sliderPositionNewDosingVolume = 2.0,
                )
            }
        }
    }
}