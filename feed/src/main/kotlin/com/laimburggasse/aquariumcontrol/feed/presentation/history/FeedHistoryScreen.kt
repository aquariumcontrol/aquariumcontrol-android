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

package com.laimburggasse.aquariumcontrol.feed.presentation.history

import android.content.res.Configuration
import android.widget.Toast
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.LoadStates
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import com.laimburggasse.aquariumcontrol.common.presentation.MainScaffold
import com.laimburggasse.aquariumcontrol.common.presentation.ScreenHeadlineComposable
import com.laimburggasse.aquariumcontrol.common.presentation.ViewNavigationRoutes
import com.laimburggasse.aquariumcontrol.feed.R
import com.laimburggasse.aquariumcontrol.feed.domain.model.FeedHistoryLogEntry
import com.laimburggasse.aquariumcontrol.feed.testing.FeedMockData.mockFeedHistoryLogEntries
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

import com.laimburggasse.aquariumcontrol.common.presentation.GenericHistoryScreen

@Composable
fun FeedHistoryScreen(
    feedHistoryFlow: Flow<PagingData<FeedHistoryLogEntry>>,
    previewData: List<FeedHistoryLogEntry>? = null
) {
    GenericHistoryScreen(
        historyFlow = feedHistoryFlow,
        headline = stringResource(id = R.string.feed_log),
        noEventsMessage = stringResource(R.string.no_feed_events_found),
        entryComposable = { feedHistoryLogEntry ->
            FeedHistoryLogEntryComposable(feedHistoryLogEntry)
        },
        previewData = previewData
    )
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    device = "spec:width=600dp,height=400dp,orientation=landscape"
)
@Composable
internal fun FeedHistoryScreenSuccessDarkPreview() {
    MainScaffold(
        isControllerLoaded = true,
        navigationRouteSource = ViewNavigationRoutes.feedHistory,
        screenContent = {
            FeedHistoryScreen(
                feedHistoryFlow = flowOf(PagingData.from(
                    mockFeedHistoryLogEntries,
                    sourceLoadStates = LoadStates(
                        refresh = LoadState.NotLoading(true),
                        prepend = LoadState.NotLoading(true),
                        append = LoadState.NotLoading(true)
                    )
                )),
                previewData = mockFeedHistoryLogEntries
            )
        },
        onNavigate = {},
        darkTheme = isSystemInDarkTheme()
    )
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO,
    device = "spec:width=600dp,height=400dp,orientation=landscape"
)
@Composable
internal fun FeedHistoryScreenSuccessLightPreview() {
    MainScaffold(
        isControllerLoaded = true,
        navigationRouteSource = ViewNavigationRoutes.feedHistory,
        screenContent = {
            FeedHistoryScreen(
                feedHistoryFlow = flowOf(PagingData.from(
                    emptyList(),
                    sourceLoadStates = LoadStates(
                        refresh = LoadState.NotLoading(true),
                        prepend = LoadState.NotLoading(true),
                        append = LoadState.NotLoading(true)
                    )
                )),
                previewData = mockFeedHistoryLogEntries
            )
        },
        onNavigate = {},
        darkTheme = false
    )
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
internal fun FeedHistoryScreenLoadingDarkPreview() {
    MainScaffold(
        isControllerLoaded = true,
        navigationRouteSource = ViewNavigationRoutes.feedHistory,
        screenContent = {
            FeedHistoryScreen(
                feedHistoryFlow = flowOf(PagingData.from(emptyList(), LoadStates(
                    refresh = LoadState.Loading,
                    prepend = LoadState.NotLoading(true),
                    append = LoadState.NotLoading(true)
                ))),
            )
        },
        onNavigate = {},
        darkTheme = true
    )
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
internal fun FeedHistoryScreenLoadingLightPreview() {
    MainScaffold(
        isControllerLoaded = true,
        navigationRouteSource = ViewNavigationRoutes.feedHistory,
        screenContent = {
            FeedHistoryScreen(
                feedHistoryFlow = flowOf(PagingData.from(emptyList(), LoadStates(
                    refresh = LoadState.Loading,
                    prepend = LoadState.NotLoading(true),
                    append = LoadState.NotLoading(true)
                ))),
            )
        },
        onNavigate = {},
        darkTheme = false
    )
}