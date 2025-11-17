package com.laimburggasse.aquariumcontrol.feed.presentation.entry

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
import androidx.compose.material.icons.filled.Schedule
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
import com.laimburggasse.aquariumcontrol.feed.R
import com.laimburggasse.aquariumcontrol.feed.domain.model.FeedProfile
import com.laimburggasse.aquariumcontrol.common.presentation.theme.AquariumControllerTheme
import com.laimburggasse.aquariumcontrol.feed.testing.FeedMockData.mockFeedProfile

@Composable
fun FeedProfileSummaryComposable(
    feedProfile: FeedProfile,
    onEditClick: () -> Unit,
    onScheduleClick: () -> Unit,
    onDeleteClick: () -> Unit,
) {
    // This composable displays one Feed dosing profile summary consisting of:
    // - The profile ID
    // - The profile name
    // - The edit icon
    // - The schedule icon
    // - The delete icon
    // When the user clicks on one of the icons, the corresponding action is triggered.

    Row(
        modifier = Modifier.fillMaxWidth().border(
                border = BorderStroke(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.onSurface
                )
            )
            .padding(all = 8.dp)
    ) {
        Column(modifier = Modifier.weight(weight = 1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = stringResource(id = R.string.id) + feedProfile.profileID,
                    fontSize = 12.sp
                )
            }
        }

        Column(modifier = Modifier.weight(weight = 3f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = stringResource(id = R.string.name) + ": " + feedProfile.profileName,
                    fontSize = 12.sp
                )
            }
        }
        Column(horizontalAlignment = Alignment.End) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Filled.Edit,
                    contentDescription = stringResource(id = R.string.edit),
                    modifier = Modifier.size(size = 48.dp).clickable {onEditClick()}
                )
                Spacer(modifier = Modifier.width(width = 16.dp))
                Icon(
                    imageVector = Icons.Filled.Schedule,
                    contentDescription = stringResource(id = R.string.schedule),
                    modifier = Modifier.size(size = 48.dp).clickable {onScheduleClick()}
                )
                Spacer(modifier = Modifier.width(width = 16.dp))
                Icon(
                    imageVector = Icons.Filled.Delete,
                    contentDescription = stringResource(id = R.string.delete),
                    modifier = Modifier.size(size = 48.dp).clickable {onDeleteClick()}
                )
            }
        }
    }
}


@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
internal fun FeedProfileSummaryComposablePreview() {
    AquariumControllerTheme {
        Surface(
            color = MaterialTheme.colorScheme.background
        ) {
            FeedProfileSummaryComposable(
                feedProfile = mockFeedProfile,
                onEditClick = {},
                onScheduleClick = {},
                onDeleteClick = {},
            )
        }
    }
}