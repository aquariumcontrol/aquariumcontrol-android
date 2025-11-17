package com.laimburggasse.aquariumcontrol.feed.presentation.edit

import android.content.res.Configuration
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.laimburggasse.aquariumcontrol.common.presentation.MainScaffold
import com.laimburggasse.aquariumcontrol.common.presentation.ScreenHeadlineComposable
import com.laimburggasse.aquariumcontrol.common.presentation.ViewNavigationRoutes
import com.laimburggasse.aquariumcontrol.feed.R
import com.laimburggasse.aquariumcontrol.feed.testing.FeedMockData.mockFeedProfileEditUiStateFailure
import com.laimburggasse.aquariumcontrol.feed.testing.FeedMockData.mockFeedProfileEditUiStateSuccess

@Composable
fun FeedProfileEditScreen(
    uiState: FeedProfileEditUiState = FeedProfileEditUiState(),
    onFeedProfileEditUiEvent: (FeedProfileEditUiEvent) -> Unit,
) {
    // This screen displays
    // - a graphic visualizing the feed profile
    // - button for triggering update of feed profile
    // - button for triggering delete of feed profile
    // - button for scheduling the execution of the feed profile
    // - a hierarchical menu for editing the feed profile

    val configuration: Configuration = LocalConfiguration.current

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.verticalScroll(state = rememberScrollState())
    ) {
        ScreenHeadlineComposable(text = stringResource(id = R.string.feed_profile_editing))

        if (uiState.feedProfile == null) {
            Text(text = "Feed profile not found")
        }
        else {
            FeedProfileDisplayComposable(
                lineDataFeeder = uiState.lineDataFeeder,
                lineDataSkimmer = uiState.lineDataSkimmer,
                lineDataMainPump1 = uiState.lineDataMainPump1,
                lineDataMainPump2 = uiState.lineDataMainPump2,
                lineDataAuxPump1 = uiState.lineDataAuxPump1,
                lineDataAuxPump2 = uiState.lineDataAuxPump2,
                totalDuration = uiState.totalDuration
            )

            FeedProfileDisplayLegendComposable()

            when (configuration.orientation) {
                Configuration.ORIENTATION_LANDSCAPE -> {
                    Row {
                        FeedProfileEditButtonsComposable(onFeedProfileEditUiEvent = onFeedProfileEditUiEvent)
                    }
                }
                else -> {
                    FeedProfileEditButtonsComposable(
                        modifier = Modifier.fillMaxWidth(),
                        onFeedProfileEditUiEvent = onFeedProfileEditUiEvent
                    )
                }
            }

            FeedProfileEditMenuComposable(
                itemsToRender = FeedProfileEditMenuItems.phases, // all items shall be rendered
                feedProfile = uiState.feedProfile,
                isExpanded = uiState.expandedItemIds,
                onFeedProfileEditUiEvent = onFeedProfileEditUiEvent
            )
        }
    }
}



@Preview(showBackground = true, heightDp = 1100, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(showBackground = true, heightDp = 1100, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    device = "spec:width=600dp,height=1100dp,orientation=landscape"
)
@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO,
    device = "spec:width=600dp,height=1100dp,orientation=landscape"
)
@Composable
internal fun FeedProfileEditScreenSuccessPreview() {
    MainScaffold(
        isControllerLoaded = true,
        navigationRouteSource = ViewNavigationRoutes.feedEdit,
        screenContent = {
            FeedProfileEditScreen(
                uiState = mockFeedProfileEditUiStateSuccess,
                onFeedProfileEditUiEvent = {}
            )
        },
        onNavigate = {},
        darkTheme = isSystemInDarkTheme()
    )
}

@Preview(showBackground = true, heightDp = 1100, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(showBackground = true, heightDp = 1100, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
internal fun FeedProfileEditScreenFailurePreview() {
    MainScaffold(
        isControllerLoaded = true,
        navigationRouteSource = ViewNavigationRoutes.feedEdit,
        screenContent = {
            FeedProfileEditScreen(
                uiState = mockFeedProfileEditUiStateFailure,
                onFeedProfileEditUiEvent = {}
            )
        },
        onNavigate = {},
        darkTheme = isSystemInDarkTheme()
    )
}