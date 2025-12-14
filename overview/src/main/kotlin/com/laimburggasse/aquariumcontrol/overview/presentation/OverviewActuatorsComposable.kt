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

package com.laimburggasse.aquariumcontrol.overview.presentation

import android.content.res.Configuration
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.laimburggasse.aquariumcontrol.overview.R
import com.laimburggasse.aquariumcontrol.overview.domain.model.OverviewData
import com.laimburggasse.aquariumcontrol.common.presentation.theme.AquariumControllerTheme
import com.laimburggasse.aquariumcontrol.overview.preview.OverviewPreviewDataImpl.mockOverviewData

@Composable
fun OverviewActuatorsComposable(overviewData: OverviewData) {
    Column(
        modifier = Modifier.border(
            border = BorderStroke(
                width = 1.dp,
                color = MaterialTheme.colorScheme.onSurface
            )
        ).padding(all = 8.dp)
    ) {
        Row(Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.weight(weight = 1f)) {
                Row(modifier = Modifier.align(Alignment.Start)) {
                    Text(text = stringResource(R.string.tank_level_switch_position))
                }
            }
            Column(modifier = Modifier.weight(weight = 0.5f)) {
                Row(modifier = Modifier.align(Alignment.End)) {
                    Text(text = overviewData.tankLevelSwitchPosition)
                }
            }
        }
        Row(Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.weight(weight = 1f)) {
                Row(modifier = Modifier.align(Alignment.Start)) {
                    Text(text = stringResource(R.string.ventilation))
                }
            }
            Column(modifier = Modifier.weight(weight = 1f)) {
                Row(modifier = Modifier.align(Alignment.End)) {
                    Text(text = overviewData.surfaceVentilationStatus)
                }
            }
        }
        Row(Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.weight(weight = 1f)) {
                Row(modifier = Modifier.align(Alignment.Start)) {
                    Text(text = stringResource(R.string.heating))
                }
            }
            Column(modifier = Modifier.weight(weight = 1f)) {
                Row(modifier = Modifier.align(Alignment.End)) {
                    Text(text = overviewData.heatingStatus)
                }
            }
        }
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
internal fun OverviewActuatorsComposableDarkPreview() {
    AquariumControllerTheme(darkTheme = true) {
        Surface {
            OverviewActuatorsComposable(mockOverviewData)
        }
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
internal fun OverviewActuatorsComposableLightPreview() {
    AquariumControllerTheme(darkTheme = false) {
        Surface {
            OverviewActuatorsComposable(mockOverviewData)
        }
    }
}