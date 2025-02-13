package com.example.aquariumcontroller.heating

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.aquariumcontroller.R
import com.example.aquariumcontroller.ui.theme.AquariumControllerTheme

@Composable
fun HeatingStatsLegend() {
    val rectBullet = stringResource(id = R.string.rectBullet)
    Row() {
        Column(
            modifier = Modifier.padding(
                horizontal = 12.dp
            )
        ) {
            Row() {
                Text(
                    text = rectBullet,
                    fontSize = 12.sp,
                    color = heatingStatsTextColorDefinition(HeatingStatsSignal.WATER_TEMPERATURE),
                    modifier = Modifier.padding(
                        horizontal = 4.dp
                    )
                )
                Text(text = "Water temperature [°C]", fontSize = 12.sp)
            }
            Row() {
                Text(
                    text = rectBullet,
                    fontSize = 12.sp,
                    color = heatingStatsTextColorDefinition(HeatingStatsSignal.AMBIENT_TEMPERATURE),
                    modifier = Modifier.padding(
                        horizontal = 4.dp
                    )
                )
                Text(text = "Ambient temperature [°C]", fontSize = 12.sp)
            }
            Row() {
                Text(
                    text = rectBullet, fontSize = 12.sp,
                    color = heatingStatsTextColorDefinition(HeatingStatsSignal.ENERGY),
                    modifier = Modifier.padding(
                        horizontal = 4.dp
                    )
                )
                Text(text = "Energy [Wh]", fontSize = 12.sp)
            }
        }
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun HeatingStatsLegendPreview() {
    AquariumControllerTheme {
        HeatingStatsLegend()
    }
}