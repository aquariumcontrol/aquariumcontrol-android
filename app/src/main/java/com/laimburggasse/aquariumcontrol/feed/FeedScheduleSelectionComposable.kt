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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.laimburggasse.aquariumcontrol.MainViewModel
import com.laimburggasse.aquariumcontrol.MockMainViewModel
import com.laimburggasse.aquariumcontrol.R
import com.laimburggasse.aquariumcontrol.feed.schedule.FeedScheduleEntryComposable
import com.laimburggasse.aquariumcontrol.feed.schedule.FeedScheduleUiState
import com.laimburggasse.aquariumcontrol.feed.schedule.FeedScheduleViewModelInterface
import com.laimburggasse.aquariumcontrol.feed.schedule.MockFeedScheduleViewModel
import com.laimburggasse.aquariumcontrol.ui.theme.AquariumControllerTheme

@Composable
fun FeedScheduleSelectionComposable(
    myFeedScheduleViewModel: FeedScheduleViewModelInterface,
    mainViewModel: MainViewModel
) {
    // This composable displays a label "Feed Schedule"
    // and a list of feed schedule entries

    val feedScheduleUiState: FeedScheduleUiState = myFeedScheduleViewModel.feedScheduleUiState

    Text(text = stringResource(R.string.feed_schedule))
    when (feedScheduleUiState) {
        is FeedScheduleUiState.Success -> {
            val feedScheduleEntries = feedScheduleUiState.feedScheduleEntries

            if (LocalInspectionMode.current) {
                // In Preview: Render a simple, static Column instead of LazyColumn
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
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
                        .padding(16.dp)
                ) {
                    items(items = feedScheduleEntries) { feedScheduleEntry ->
                        FeedScheduleEntryComposable(
                            feedScheduleEntry = feedScheduleEntry,
                            onEditClick = {
                                mainViewModel.updateSelectedFeedScheduleEntry(feedScheduleEntry)
                            },
                            onDeleteClick = {
                                mainViewModel.updateSelectedFeedScheduleEntry(null)
                                myFeedScheduleViewModel.deleteFeedScheduleEntry(
                                    feedScheduleEntry = feedScheduleEntry,
                                    mainViewModel = mainViewModel
                                )
                            }
                        )
                    }
                }
            }
        }
        is FeedScheduleUiState.Loading -> {
            Text(text = "Loading...")
        }
        is FeedScheduleUiState.Error -> {
            Text(text = feedScheduleUiState.errorMessage)
        }
        is FeedScheduleUiState.Empty -> {
            Text(text = "No feed schedule entries found.")
        }
    }
}

val mockFeedScheduleEntries = createMockFeedSchedule()

val mockFeedScheduleViewModelSuccess = MockFeedScheduleViewModel(
    feedScheduleUiState = FeedScheduleUiState.Success(feedScheduleEntries = mockFeedScheduleEntries),
    isRefreshing = false
)

val mockMainViewModel = MockMainViewModel()

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
                    mainViewModel = mockMainViewModel,
                    myFeedScheduleViewModel = mockFeedScheduleViewModelSuccess
                )
            }
        }
    }
}