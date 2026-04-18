/*
 * Copyright 2025 Uwe Martin
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.laimburggasse.aquariumcontrol.feed.R
import com.laimburggasse.aquariumcontrol.feed.domain.model.FeedProfile
import com.laimburggasse.aquariumcontrol.common.presentation.theme.AquariumControllerTheme
import com.laimburggasse.aquariumcontrol.feed.preview.FeedPreviewDataImpl.mockFeedProfile

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
                    modifier = Modifier
                        .size(size = 48.dp)
                        .testTag("feed_profile_edit_${feedProfile.profileID}")
                        .clickable {onEditClick()}
                )
                Spacer(modifier = Modifier.width(width = 16.dp))
                Icon(
                    imageVector = Icons.Filled.Schedule,
                    contentDescription = stringResource(id = R.string.schedule),
                    modifier = Modifier
                        .size(size = 48.dp)
                        .testTag("feed_profile_schedule_${feedProfile.profileID}")
                        .clickable {onScheduleClick()}
                )
                Spacer(modifier = Modifier.width(width = 16.dp))
                Icon(
                    imageVector = Icons.Filled.Delete,
                    contentDescription = stringResource(id = R.string.delete),
                    modifier = Modifier
                        .size(size = 48.dp)
                        .testTag("feed_profile_delete_${feedProfile.profileID}")
                        .clickable {onDeleteClick()}
                )
            }
        }
    }
}


@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
internal fun FeedProfileSummaryComposableDarkPreview() {
    AquariumControllerTheme(darkTheme = true) {
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


@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
internal fun FeedProfileSummaryComposableLightPreview() {
    AquariumControllerTheme(darkTheme = false) {
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