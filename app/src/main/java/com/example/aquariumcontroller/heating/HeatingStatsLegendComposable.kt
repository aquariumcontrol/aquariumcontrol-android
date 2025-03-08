package com.example.aquariumcontroller.heating

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
import com.example.aquariumcontroller.R
import com.example.aquariumcontroller.ui.theme.AquariumControllerTheme

@Composable
fun HeatingStatsLegend() {
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
                            color = heatingStatsTextColorDefinition(HeatingStatsSignal.WATER_TEMPERATURE),
                            modifier = Modifier.padding(
                                horizontal = 4.dp
                            )
                        )
                        Text(text = stringResource(R.string.water_temperature_c), fontSize = 12.sp)
                    }
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Row {
                        Text(
                            text = rectBullet,
                            fontSize = 12.sp,
                            color = heatingStatsTextColorDefinition(HeatingStatsSignal.AMBIENT_TEMPERATURE),
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
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Row {
                        Text(
                            text = rectBullet, fontSize = 12.sp,
                            color = heatingStatsTextColorDefinition(HeatingStatsSignal.ENERGY),
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
                            color = heatingStatsTextColorDefinition(HeatingStatsSignal.WATER_TEMPERATURE),
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
                            color = heatingStatsTextColorDefinition(HeatingStatsSignal.AMBIENT_TEMPERATURE),
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
                            color = heatingStatsTextColorDefinition(HeatingStatsSignal.ENERGY),
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
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
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
fun HeatingStatsLegendPreview() {
    AquariumControllerTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            HeatingStatsLegend()
        }
    }
}