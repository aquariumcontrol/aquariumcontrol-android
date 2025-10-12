package com.laimburggasse.aquariumcontrol.feed

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.laimburggasse.aquariumcontrol.R
import com.laimburggasse.aquariumcontrol.ViewNavigationRoutes
import com.laimburggasse.aquariumcontrol.feed.schedule.FeedScheduleEntry
import com.laimburggasse.aquariumcontrol.feed.schedule.FeedScheduleEntryComposable
import com.laimburggasse.aquariumcontrol.feed.schedule.FeedScheduleUiState
import com.laimburggasse.aquariumcontrol.feed.schedule.FeedScheduleViewModel
import com.laimburggasse.aquariumcontrol.ui.theme.AquariumControllerTheme

@Composable
fun FeedScheduleSelectionComposable(
    navHostController: NavHostController? = null,
    feedScheduleViewModel: FeedScheduleViewModel
) {
    // This composable displays a label "Feed Schedule"
    // and a list of feed schedule entries
    val feedScheduleUiState: FeedScheduleUiState by feedScheduleViewModel.feedScheduleUiState.collectAsState()

    Text(text = stringResource(R.string.feed_schedule))
    when (feedScheduleUiState) {
        is FeedScheduleUiState.Success -> {
            val feedScheduleEntries: List<FeedScheduleEntry> =
                (feedScheduleUiState as FeedScheduleUiState.Success).feedScheduleEntries

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
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(all = 16.dp)
                ) {
                    items(items = feedScheduleEntries) { feedScheduleEntry ->
                        val feedProfileId: Int? = feedScheduleViewModel.getFeedProfileIdByName(
                            feedScheduleEntry.profileName
                        )
                        if (feedProfileId != null) {
                            FeedScheduleEntryComposable(
                                feedScheduleEntry = feedScheduleEntry,
                                onEditClick = {
                                    navHostController?.navigate(
                                        route = ViewNavigationRoutes.feedSchedule(
                                            feedProfileId = feedProfileId,
                                            isNewScheduleEntry = true,
                                            millisSinceEpoch = feedScheduleEntry.timestamp.time
                                        )
                                    )
                                },
                                onDeleteClick = {
                                    feedScheduleViewModel.deleteFeedScheduleEntry(
                                        feedScheduleEntry = feedScheduleEntry,
                                    )
                                }
                            )
                        }
                        else {
                            // @todo: handle error case (e.g. Toast)
                        }
                    }
                }
            }
        }
        is FeedScheduleUiState.Loading -> {
            Text(text = "Loading...")
        }
        is FeedScheduleUiState.Error -> {
            Text(text = (feedScheduleUiState as FeedScheduleUiState.Error).errorMessage)
        }
        is FeedScheduleUiState.Empty -> {
            Text(text = "No feed schedule entries found.")
        }
    }
}

val mockFeedScheduleViewModelSuccess = mockFeedScheduleViewModelFactory.create(
   FeedScheduleViewModel::class.java
)

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
internal fun FeedScheduleSelectionComposablePreview() {
    AquariumControllerTheme {
        Surface {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                FeedScheduleSelectionComposable(
                    feedScheduleViewModel = mockFeedScheduleViewModelSuccess,
                )
            }
        }
    }
}