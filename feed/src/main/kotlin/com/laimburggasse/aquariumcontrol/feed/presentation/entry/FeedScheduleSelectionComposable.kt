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
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.laimburggasse.aquariumcontrol.feed.R
import com.laimburggasse.aquariumcontrol.feed.domain.model.FeedScheduleEntry
import com.laimburggasse.aquariumcontrol.feed.presentation.navigation.FeedNavigationEvent
import com.laimburggasse.aquariumcontrol.feed.presentation.schedule.FeedScheduleEntryComposable
import com.laimburggasse.aquariumcontrol.common.presentation.theme.AquariumControllerTheme
import com.laimburggasse.aquariumcontrol.feed.preview.FeedPreviewData.mockFeedScheduleSelectionUiStateEmpty
import com.laimburggasse.aquariumcontrol.feed.preview.FeedPreviewData.mockFeedScheduleSelectionUiStateSuccess

@Composable
fun FeedScheduleSelectionComposable(
    feedScheduleSelectionUiState: FeedScheduleSelectionUiState,
    onNavigateFromFeed: (FeedNavigationEvent, Int?, Boolean?, Long?) -> Unit,
    onFeedScheduleEntryUiEvent: (FeedScheduleUiEvent) -> Int?,
) {
    // This composable displays a label "Feed Schedule"
    // and a list of feed schedule entries
    Text(text = stringResource(R.string.feed_schedule))

    val columnModifier: Modifier = Modifier.padding(all = 8.dp)
    PullToRefreshBox(
        modifier = Modifier.fillMaxWidth(),
        state = rememberPullToRefreshState(),
        isRefreshing = feedScheduleSelectionUiState == FeedProfilesUiState.Loading,
        contentAlignment = Alignment.Center,
        onRefresh = { onFeedScheduleEntryUiEvent(FeedScheduleUiEvent.Refresh) },
        indicator = {
            if (feedScheduleSelectionUiState == FeedProfilesUiState.Loading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(size = 150.dp),
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    ) {
        when (feedScheduleSelectionUiState) {
            is FeedScheduleSelectionUiState.Success -> {
                val feedScheduleEntries: List<FeedScheduleEntry> =
                    feedScheduleSelectionUiState.feedScheduleEntries
                if (LocalInspectionMode.current) {
                    // In Preview: Render a simple, static Column instead of LazyColumn
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(all = 16.dp)
                    ) {
                        feedScheduleEntries.forEach { feedScheduleEntry ->
                            FeedScheduleEntryComposable(
                                feedScheduleEntry = feedScheduleEntry,
                                onEditClick = {},
                                onDeleteClick = {}
                            )
                        }
                    }
                } else {
                    // In App Runtime: Use the performant LazyColumn and provide lambdas
                    LazyColumn(modifier = columnModifier) {
                        items(items = feedScheduleEntries) { feedScheduleEntry ->
                            FeedScheduleEntryComposable(
                                feedScheduleEntry = feedScheduleEntry,
                                onEditClick = {
                                    onNavigateFromFeed(
                                        FeedNavigationEvent.ToFeedScheduling,
                                        feedScheduleEntry.profileId,
                                        false,
                                        feedScheduleEntry.timestamp
                                    )
                                },
                                onDeleteClick = {
                                    onFeedScheduleEntryUiEvent(
                                        FeedScheduleUiEvent.DeleteFeedScheduleEntry(
                                            feedScheduleEntry
                                        )
                                    )
                                }
                            )
                        }
                    }
                }
            }

            is FeedScheduleSelectionUiState.Loading -> {
                Text(text = stringResource(id = R.string.loading_feed_schedule), fontSize = 12.sp)
            }

            is FeedScheduleSelectionUiState.Error -> {
                Text(text = feedScheduleSelectionUiState.errorMessage, fontSize = 12.sp)
            }

            is FeedScheduleSelectionUiState.Empty -> {
                Text(text = stringResource(id = R.string.no_feed_schedule_entries_found), fontSize = 12.sp)
            }
        }
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    device = "spec:width=600dp,height=400dp,orientation=landscape"
)
@Composable
internal fun FeedScheduleSelectionComposableSuccessDarkPreview() {
    AquariumControllerTheme(darkTheme = true) {
        Surface {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                FeedScheduleSelectionComposable(
                    feedScheduleSelectionUiState = mockFeedScheduleSelectionUiStateSuccess,
                    onFeedScheduleEntryUiEvent = { _: FeedScheduleUiEvent -> return@FeedScheduleSelectionComposable null },
                    onNavigateFromFeed = { _, _, _, _ -> },
                )
            }
        }
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO,
    device = "spec:width=600dp,height=400dp,orientation=landscape"
)
@Composable
internal fun FeedScheduleSelectionComposableSuccessLightPreview() {
    AquariumControllerTheme(darkTheme = false) {
        Surface {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                FeedScheduleSelectionComposable(
                    feedScheduleSelectionUiState = mockFeedScheduleSelectionUiStateSuccess,
                    onFeedScheduleEntryUiEvent = { _: FeedScheduleUiEvent -> return@FeedScheduleSelectionComposable null },
                    onNavigateFromFeed = { _, _, _, _ -> },
                )
            }
        }
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
internal fun FeedScheduleSelectionComposableEmptyDarkPreview() {
    AquariumControllerTheme(darkTheme = true) {
        Surface {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                FeedScheduleSelectionComposable(
                    feedScheduleSelectionUiState = mockFeedScheduleSelectionUiStateEmpty,
                    onFeedScheduleEntryUiEvent = { _: FeedScheduleUiEvent -> return@FeedScheduleSelectionComposable null },
                    onNavigateFromFeed = { _, _, _, _ -> },
                )
            }
        }
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
internal fun FeedScheduleSelectionComposableEmptyLightPreview() {
    AquariumControllerTheme(darkTheme = false) {
        Surface {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                FeedScheduleSelectionComposable(
                    feedScheduleSelectionUiState = mockFeedScheduleSelectionUiStateEmpty,
                    onFeedScheduleEntryUiEvent = { _: FeedScheduleUiEvent -> return@FeedScheduleSelectionComposable null },
                    onNavigateFromFeed = { _, _, _, _ -> },
                )
            }
        }
    }
}