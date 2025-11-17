package com.laimburggasse.aquariumcontrol.feed.presentation.history

import android.content.res.Configuration
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.laimburggasse.aquariumcontrol.common.presentation.MainScaffold
import com.laimburggasse.aquariumcontrol.feed.R
import com.laimburggasse.aquariumcontrol.common.presentation.ViewNavigationRoutes
import com.laimburggasse.aquariumcontrol.feed.domain.model.FeedHistoryLogEntry
import com.laimburggasse.aquariumcontrol.common.presentation.ScreenHeadlineComposable

@Composable
fun FeedHistoryScreen(
    showFullWeek: Boolean,
    feedHistoryUiState: FeedHistoryUiState,
    onFeedHistoryEvent: (FeedHistoryUiEvent) -> Unit
) {
    // This screen displays:
    // - headline
    // - sub headline (last 7 days or last 24 hours)
    // - list of feed events

    LaunchedEffect(key1 = Unit) {
        onFeedHistoryEvent(FeedHistoryUiEvent.SetRange(showFullWeek))
    }
    Column(
        modifier = Modifier.fillMaxSize().padding(all = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ScreenHeadlineComposable(text = stringResource(R.string.feed_log))

        val subHeadlineText: String =
            if (showFullWeek) {
                stringResource(R.string.feed_events_last_7d)
            } else {
                stringResource(R.string.feed_events_last_24h)
            }
        Text(text = subHeadlineText)

        Scaffold(
            topBar = {},
            bottomBar = {},
            floatingActionButton = {},
            snackbarHost = { SnackbarHost(hostState = remember { SnackbarHostState() })}
        ) { innerPadding ->
            Column(
                modifier = Modifier.fillMaxSize().padding(paddingValues = innerPadding),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                PullToRefreshBox(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center,
                    state = rememberPullToRefreshState(),
                    isRefreshing = feedHistoryUiState is FeedHistoryUiState.Loading,
                    onRefresh = { onFeedHistoryEvent(FeedHistoryUiEvent.Refresh)},
                    indicator = {
                        if (feedHistoryUiState is FeedHistoryUiState.Loading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(size = 150.dp),
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                ) {
                    Row(
                        modifier = Modifier.fillMaxSize().padding(all = 16.dp),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        when (feedHistoryUiState) {
                            is FeedHistoryUiState.Empty -> {
                                Text(
                                    text = stringResource(R.string.no_feed_events_found),
                                    style = TextStyle(
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }

                            is FeedHistoryUiState.Error -> {
                                Text(
                                    text = stringResource(id = R.string.error_loading_feed_data) +
                                            feedHistoryUiState.errorMessage,
                                    style = TextStyle(
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold
                                    ),
                                    modifier = Modifier.weight(weight = 1f),
                                    textAlign = TextAlign.Center
                                )
                            }

                            is FeedHistoryUiState.Loading -> {
                                Text(
                                    text = stringResource(R.string.loading_feed_events),
                                    style = TextStyle(
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }

                            is FeedHistoryUiState.Success -> {
                                val feedEvents: List<FeedHistoryLogEntry> = feedHistoryUiState.feedHistoryLogEntries
                                if (LocalInspectionMode.current) {
                                    // In Preview: Render a simple, static Column instead of LazyColumn
                                    Column(modifier = Modifier.fillMaxSize()) {
                                        feedEvents.forEach { feedHistoryLogEntry ->
                                            FeedHistoryEventComposable(feedHistoryLogEntry)
                                        }
                                    }
                                } else {
                                    // In App Runtime: Use the performant LazyColumn
                                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                                        items(items = feedEvents) { feedHistoryLogEntry ->
                                            FeedHistoryEventComposable(feedHistoryLogEntry)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

val mockFeedHistoryLogEntries = listOf(
    FeedHistoryLogEntry(
        timestamp = "2025-01-18 12:00:00",
        feederOnTime = 20000,
        profileName = "Mock feed profile 1",
        idx = 1
    ),
    FeedHistoryLogEntry(
        timestamp = "2025-01-18 12:00:00",
        feederOnTime = 10000,
        profileName = "Mock feed profile 2",
        idx = 2
    ),
)

val mockFeedHistoryUiStateSuccess = FeedHistoryUiState.Success(
    feedHistoryLogEntries = mockFeedHistoryLogEntries
)

val mockFeedHistoryUiStateError = FeedHistoryUiState.Error(
    errorMessage = "Mock Error message"
)

val mockFeedHistoryUiStateEmpty = FeedHistoryUiState.Empty

val mockFeedHistoryUiStateLoading = FeedHistoryUiState.Loading

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
internal fun FeedHistoryScreenPreviewSuccess() {
    MainScaffold(
        isControllerLoaded = true,
        navigationRouteSource = ViewNavigationRoutes.feedHistory24h,
        screenContent = {
            FeedHistoryScreen(
                showFullWeek = false,
                feedHistoryUiState = mockFeedHistoryUiStateSuccess,
                onFeedHistoryEvent = {}
            )
        },
        onNavigate = {},
        darkTheme = isSystemInDarkTheme()
    )
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
internal fun FeedHistoryScreenPreviewError() {
    MainScaffold(
        isControllerLoaded = true,
        navigationRouteSource = ViewNavigationRoutes.feedAddProfile,
        screenContent = {
            FeedHistoryScreen(
                showFullWeek = false,
                feedHistoryUiState = mockFeedHistoryUiStateError,
                onFeedHistoryEvent = {}
            )
        },
        onNavigate = {},
        darkTheme = isSystemInDarkTheme()
    )
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
internal fun FeedHistoryScreenPreviewEmpty() {
    MainScaffold(
        isControllerLoaded = true,
        navigationRouteSource = ViewNavigationRoutes.feedAddProfile,
        screenContent = {
            FeedHistoryScreen(
                showFullWeek = false,
                feedHistoryUiState = mockFeedHistoryUiStateEmpty,
                onFeedHistoryEvent = {}
            )
        },
        onNavigate = {},
        darkTheme = isSystemInDarkTheme()
    )
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
internal fun FeedHistoryScreenPreviewLoading() {
    MainScaffold(
        isControllerLoaded = true,
        navigationRouteSource = ViewNavigationRoutes.feedAddProfile,
        screenContent = {
            FeedHistoryScreen(
                showFullWeek = false,
                feedHistoryUiState = mockFeedHistoryUiStateLoading,
                onFeedHistoryEvent = {}
            )
        },
        onNavigate = {},
        darkTheme = isSystemInDarkTheme()
    )
}
