package com.laimburggasse.aquariumcontrol.feed.entry

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.laimburggasse.aquariumcontrol.R
import com.laimburggasse.aquariumcontrol.feed.common.FeedNavigationEvent
import com.laimburggasse.aquariumcontrol.feed.schedule.FeedScheduleEntry
import com.laimburggasse.aquariumcontrol.feed.schedule.FeedScheduleEntryComposable
import com.laimburggasse.aquariumcontrol.feed.common.FeedScheduleUiState
import com.laimburggasse.aquariumcontrol.ui.theme.AquariumControllerTheme

@Composable
fun FeedScheduleSelectionComposable(
    feedScheduleUiState: FeedScheduleUiState,
    onNavigateFromFeed: (FeedNavigationEvent, Int?, Boolean?, Long?) -> Unit,
    onFeedSchedulesUiEvent: (FeedScheduleUiEvent) -> Int?,
) {
    // This composable displays a label "Feed Schedule"
    // and a list of feed schedule entries
    Text(text = stringResource(R.string.feed_schedule))
    when (feedScheduleUiState) {
        is FeedScheduleUiState.Success -> {
            val feedScheduleEntries: List<FeedScheduleEntry> = feedScheduleUiState.feedScheduleEntries
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
                LazyColumn(modifier = Modifier.fillMaxWidth().padding(all = 16.dp)) {
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
                                    FeedScheduleUiEvent.DeleteFeedScheduleEntry(feedScheduleEntry)
                                )
                            }
                        )
                    }
                }
            }
        }
        is FeedScheduleUiState.Loading -> {
            Text(text = "Loading...", fontSize = 12.sp)
        }
        is FeedScheduleUiState.Error -> {
            Text(text = feedScheduleUiState.errorMessage, fontSize = 12.sp)
        }
        is FeedScheduleUiState.Empty -> {
            Text(text = "No feed schedule entries found.", fontSize = 12.sp)
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
                    feedScheduleUiState = mockFeedScheduleUiStateSuccess,
                    onFeedSchedulesUiEvent = {event: FeedScheduleUiEvent -> return@FeedScheduleSelectionComposable null },
                    onNavigateFromFeed = { event, id, isNew, millis -> },
                )
            }
        }
    }
}

val mockFeedScheduleUiStateEmpty = FeedScheduleUiState.Empty

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
internal fun FeedScheduleSelectionComposableEmptyPreview() {
    AquariumControllerTheme {
        Surface {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                FeedScheduleSelectionComposable(
                    feedScheduleUiState = mockFeedScheduleUiStateEmpty,
                    onFeedSchedulesUiEvent = {event: FeedScheduleUiEvent -> return@FeedScheduleSelectionComposable null },
                    onNavigateFromFeed = { event, id, isNew, millis -> },
                )
            }
        }
    }
}