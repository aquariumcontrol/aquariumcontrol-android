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

package com.laimburggasse.aquariumcontrol.feed.presentation.edit

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.laimburggasse.aquariumcontrol.feed.domain.model.FeedProfile
import com.laimburggasse.aquariumcontrol.common.presentation.theme.AquariumControllerTheme
import com.laimburggasse.aquariumcontrol.feed.preview.FeedPreviewDataImpl.mockFeedProfile
import com.laimburggasse.aquariumcontrol.feed.preview.FeedPreviewDataImpl.mockInitialExpandedItemIds

@Composable
fun FeedProfileEditMenuComposable(
    itemsToRender: List<FeedProfileEditMenuItem>,
    feedProfile: FeedProfile,
    isExpanded: Set<Int>,
    onFeedProfileEditUiEvent: (FeedProfileEditUiEvent) -> Unit
) {
    // This composable acts as container for the feed profile edit menu items.
    if (LocalInspectionMode.current) {
        // In Preview: Render a simple, static Column instead of LazyColumn
        Column(modifier = Modifier.heightIn(max = 20000.dp)) {
            itemsToRender.forEach { feedProfileEditMenuItem ->
                FeedProfileEditMenuItemComposable(
                    feedProfileEditMenuItem,
                    isExpanded,
                    feedProfile,
                    onFeedProfileEditUiEvent
                )
            }
        }
    } else {
        // In App Runtime: Use the performant LazyColumn
        LazyColumn(modifier = Modifier.heightIn(max = 20000.dp)) {
            items(items = itemsToRender) { feedProfileEditMenuItem ->
                FeedProfileEditMenuItemComposable(
                    feedProfileEditMenuItem,
                    isExpanded,
                    feedProfile,
                    onFeedProfileEditUiEvent
                )
            }
        }
    }
}

@Preview(showBackground = true, heightDp = 1400, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
internal fun FeedProfileEditMenuComposableDarkPreview() {
    AquariumControllerTheme(darkTheme = true) {
        Surface(color = MaterialTheme.colorScheme.background) {
            FeedProfileEditMenuComposable(
                itemsToRender = FeedProfileEditMenuItems.phases,
                feedProfile = mockFeedProfile,
                isExpanded = mockInitialExpandedItemIds,
                onFeedProfileEditUiEvent = {}
            )
        }
    }
}

@Preview(showBackground = true, heightDp = 1400, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
internal fun FeedProfileEditMenuComposableLightPreview() {
    AquariumControllerTheme(darkTheme = false) {
        Surface(color = MaterialTheme.colorScheme.background) {
            FeedProfileEditMenuComposable(
                itemsToRender = FeedProfileEditMenuItems.phases,
                feedProfile = mockFeedProfile,
                isExpanded = mockInitialExpandedItemIds,
                onFeedProfileEditUiEvent = {}
            )
        }
    }
}