package com.example.aquariumcontroller.feed.schedule

import android.content.res.Configuration
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.aquariumcontroller.ui.theme.AquariumControllerTheme

@Composable
fun FeedScheduleEntryComposable(
    feedScheduleEntry: FeedScheduleEntry
) {
    // This composable displays one feed schedule entry:
    // - Name of feed profile
    // - timestamp of schedule
    // - information if the feed profile execution should be repeated daily

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
            modifier = Modifier
                .weight(5f)
        ) {
            Text(
                text = "Profile name: ${feedScheduleEntry.profileName}",
                fontSize = 12.sp
            )
            Text(
                text = "Scheduled for ${feedScheduleEntry.getTimestampString()}",
                fontSize = 12.sp
            )
            Text(
                text = "Repeat daily: ${if (feedScheduleEntry.isDaily) "Yes" else "No"}",
                fontSize = 12.sp
            )
        }
        Column(
            Modifier
                .align(Alignment.CenterVertically)
        ) {
            Row {
                Icon(
                    imageVector = Icons.Filled.Edit,
                    contentDescription = "Edit",
                    Modifier.size(48.dp)
                )
                Icon(
                    imageVector = Icons.Filled.Delete,
                    contentDescription = "Delete",
                    Modifier.size(48.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun FeedScheduleEntryComposablePreview() {
    val mockFeedScheduleEntryImport = FeedScheduleEntryImport(
        timestampString = "2023-09-01 12:00:00",
        profileName = "MockProfile",
        isDaily = 1
    )

    val mockFeedScheduleEntry = mockFeedScheduleEntryImport.transformToFeedScheduleEntry()

    AquariumControllerTheme {
        FeedScheduleEntryComposable(
            feedScheduleEntry = mockFeedScheduleEntry
        )
    }
}