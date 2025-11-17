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
import com.laimburggasse.aquariumcontrol.feed.testing.FeedMockData.mockFeedScheduleSelectionUiStateEmpty
import com.laimburggasse.aquariumcontrol.feed.testing.FeedMockData.mockFeedScheduleSelectionUiStateSuccess

@Composable
fun FeedScheduleSelectionComposable(
    feedScheduleSelectionUiState: FeedScheduleSelectionUiState,
    onNavigateFromFeed: (FeedNavigationEvent, Int?, Boolean?, Long?) -> Unit,
    onFeedSchedulesUiEvent: (FeedScheduleUiEvent) -> Int?,
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
        onRefresh = { onFeedSchedulesUiEvent(FeedScheduleUiEvent.Refresh) },
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
                                        feedScheduleEntry.timestamp.time
                                    )
                                },
                                onDeleteClick = {
                                    onFeedSchedulesUiEvent(
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
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    device = "spec:width=600dp,height=400dp,orientation=landscape"
)
@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO,
    device = "spec:width=600dp,height=400dp,orientation=landscape"
)
@Composable
internal fun FeedScheduleSelectionComposableSuccessPreview() {
    AquariumControllerTheme {
        Surface {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                FeedScheduleSelectionComposable(
                    feedScheduleSelectionUiState = mockFeedScheduleSelectionUiStateSuccess,
                    onFeedSchedulesUiEvent = {event: FeedScheduleUiEvent -> return@FeedScheduleSelectionComposable null },
                    onNavigateFromFeed = { event, id, isNew, millis -> },
                )
            }
        }
    }
}


@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
internal fun FeedScheduleSelectionComposableEmptyPreview() {
    AquariumControllerTheme {
        Surface {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                FeedScheduleSelectionComposable(
                    feedScheduleSelectionUiState = mockFeedScheduleSelectionUiStateEmpty,
                    onFeedSchedulesUiEvent = {event: FeedScheduleUiEvent -> return@FeedScheduleSelectionComposable null },
                    onNavigateFromFeed = { event, id, isNew, millis -> },
                )
            }
        }
    }
}