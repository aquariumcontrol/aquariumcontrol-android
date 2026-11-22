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

package com.laimburggasse.aquariumcontrol.heating.presentation

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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.laimburggasse.aquariumcontrol.heating.R
import com.laimburggasse.aquariumcontrol.common.presentation.NumericDoubleOutlinedTextFieldComposable
import com.laimburggasse.aquariumcontrol.common.presentation.RoundRefreshButtonComposable
import com.laimburggasse.aquariumcontrol.common.presentation.theme.AquariumControllerTheme

@Composable
fun HeatingRangeSliderTextFieldsPortraitComposable(
    heatingSwitchOffTemp: Double,
    heatingSwitchOnTemp: Double,
    onLowerValueChanged: (Double) -> Unit,
    onUpperValueChanged: (Double) -> Unit,
    onSliderValueChanged: (Float, Float) -> Unit,
    onRefresh: () -> Unit
) {
    // This composable displays a slider and two text fields.
    // The slider is arranged on top of the two text fields.
    // This composable shall be exclusively used for portrait mode.

    val sliderRange: ClosedFloatingPointRange<Float> = heatingSwitchOnTemp.toFloat()..heatingSwitchOffTemp.toFloat()

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
            value = heatingSwitchOnTemp,
            onValueChange = { newStringValue ->
                if (newStringValue.isNotEmpty() && newStringValue.isNotBlank()) {
                    onLowerValueChanged(newStringValue.toDouble())
                }
            },
            label = stringResource(id = R.string.switch_on_at),
            modifier = Modifier
                .padding(top = 16.dp)
                .size(width = 128.dp, height = 72.dp),
        )
        Spacer(modifier = Modifier.size(width = 36.dp, height = 10.dp))
        NumericDoubleOutlinedTextFieldComposable(
            value = heatingSwitchOffTemp,
            onValueChange = { newStringValue ->
                if (newStringValue.isNotEmpty() && newStringValue.isNotBlank()) {
                    onUpperValueChanged(newStringValue.toDouble())
                }
            },
            label = stringResource(id = R.string.switch_off_at),
            modifier = Modifier
                .padding(top = 16.dp)
                .size(width = 128.dp, height = 72.dp),
        )
        Spacer(modifier = Modifier.size(width = 24.dp, height = 12.dp))
        RoundRefreshButtonComposable { onRefresh() }
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
internal fun HeatingRangeSliderTextFieldsPortraitComposableDarkPreview() {
    AquariumControllerTheme(darkTheme = true) {
        Surface {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                HeatingRangeSliderTextFieldsPortraitComposable(
                    heatingSwitchOffTemp = 27.0,
                    heatingSwitchOnTemp = 23.0,
                    onLowerValueChanged = {},
                    onUpperValueChanged = {},
                    onSliderValueChanged = { _, _ -> },
                    onRefresh = {}
                )
            }
        }
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
internal fun HeatingRangeSliderTextFieldsPortraitComposableLightPreview() {
    AquariumControllerTheme(darkTheme = false) {
        Surface {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                HeatingRangeSliderTextFieldsPortraitComposable(
                    heatingSwitchOffTemp = 27.0,
                    heatingSwitchOnTemp = 23.0,
                    onLowerValueChanged = {},
                    onUpperValueChanged = {},
                    onSliderValueChanged = {lowerValue, upperValue -> },
                    onRefresh = {}
                )
            }
        }
    }
}
