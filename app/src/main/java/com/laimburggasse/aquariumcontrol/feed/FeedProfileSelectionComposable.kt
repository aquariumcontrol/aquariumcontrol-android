package com.laimburggasse.aquariumcontrol.feed

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.laimburggasse.aquariumcontrol.MainViewModel
import com.laimburggasse.aquariumcontrol.R
import com.laimburggasse.aquariumcontrol.ViewNavigationRoutes
import com.laimburggasse.aquariumcontrol.feed.schedule.FeedScheduleViewModel
import com.laimburggasse.aquariumcontrol.ui.theme.AquariumControllerTheme

@Composable
fun FeedProfileSelectionComposable(
    modifier: Modifier? = Modifier,
    mockFeedProfiles: List<FeedProfile>? = null,
    myFeedProfilesViewModel: FeedProfilesViewModel? = null,
    mainViewModel: MainViewModel? = null,
    navHostController: NavHostController? = null,
    feedScheduleViewModel: FeedScheduleViewModel? = null,
) {
    // This composable displays a label "Feed Profiles"
    // and a list of feed profiles.

    // instantiate a new view model if there is no view model provided as parameter
    val feedProfilesViewModel: FeedProfilesViewModel = myFeedProfilesViewModel ?: viewModel(
        factory = FeedProfilesViewModelFactory(feedProfiles = mockFeedProfiles)
    )

    Text(text = stringResource(R.string.feed_profiles))

    val columnModifier = Modifier.fillMaxWidth().padding(8.dp)
    if (LocalInspectionMode.current) {
        Column(modifier = columnModifier) {
            feedProfilesViewModel.getFeedProfiles().forEach { feedProfile ->
                FeedProfileSummaryComposable(
                    feedProfile = feedProfile,
                    onEditClick = {},
                    onScheduleClick = {},
                    onDeleteClick = {}
                )
            }
        }
    }
    else {
        LazyColumn(modifier = columnModifier) {
            items(feedProfilesViewModel.getFeedProfiles()) { feedProfile ->
                FeedProfileSummaryComposable(
                    feedProfile = feedProfile,
                    onEditClick = {
                        mainViewModel?.updateSelectedFeedProfile(feedProfile)
                        navHostController?.navigate(route = ViewNavigationRoutes.feedEdit)
                    },
                    onScheduleClick = {
                        mainViewModel?.updateSelectedFeedProfile(feedProfile)
                        mainViewModel?.updateSelectedFeedScheduleEntry(null)
                        mainViewModel?.setMyFeedScheduleViewModel(feedScheduleViewModel)
                        navHostController?.navigate(route = ViewNavigationRoutes.feedSchedule)
                    },
                    onDeleteClick = {
                        feedProfilesViewModel.deleteFeedProfile(feedProfile)
                    }
                )
            }
        }
    }
    FloatingActionButton(
        onClick = {
            navHostController?.navigate(ViewNavigationRoutes.feedAddProfile)
        },
        modifier = modifier?.then(
            Modifier
                .padding(horizontal = 8.dp)
        ) ?: Modifier
    ) {
        Icon(
            imageVector = Icons.Filled.Add,
            contentDescription = "Create New Profile"
        )
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
internal fun FeedProfileSelectionComposablePreview() {
    AquariumControllerTheme {
        Surface {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                FeedProfileSelectionComposable(
                    mockFeedProfiles = createMockFeedProfiles(),
                )
            }
        }
    }
}