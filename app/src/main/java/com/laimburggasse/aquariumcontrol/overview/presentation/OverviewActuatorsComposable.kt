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
import com.laimburggasse.aquariumcontrol.R
import com.laimburggasse.aquariumcontrol.overview.domain.model.OverviewData
import com.laimburggasse.aquariumcontrol.common.presentation.theme.AquariumControllerTheme

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
                    Text(text = overviewData.surfaceVentilation)
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
                    Text(text = overviewData.heater)
                }
            }
        }
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
internal fun OverviewActuatorsComposablePreview() {
    AquariumControllerTheme {
        Surface {
            OverviewActuatorsComposable(mockOverviewData)
        }
    }
}