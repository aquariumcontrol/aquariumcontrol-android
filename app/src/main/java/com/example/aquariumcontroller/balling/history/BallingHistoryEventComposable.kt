package com.example.aquariumcontroller.balling.history

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
import com.example.aquariumcontroller.R
import com.example.aquariumcontroller.ui.theme.AquariumControllerTheme

@Composable
fun BallingHistoryEventComposable(
    ballingHistoryDosingEvent: BallingHistoryDosingEvent
) {
    // This screen displays one Balling dosing event with the following information:
    // - timestamp
    // - pump id
    // - dosing volume
    // - label of the Balling profile

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                border = BorderStroke(
                    1.dp,
                    MaterialTheme.colorScheme.onSurface
                )
            )
            .padding(8.dp)
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = stringResource(R.string.timestamp),
                fontSize = 12.sp
            )
            Text(
                text = ballingHistoryDosingEvent.timestamp,
                fontSize = 12.sp
            )
        }
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = stringResource(R.string.pump_id) + "${ballingHistoryDosingEvent.pumpId}",
                fontSize = 12.sp
            )
            Text(
                text = stringResource(R.string.dosing_volume) +
                        "%.2f".format(ballingHistoryDosingEvent.volume) +
                        stringResource(R.string.milliliter_unit),
                fontSize = 12.sp
            )
            Text(
                text = stringResource(R.string.label) + ballingHistoryDosingEvent.label,
                fontSize = 12.sp
            )
        }
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
fun BallingHistoryEventComposablePreview() {
    val mockEvent = BallingHistoryDosingEvent(
        timestamp = "2025-01-18 12:00:00",
        pumpId = 1,
        volume = 1.5,
        label = "MockDosingUnit 1"
    )

    AquariumControllerTheme {
        Surface(
            color = MaterialTheme.colorScheme.background
        ) {
            BallingHistoryEventComposable(mockEvent)
        }
    }
}