package com.example.aquariumcontroller.balling.history

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.aquariumcontroller.ui.theme.AquariumControllerTheme

@Composable
fun BallingHistoryEventScreen(
    ballingHistoryDosingEvent: BallingHistoryDosingEvent
) {
    // This screen displays one Balling dosing event.
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
                text = "Timestamp",
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
                text = "Pump ID: ${ballingHistoryDosingEvent.pumpId}",
                fontSize = 12.sp
            )
            Text(
                text = "Dosing volume: ${"%.2f".format(ballingHistoryDosingEvent.volume)} ml",
                fontSize = 12.sp
            )
            Text(
                text = "Label: ${ballingHistoryDosingEvent.label}",
                fontSize = 12.sp
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
fun BallingEventPreviewScreen() {
    val mockEvent = BallingHistoryDosingEvent(
        timestamp = "2025-01-18 12:00:00",
        pumpId = 1,
        volume = 1.5,
        label = "MockDosingUnit 1"
    )

    AquariumControllerTheme {
        BallingHistoryEventScreen(mockEvent)
    }
}