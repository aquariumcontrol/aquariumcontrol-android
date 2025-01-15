package com.example.aquariumcontroller.refill

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
import java.sql.Timestamp
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime

@Composable
fun RefillEventScreen(
    refillEvent: RefillEvent
) {
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
                text = refillEvent.getTimestamp(),
                fontSize = 12.sp
            )
        }
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = "Duration: " + refillEvent.getDuration() + " seconds",
                fontSize = 12.sp
            )
            Text(
                text = "Volume: " + refillEvent.getVolume() + " Liters",
                fontSize = 12.sp
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
fun RefillEventPreviewScreen() {
    val fixedTimestamp = Timestamp(0L)

    val mockEvent = RefillEvent(
        timestamp = fixedTimestamp,
        duration = 60,
        volume = 10.0
    )

    AquariumControllerTheme {
        RefillEventScreen(mockEvent)
    }
}