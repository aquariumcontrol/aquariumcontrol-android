package com.laimburggasse.aquariumcontrol.refill.history

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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.laimburggasse.aquariumcontrol.ui.theme.AquariumControllerTheme
import java.sql.Timestamp

@Composable
fun RefillEventComposable(
    refillHistoryLogEntry: RefillHistoryLogEntry
) {
    // This composable displays a refill event:
    // Text objects for name and value of Timestamp, Duration, Volume

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
                text = refillHistoryLogEntry.timestamp,
                fontSize = 12.sp
            )
        }
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = "Duration: ${"%.1f".format(refillHistoryLogEntry.duration)} seconds",
                fontSize = 12.sp
            )
            Text(
                text = "Volume: ${"%.2f".format(refillHistoryLogEntry.volume)} Liters",
                fontSize = 12.sp
            )
        }
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
internal fun RefillEventComposablePreview() {
    val fixedTimestamp = Timestamp(0L)

    val mockEvent = RefillHistoryLogEntry(
        timestamp = fixedTimestamp,
        duration = 60.0,
        volume = 10.0
    )

    AquariumControllerTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            RefillEventComposable(mockEvent)
        }
    }
}