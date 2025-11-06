package com.laimburggasse.aquariumcontrol.balling.presentation.history

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
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.laimburggasse.aquariumcontrol.R
import com.laimburggasse.aquariumcontrol.balling.domain.BallingHistoryLogEntry
import com.laimburggasse.aquariumcontrol.ui.theme.AquariumControllerTheme

@Composable
fun BallingHistoryEventComposable(ballingHistoryLogEntry: BallingHistoryLogEntry) {
    // This screen displays one Balling dosing event with the following information:
    // - timestamp
    // - pump id
    // - dosing volume
    // - label of the Balling profile

    Row(modifier = Modifier.fillMaxWidth().border(
            border = BorderStroke(width = 1.dp, color = MaterialTheme.colorScheme.onSurface)
        )
        .padding(all = 8.dp)
    ) {
        Column(modifier = Modifier.weight(weight = 1f)) {
            Text(text = stringResource(R.string.timestamp), fontSize = 12.sp)
            Text(text = ballingHistoryLogEntry.timestamp, fontSize = 12.sp)
        }
        Column(modifier = Modifier.weight(weight = 1f)) {
            Text(
                text = stringResource(R.string.pump_id) + "${ballingHistoryLogEntry.pumpId}",
                fontSize = 12.sp
            )
            Text(
                text = stringResource(R.string.dosing_volume) +
                        "%.2f".format(ballingHistoryLogEntry.volume) +
                        stringResource(R.string.milliliter_unit),
                fontSize = 12.sp
            )
            Text(text = stringResource(R.string.label) + ballingHistoryLogEntry.label, fontSize = 12.sp)
        }
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
internal fun BallingHistoryEventComposablePreview() {
    val mockEvent = BallingHistoryLogEntry(
        timestamp = "2025-01-18 12:00:00",
        pumpId = 1,
        volume = 1.5,
        label = "MockDosingUnit 1"
    )

    AquariumControllerTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            BallingHistoryEventComposable(ballingHistoryLogEntry = mockEvent)
        }
    }
}