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
import com.laimburggasse.aquariumcontrol.common.presentation.theme.AquariumControllerTheme
import com.laimburggasse.aquariumcontrol.feed.preview.FeedPreviewDataImpl.mockEvent
import com.laimburggasse.aquariumcontrol.feed.R
import com.laimburggasse.aquariumcontrol.feed.domain.model.FeedHistoryLogEntry
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun FeedHistoryLogEntryComposable(
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
            val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)
            Text(
                text = dateFormat.format(Date(feedHistoryLogEntry.timestamp)),
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
@Composable
internal fun FeedHistoryLogEntryComposableDarkPreview() {
    AquariumControllerTheme(darkTheme = true) {
        Surface(color = MaterialTheme.colorScheme.background) {
            FeedHistoryLogEntryComposable(feedHistoryLogEntry = mockEvent)
        }
    }
}


@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
internal fun FeedHistoryLogEntryComposableLightPreview() {
    AquariumControllerTheme(darkTheme = false) {
        Surface(color = MaterialTheme.colorScheme.background) {
            FeedHistoryLogEntryComposable(feedHistoryLogEntry = mockEvent)
        }
    }
}