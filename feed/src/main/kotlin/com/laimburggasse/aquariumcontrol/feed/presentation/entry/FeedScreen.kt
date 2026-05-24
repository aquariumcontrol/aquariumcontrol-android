/*
 * Copyright 2025 Uwe Martin
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.laimburggasse.aquariumcontrol.feed.presentation.entry

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.laimburggasse.aquariumcontrol.common.presentation.MainScaffold
import com.laimburggasse.aquariumcontrol.common.presentation.ScreenHeadlineComposable
import com.laimburggasse.aquariumcontrol.common.presentation.ViewNavigationRoutes
import com.laimburggasse.aquariumcontrol.feed.R
import com.laimburggasse.aquariumcontrol.feed.presentation.navigation.FeedNavigationEvent
import com.laimburggasse.aquariumcontrol.feed.preview.FeedPreviewDataImpl.mockFeedProfilesUiStateError
import com.laimburggasse.aquariumcontrol.feed.preview.FeedPreviewDataImpl.mockFeedProfilesUiStateSuccess
import com.laimburggasse.aquariumcontrol.feed.preview.FeedPreviewDataImpl.mockFeedScheduleSelectionUiStateError
import com.laimburggasse.aquariumcontrol.feed.preview.FeedPreviewDataImpl.mockFeedScheduleSelectionUiStateSuccess

@Composable
fun FeedScreen(
    feedProfilesUiState: FeedProfilesUiState,
    feedScheduleSelectionUiState: FeedScheduleSelectionUiState,
    onFeedProfilesUiEvent: (FeedProfilesUiEvent) -> Unit,
    onFeedScheduleEntryUiEvent: (FeedScheduleUiEvent) -> Int?,
    onNavigateFromFeed: (FeedNavigationEvent, Int?, Boolean?, Long?) -> Unit
) {
    // This screen displays
    // - headline
    // - a button for navigating to history screen
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
                    Button(
                        modifier = Modifier.padding(horizontal = 8.dp).fillMaxWidth(),
                        onClick = { onNavigateFromFeed(FeedNavigationEvent.ToFeedHistory, null, null, null) },
                    ) {
                        Text(
                            text = stringResource(id = R.string.view_feed_events),
                            fontSize = 16.sp
                        )
                    }
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
                                onFeedScheduleEntryUiEvent
                            )
                        }
                    }
                }
            }
            else -> {
                Button(
                    modifier = Modifier.padding(horizontal = 8.dp).fillMaxWidth(),
                    onClick = { onNavigateFromFeed(FeedNavigationEvent.ToFeedHistory, null, null, null) },
                ) {
                    Text(
                        text = stringResource(id = R.string.view_feed_events),
                        fontSize = 16.sp
                    )
                }
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
                        onFeedScheduleEntryUiEvent
                    )
                }
            }
        }
    }
}


@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    device = "spec:width=800dp,height=400dp,orientation=landscape"
)
@Composable
fun FeedScreenSuccessDarkPreview() {
    MainScaffold(
        isControllerLoaded = true,
        navigationRouteSource = ViewNavigationRoutes.feed,
        screenContent = {
            FeedScreen(
                feedProfilesUiState = mockFeedProfilesUiStateSuccess,
                feedScheduleSelectionUiState = mockFeedScheduleSelectionUiStateSuccess,
                onNavigateFromFeed = { _, _, _, _ -> },
                onFeedProfilesUiEvent = {},
                onFeedScheduleEntryUiEvent = { _ -> return@FeedScreen null }
            )
        },
        onNavigate = {},
        darkTheme = true
    )
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO,
    device = "spec:width=800dp,height=400dp,orientation=landscape"
)
@Composable
fun FeedScreenSuccessLightPreview() {
    MainScaffold(
        isControllerLoaded = true,
        navigationRouteSource = ViewNavigationRoutes.feed,
        screenContent = {
            FeedScreen(
                feedProfilesUiState = mockFeedProfilesUiStateSuccess,
                feedScheduleSelectionUiState = mockFeedScheduleSelectionUiStateSuccess,
                onNavigateFromFeed = { _, _, _, _ -> },
                onFeedProfilesUiEvent = {},
                onFeedScheduleEntryUiEvent = { _ -> return@FeedScreen null }
            )
        },
        onNavigate = {},
        darkTheme = false
    )
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    device = "spec:width=800dp,height=400dp,orientation=landscape"
)
@Composable
fun FeedScreenErrorDarkPreview() {
    MainScaffold(
        isControllerLoaded = true,
        navigationRouteSource = ViewNavigationRoutes.feed,
        screenContent = {
            FeedScreen(
                feedProfilesUiState = mockFeedProfilesUiStateError,
                feedScheduleSelectionUiState = mockFeedScheduleSelectionUiStateError,
                onNavigateFromFeed = { _, _, _, _ -> },
                onFeedProfilesUiEvent = {},
                onFeedScheduleEntryUiEvent = { _ -> return@FeedScreen null }
            )
        },
        onNavigate = {},
        darkTheme = true
    )
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO,
    device = "spec:width=800dp,height=400dp,orientation=landscape"
)
@Composable
fun FeedScreenErrorLightPreview() {
    MainScaffold(
        isControllerLoaded = true,
        navigationRouteSource = ViewNavigationRoutes.feed,
        screenContent = {
            FeedScreen(
                feedProfilesUiState = mockFeedProfilesUiStateError,
                feedScheduleSelectionUiState = mockFeedScheduleSelectionUiStateError,
                onNavigateFromFeed = { _, _, _, _ -> },
                onFeedProfilesUiEvent = {},
                onFeedScheduleEntryUiEvent = { _ -> return@FeedScreen null }
            )
        },
        onNavigate = {},
        darkTheme = false
    )
}