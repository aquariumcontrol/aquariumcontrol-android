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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.laimburggasse.aquariumcontrol.heating.R
import com.laimburggasse.aquariumcontrol.common.presentation.theme.AquariumControllerTheme

@Composable
fun HeatingStatsLegendComposable() {
    // This composable displays the legend for the heating stats:
    // - a bullet for each signal
    // - the name of the signal incl. unit

    val configuration = LocalConfiguration.current

    val rectBullet = stringResource(id = R.string.rect_bullet)

    Row {
        when (configuration.orientation) {
            Configuration.ORIENTATION_LANDSCAPE -> {
                Column {
                    Row {
                        Text(
                            text = rectBullet,
                            fontSize = 12.sp,
                            color = heatingStatsTextColorDefinition(signal = HeatingStatsSignal.WATER_TEMPERATURE),
                            modifier = Modifier.padding(
                                horizontal = 4.dp
                            )
                        )
                        Text(text = stringResource(R.string.water_temperature_c), fontSize = 12.sp)
                    }
                }
                Spacer(modifier = Modifier.width(width = 10.dp))
                Column {
                    Row {
                        Text(
                            text = rectBullet,
                            fontSize = 12.sp,
                            color = heatingStatsTextColorDefinition(signal = HeatingStatsSignal.AMBIENT_TEMPERATURE),
                            modifier = Modifier.padding(
                                horizontal = 4.dp
                            )
                        )
                        Text(
                            text = stringResource(R.string.heating_stats_ambient_temperature_c),
                            fontSize = 12.sp
                        )
                    }
                }
                Spacer(modifier = Modifier.width(width = 10.dp))
                Column {
                    Row {
                        Text(
                            text = rectBullet, fontSize = 12.sp,
                            color = heatingStatsTextColorDefinition(signal = HeatingStatsSignal.ENERGY),
                            modifier = Modifier.padding(
                                horizontal = 4.dp
                            )
                        )
                        Text(
                            text = stringResource(R.string.heating_stats_energy_wh),
                            fontSize = 12.sp
                        )
                    }
                }
            }
            else -> {
                Column(
                    modifier = Modifier.padding(
                        horizontal = 12.dp
                    )
                ) {
                    Row {
                        Text(
                            text = rectBullet,
                            fontSize = 12.sp,
                            color = heatingStatsTextColorDefinition(signal = HeatingStatsSignal.WATER_TEMPERATURE),
                            modifier = Modifier.padding(
                                horizontal = 4.dp
                            )
                        )
                        Text(text = stringResource(R.string.water_temperature_c), fontSize = 12.sp)
                    }
                    Row {
                        Text(
                            text = rectBullet,
                            fontSize = 12.sp,
                            color = heatingStatsTextColorDefinition(signal = HeatingStatsSignal.AMBIENT_TEMPERATURE),
                            modifier = Modifier.padding(
                                horizontal = 4.dp
                            )
                        )
                        Text(
                            text = stringResource(R.string.heating_stats_ambient_temperature_c),
                            fontSize = 12.sp
                        )
                    }
                    Row {
                        Text(
                            text = rectBullet, fontSize = 12.sp,
                            color = heatingStatsTextColorDefinition(signal = HeatingStatsSignal.ENERGY),
                            modifier = Modifier.padding(
                                horizontal = 4.dp
                            )
                        )
                        Text(
                            text = stringResource(R.string.heating_stats_energy_wh),
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    device = "spec:width=800dp,height=400dp,orientation=landscape"
)
@Composable
internal fun HeatingStatsLegendComposableDarkPreview() {
    AquariumControllerTheme(darkTheme = true) {
        Surface(color = MaterialTheme.colorScheme.background) {
            HeatingStatsLegendComposable()
        }
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO,
    device = "spec:width=800dp,height=400dp,orientation=landscape"
)
@Composable
internal fun HeatingStatsLegendComposableLightPreview() {
    AquariumControllerTheme(darkTheme = false) {
        Surface(color = MaterialTheme.colorScheme.background) {
            HeatingStatsLegendComposable()
        }
    }
}