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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.laimburggasse.aquariumcontrol.MainViewModel
import com.laimburggasse.aquariumcontrol.R
import com.laimburggasse.aquariumcontrol.feed.schedule.FeedScheduleEntry
import com.laimburggasse.aquariumcontrol.feed.schedule.FeedScheduleEntryComposable
import com.laimburggasse.aquariumcontrol.feed.schedule.FeedScheduleViewModel
import com.laimburggasse.aquariumcontrol.feed.schedule.FeedScheduleViewModelFactory
import com.laimburggasse.aquariumcontrol.ui.theme.AquariumControllerTheme

@Composable
fun FeedScheduleSelectionComposable(
    myFeedScheduleViewModel: FeedScheduleViewModel? = null,
    mockFeedScheduleEntries: List<FeedScheduleEntry>? = null,
    mainViewModel: MainViewModel? = null
) {
    // This composable displays a label "Feed Schedule"
    // and a list of feed schedule entries

    // use view model provided as parameter or create a new one
    val feedScheduleViewModel: FeedScheduleViewModel = myFeedScheduleViewModel ?: viewModel(
        factory = FeedScheduleViewModelFactory(feedScheduleEntries = mockFeedScheduleEntries)
    )

    Text(text = stringResource(R.string.feed_schedule))
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        items(feedScheduleViewModel.getFeedScheduleEntries()) { feedScheduleEntry ->
            FeedScheduleEntryComposable(
                mainViewModel = mainViewModel,
                feedScheduleEntry = feedScheduleEntry,
                feedScheduleViewModel = feedScheduleViewModel
            )
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
internal fun FeedScheduleSelectionComposablePreview() {
    AquariumControllerTheme {
        Surface {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                FeedScheduleSelectionComposable(
                    mockFeedScheduleEntries = createMockFeedSchedule()
                )
            }
        }
    }
}