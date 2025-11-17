package com.laimburggasse.aquariumcontrol.feed.presentation.entry

import android.content.res.Configuration
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.laimburggasse.aquariumcontrol.common.presentation.MainScaffold
import com.laimburggasse.aquariumcontrol.common.presentation.ScreenHeadlineComposable
import com.laimburggasse.aquariumcontrol.common.presentation.ViewNavigationRoutes
import com.laimburggasse.aquariumcontrol.feed.R
import com.laimburggasse.aquariumcontrol.feed.presentation.navigation.FeedNavigationEvent
import com.laimburggasse.aquariumcontrol.feed.testing.FeedMockData.mockFeedProfilesUiStateError
import com.laimburggasse.aquariumcontrol.feed.testing.FeedMockData.mockFeedProfilesUiStateSuccess
import com.laimburggasse.aquariumcontrol.feed.testing.FeedMockData.mockFeedScheduleSelectionUiStateError
import com.laimburggasse.aquariumcontrol.feed.testing.FeedMockData.mockFeedScheduleSelectionUiStateSuccess

@Composable
fun FeedScreen(
    feedProfilesUiState: FeedProfilesUiState,
    feedScheduleSelectionUiState: FeedScheduleSelectionUiState,
    onFeedProfilesUiEvent: (FeedProfilesUiEvent) -> Unit,
    onFeedSchedulesUiEvent: (FeedScheduleUiEvent) -> Int?,
    onNavigateFromFeed: (FeedNavigationEvent, Int?, Boolean?, Long?) -> Unit
) {
    // This screen displays
    // - headline
    // - a button for visualizing all feed events of the last 24h
    // - a button for visualizing all feed events of the last 7 days
    // - subhead line for feed profiles
    // - feed profiles
    // - subhead line for feed schedule
    // - feed schedule entries
    // - floating action button for adding feed profile

    val configuration: Configuration = LocalConfiguration.current

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        ScreenHeadlineComposable(text = stringResource(id = R.string.feed_control))

        when (configuration.orientation) {
            Configuration.ORIENTATION_LANDSCAPE -> {
                Row {
                    FeedHistorySelectionButtonsComposable(onNavigateFromFeed = onNavigateFromFeed)
                }
                Spacer(modifier = Modifier.height(height = 16.dp))
                if (feedProfilesUiState is FeedProfilesUiState.Loading
                    || feedScheduleSelectionUiState is FeedScheduleSelectionUiState.Loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(size = 150.dp),
                        color = MaterialTheme.colorScheme.primary
                    )
                } else {
                    Row {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.weight(weight = 1f)
                        ) {
                            FeedProfileSelectionComposable(
                                modifier = Modifier.align(Alignment.End),
                                feedProfilesUiState,
                                onNavigateFromFeed,
                                onFeedProfilesUiEvent
                            )
                        }
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.weight(weight = 1f)
                        ) {
                            FeedScheduleSelectionComposable(
                                feedScheduleSelectionUiState,
                                onNavigateFromFeed,
                                onFeedSchedulesUiEvent
                            )
                        }
                    }
                }
            }
            else -> {
                FeedHistorySelectionButtonsComposable(
                    modifier = Modifier.fillMaxWidth(),
                    onNavigateFromFeed
                )
                Spacer(modifier = Modifier.height(height = 16.dp))
                if (feedProfilesUiState is FeedProfilesUiState.Loading
                    || feedScheduleSelectionUiState is FeedScheduleSelectionUiState.Loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(size = 150.dp),
                        color = MaterialTheme.colorScheme.primary
                    )
                } else {
                    FeedProfileSelectionComposable(
                        modifier = Modifier.align(Alignment.End),
                        feedProfilesUiState,
                        onNavigateFromFeed,
                        onFeedProfilesUiEvent
                    )
                    FeedScheduleSelectionComposable(
                        feedScheduleSelectionUiState,
                        onNavigateFromFeed,
                        onFeedSchedulesUiEvent
                    )
                }
            }
        }
    }
}


@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    device = "spec:width=800dp,height=400dp,orientation=landscape"
)
@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO,
    device = "spec:width=800dp,height=400dp,orientation=landscape"
)
@Composable
fun FeedScreenPreviewSuccess() {
    MainScaffold(
        isControllerLoaded = true,
        navigationRouteSource = ViewNavigationRoutes.feed,
        screenContent = {
            FeedScreen(
                feedProfilesUiState = mockFeedProfilesUiStateSuccess,
                feedScheduleSelectionUiState = mockFeedScheduleSelectionUiStateSuccess,
                onNavigateFromFeed = { event, id, isNew, millis -> },
                onFeedProfilesUiEvent = {},
                onFeedSchedulesUiEvent = { event -> return@FeedScreen null }
            )
        },
        onNavigate = {},
        darkTheme = isSystemInDarkTheme()
    )
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    device = "spec:width=800dp,height=400dp,orientation=landscape"
)
@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO,
    device = "spec:width=800dp,height=400dp,orientation=landscape"
)
@Composable
fun FeedScreenPreviewError() {
    MainScaffold(
        isControllerLoaded = true,
        navigationRouteSource = ViewNavigationRoutes.feed,
        screenContent = {
            FeedScreen(
                feedProfilesUiState = mockFeedProfilesUiStateError,
                feedScheduleSelectionUiState = mockFeedScheduleSelectionUiStateError,
                onNavigateFromFeed = { event, id, isNew, millis -> },
                onFeedProfilesUiEvent = {},
                onFeedSchedulesUiEvent = { event -> return@FeedScreen null }
            )
        },
        onNavigate = {},
        darkTheme = isSystemInDarkTheme()
    )
}