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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.laimburggasse.aquariumcontrol.R
import com.laimburggasse.aquariumcontrol.ui.theme.AquariumControllerTheme

@Composable
fun FeedProfileSelectionComposable(
    modifier: Modifier? = Modifier,
    feedProfilesViewModelFactory: FeedProfilesViewModelFactory,
    onNavigateFromFeed: (FeedNavigationEvent, Int?, Boolean?, Long?) -> Unit
) {
    // This composable displays a label "Feed Profiles"
    // and a list of feed profiles.

    val feedProfilesViewModel: FeedProfilesViewModel = viewModel(
        factory = feedProfilesViewModelFactory
    )
    val feedProfilesUiState: FeedProfilesUiState by feedProfilesViewModel.feedProfilesUiState.collectAsState()

    Text(text = stringResource(R.string.feed_profiles))

    val columnModifier: Modifier = Modifier
        .fillMaxWidth()
        .padding(8.dp)
    when (feedProfilesUiState) {
        is FeedProfilesUiState.Loading -> {
            Text(text = "Loading...")
        }
        is FeedProfilesUiState.Error -> {
            val errorMessage: String = (feedProfilesUiState as FeedProfilesUiState.Error).errorMessage
            Text(text = "Error: $errorMessage")
        }
        is FeedProfilesUiState.Success -> {
            val feedProfiles: List<FeedProfile> =
                (feedProfilesUiState as FeedProfilesUiState.Success).feedProfiles
            if (LocalInspectionMode.current) {
                val feedProfiles: List<FeedProfile> =
                    (feedProfilesUiState as FeedProfilesUiState.Success).feedProfiles
                feedProfiles.forEach { feedProfile ->
                    FeedProfileSummaryComposable(
                        feedProfileId = feedProfile.profileID,
                        feedProfilesViewModelFactory = feedProfilesViewModelFactory,
                        onEditClick = {},
                        onScheduleClick = {},
                        onDeleteClick = {}
                    )
                }
            }
            else {
                LazyColumn(modifier = columnModifier) {
                    items(items = feedProfiles) { feedProfile ->
                        FeedProfileSummaryComposable(
                            feedProfileId = feedProfile.profileID,
                            feedProfilesViewModelFactory = feedProfilesViewModelFactory,
                            onEditClick = {
                                onNavigateFromFeed(
                                    FeedNavigationEvent.ToFeedProfileEdit,
                                    feedProfile.profileID,
                                    null,
                                    null
                                )
                            },
                            onScheduleClick = {
                                onNavigateFromFeed(
                                    FeedNavigationEvent.ToFeedScheduling,
                                    feedProfile.profileID,
                                    true,
                                    0
                                )
                            },
                            onDeleteClick = {
                                feedProfilesViewModel.deleteFeedProfile(feedProfile)
                            }
                        )
                    }
                }
            }
        }
        is FeedProfilesUiState.Empty -> {
            Text(text = "Empty")
        }
    }
    FloatingActionButton(
        onClick = { onNavigateFromFeed(FeedNavigationEvent.ToFeedProfileAdd, null, null, null) },
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
                    feedProfilesViewModelFactory = mockFeedProfilesViewModelFactory,
                    onNavigateFromFeed = { event, id, isNew, millis -> }
                )
            }
        }
    }
}