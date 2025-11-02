package com.laimburggasse.aquariumcontrol.feed.schedule

import android.content.res.Configuration
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.laimburggasse.aquariumcontrol.R
import com.laimburggasse.aquariumcontrol.ui.theme.AquariumControllerTheme

@Composable
fun FeedScheduleEntryComposable(
    feedScheduleEntry: FeedScheduleEntry,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    // This composable displays one feed schedule entry:
    // - Name of feed profile
    // - timestamp of schedule
    // - information if the feed profile execution should be repeated daily

    Row(modifier = Modifier.fillMaxWidth()
            .border(border = BorderStroke(width = 1.dp, color = MaterialTheme.colorScheme.onSurface))
            .padding(all = 8.dp)
    ) {
        Column(modifier = Modifier.weight(weight = 5f)) {
            Text(
                text = stringResource(id = R.string.profile_name_label) + feedScheduleEntry.profileName,
                fontSize = 12.sp
            )
            Text(
                text = stringResource(id = R.string.scheduled_for) + feedScheduleEntry.getTimestampString(
                    timestampToBeFormatted = feedScheduleEntry.timestamp
                ),
                fontSize = 12.sp
            )
            Text(
                text = stringResource(R.string.repeat_daily_label) +
                        if (feedScheduleEntry.repeatDaily)
                            stringResource(R.string.yes)
                        else stringResource(R.string.no),
                fontSize = 12.sp
            )
        }
        Column(Modifier.align(Alignment.CenterVertically)) {
            Row {
                //* deactivated because API is currently (Feb, 2025) not available
                Icon(
                    imageVector = Icons.Filled.Edit,
                    contentDescription = "Edit",
                    Modifier.size(size = 48.dp).clickable { onEditClick() }
                )
                Spacer(modifier = Modifier.width(width = 16.dp))
                Icon(
                    imageVector = Icons.Filled.Delete,
                    contentDescription = stringResource(R.string.delete),
                    Modifier.size(size = 48.dp).clickable { onDeleteClick() }
                )
            }
        }
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
internal fun FeedScheduleEntryComposablePreview() {
    val mockFeedScheduleEntryImport = FeedScheduleEntryImport(
        timestampString = "2023-09-01 12:00:00",
        profileId = 1,
        profileName = "MockProfile",
        isDaily = 1
    )

    val mockFeedScheduleEntry = mockFeedScheduleEntryImport.transformToFeedScheduleEntry()

    AquariumControllerTheme {
        Surface(
            color = MaterialTheme.colorScheme.background
        ) {
            FeedScheduleEntryComposable(
                feedScheduleEntry = mockFeedScheduleEntry,
                onEditClick = { },
                onDeleteClick = {}
            )
        }
    }
}