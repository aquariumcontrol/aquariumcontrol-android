package com.laimburggasse.aquariumcontrol.feed.presentation.history

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
import com.laimburggasse.aquariumcontrol.ui.theme.AquariumControllerTheme
import com.laimburggasse.aquariumcontrol.R
import com.laimburggasse.aquariumcontrol.feed.domain.model.FeedHistoryLogEntry

@Composable
fun FeedHistoryEventComposable(
    feedHistoryLogEntry: FeedHistoryLogEntry
) {
    // This composable displays one feed dosing event:
    // - text objects for showing timestamp
    // - text objects for showing feed profile name
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                border = BorderStroke(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.onSurface
                )
            )
            .padding(all = 8.dp)
    ) {
        Column(modifier = Modifier.weight(weight = 1f)) {
            Text(
                text = stringResource(id = R.string.timestamp),
                fontSize = 12.sp
            )
            Text(
                text = feedHistoryLogEntry.timestamp,
                fontSize = 12.sp
            )
        }
        Column(modifier = Modifier.weight(weight = 1f)) {
            Text(
                text = stringResource(R.string.name),
                fontSize = 12.sp
            )
            Text(
                text = feedHistoryLogEntry.profileName,
                fontSize = 12.sp
            )
        }
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
internal fun FeedHistoryEventComposablePreview() {
    val mockEvent = FeedHistoryLogEntry(
        timestamp = "2025-01-18 12:00:00",
        feederOnTime = 20000,
        profileName = "Mock feed profile 1",
        idx = 1
    )

    AquariumControllerTheme {
        Surface(
            color = MaterialTheme.colorScheme.background
        ) {
            FeedHistoryEventComposable(feedHistoryLogEntry = mockEvent)
        }
    }
}