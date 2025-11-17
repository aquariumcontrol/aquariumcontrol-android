package com.laimburggasse.aquariumcontrol.feed.presentation.entry

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
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
import com.laimburggasse.aquariumcontrol.feed.R
import com.laimburggasse.aquariumcontrol.feed.domain.model.FeedProfile
import com.laimburggasse.aquariumcontrol.feed.presentation.navigation.FeedNavigationEvent
import com.laimburggasse.aquariumcontrol.common.presentation.theme.AquariumControllerTheme

@Composable
fun FeedProfileSelectionComposable(
    modifier: Modifier? = Modifier,
    feedProfilesUiState: FeedProfilesUiState,
    onNavigateFromFeed: (FeedNavigationEvent, Int?, Boolean?, Long?) -> Unit,
    onFeedProfilesUiEvent: (FeedProfilesUiEvent) -> Unit
) {
    // This composable displays a label "Feed Profiles"
    // and a list of feed profiles.

    Text(text = stringResource(R.string.feed_profiles))

    val columnModifier: Modifier = Modifier.padding(all = 8.dp)
    PullToRefreshBox(
        modifier = Modifier.fillMaxWidth(),
        state = rememberPullToRefreshState(),
        isRefreshing = feedProfilesUiState == FeedProfilesUiState.Loading,
        contentAlignment = Alignment.Center,
        onRefresh = { onFeedProfilesUiEvent(FeedProfilesUiEvent.Refresh) },
        indicator = {
            if (feedProfilesUiState == FeedProfilesUiState.Loading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(size = 150.dp),
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    ) {
        when (feedProfilesUiState) {
            is FeedProfilesUiState.Loading -> {
                Text(text = stringResource(id = R.string.loading_feed_profiles))
            }

            is FeedProfilesUiState.Error -> {
                val errorMessage: String = feedProfilesUiState.errorMessage
                Text(text = "Error: $errorMessage")
            }

            is FeedProfilesUiState.Success -> {
                val feedProfiles: List<FeedProfile> = feedProfilesUiState.feedProfiles
                if (LocalInspectionMode.current) {
                    feedProfiles.forEach { feedProfile ->
                        FeedProfileSummaryComposable(
                            feedProfile,
                            onEditClick = {},
                            onScheduleClick = {},
                            onDeleteClick = {}
                        )
                    }
                } else {
                    LazyColumn(modifier = columnModifier) {
                        items(items = feedProfiles) { feedProfile ->
                            FeedProfileSummaryComposable(
                                feedProfile,
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
                                    onFeedProfilesUiEvent(
                                        FeedProfilesUiEvent.DeleteFeedProfile(feedProfile)
                                    )
                                }
                            )
                        }
                    }
                }
            }
            is FeedProfilesUiState.Empty -> {
                Text(text = stringResource(id = R.string.there_are_no_feed_profiles_create_a_new_one))
            }
        }
    }
    FloatingActionButton(
        onClick = { onNavigateFromFeed(FeedNavigationEvent.ToFeedProfileAdd, null, null, null) },
        modifier = modifier?.then(other = Modifier.padding(horizontal = 8.dp)) ?: Modifier
    ) {
        Icon(
            imageVector = Icons.Filled.Add,
            contentDescription = stringResource(id = R.string.add_feed_profile)
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
                    feedProfilesUiState = mockFeedProfilesUiStateSuccess,
                    onNavigateFromFeed = { event, id, isNew, millis -> },
                    onFeedProfilesUiEvent = { event -> }
                )
            }
        }
    }
}