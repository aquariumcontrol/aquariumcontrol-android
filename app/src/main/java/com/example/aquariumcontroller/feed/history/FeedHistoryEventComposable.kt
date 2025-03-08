package com.example.aquariumcontroller.feed.history

import android.content.res.Configuration
import android.os.Build
import androidx.annotation.RequiresApi
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
import com.example.aquariumcontroller.ui.theme.AquariumControllerTheme

@Composable
fun FeedHistoryEventComposable(
    feedHistoryEvent: FeedHistoryEvent
) {
    // This composable displays one feed dosing event:
    // - text objects for showing timestamp
    // - text objects for showing feed profile name

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
                text = feedHistoryEvent.timestamp,
                fontSize = 12.sp
            )
        }
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = "Name",
                fontSize = 12.sp
            )
            Text(
                text = feedHistoryEvent.profileName,
                fontSize = 12.sp
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
fun FeedEventPreviewScreen() {
    val mockEvent = FeedHistoryEvent(
        timestamp = "2025-01-18 12:00:00",
        feederOnTime = 20000,
        profileName = "Mock feed profile 1",
        idx = 1
    )

    AquariumControllerTheme {
        Surface(
            color = MaterialTheme.colorScheme.background
        ) {
            FeedHistoryEventComposable(mockEvent)
        }
    }
}