package com.laimburggasse.aquariumcontrol.overview

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
import com.laimburggasse.aquariumcontrol.ui.theme.AquariumControllerTheme

@Composable
fun OverviewTimestampComposable(overviewData: OverviewData) {
    Column(
        modifier = Modifier
            .border(
                border = BorderStroke(
                    1.dp,
                    MaterialTheme.colorScheme.onSurface
                )
            )
            .padding(8.dp)
    ) {
        Row(Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.weight(1f)) {
                Row(modifier = Modifier.align(Alignment.Start)) {
                    Text(text = stringResource(R.string.timestamp))
                }
            }
            Column(modifier = Modifier.weight(1f)) {
                Row(modifier = Modifier.align(Alignment.End)) {
                    Text(text = overviewData.timestamp)
                }
            }
        }
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
internal fun OverviewTimestampComposablePreview() {
    AquariumControllerTheme {
        Surface {
            OverviewTimestampComposable(mockOverviewData)
        }
    }
}