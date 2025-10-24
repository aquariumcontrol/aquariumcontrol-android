package com.laimburggasse.aquariumcontrol.feed.history

import android.content.res.Configuration
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.laimburggasse.aquariumcontrol.MainScaffold
import com.laimburggasse.aquariumcontrol.MainViewModel
import com.laimburggasse.aquariumcontrol.R
import com.laimburggasse.aquariumcontrol.ViewNavigationRoutes
import com.laimburggasse.aquariumcontrol.feed.entry.mockMainViewModel
import com.laimburggasse.aquariumcontrol.lib_composables.ScreenHeadlineComposable
import com.laimburggasse.aquariumcontrol.webapi.DataFetchResult

@Composable
fun FeedHistoryScreen(
    showFullWeek: Boolean,
    mainViewModel: MainViewModel,
    feedHistoryViewModelFactory: FeedHistoryViewModelFactory,
) {
    // This screen displays:
    // - headline
    // - sub headline (last 7 days or last 24 hours)
    // - list of feed events

    val feedHistoryViewModel: FeedHistoryViewModel = viewModel(
        factory = feedHistoryViewModelFactory
    )

    val isControllerAddressValid: Boolean = !mainViewModel.getControllerAddress().isNullOrBlank()

    val feedHistoryUiState: FeedHistoryUiState by feedHistoryViewModel.feedHistoryUiState.collectAsState()

    LaunchedEffect(key1 = isControllerAddressValid) { // Key on controller address validity
        if (isControllerAddressValid) {
            feedHistoryViewModel.loadData(showFullWeek)
        }
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
                    onRefresh = { feedHistoryViewModel.loadData(showFullWeek) },
                    indicator = {
                        if (feedHistoryUiState is FeedHistoryUiState.Loading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(150.dp),
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
                                    text = "Error loading data: ${(feedHistoryUiState as FeedHistoryUiState.Error).errorMessage}",
                                    style = TextStyle(
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold
                                    ),
                                    modifier = Modifier.weight(1f),
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
                                val feedEvents =
                                    (feedHistoryUiState as FeedHistoryUiState.Success).feedHistoryLogEntries
                                if (LocalInspectionMode.current) {
                                    // In Preview: Render a simple, static Column instead of LazyColumn
                                    Column(modifier = Modifier.fillMaxSize()) {
                                        feedEvents.forEach { feedHistoryEvent ->
                                            FeedHistoryEventComposable(feedHistoryEvent)
                                        }
                                    }
                                } else {
                                    // In App Runtime: Use the performant LazyColumn
                                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                                        items(items = feedEvents) { feedHistoryEvent ->
                                            FeedHistoryEventComposable(feedHistoryEvent)
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

val mockFeedHistoryRepositorySuccess = MockFeedHistoryRepository(
    mockData = DataFetchResult.Success(data = mockFeedHistoryLogEntries)
)

val mockFeedHistoryRepositoryEmpty = MockFeedHistoryRepository(
    mockData = DataFetchResult.Success(data = emptyList())
)

val mockFeedHistoryRepositoryError = MockFeedHistoryRepository(
    mockData = DataFetchResult.Error(exception = null, errorMessage = "Error message")
)


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
        mainViewModel = _root_ide_package_.com.laimburggasse.aquariumcontrol.balling.entry.mockMainViewModel,
        navigationRouteSource = ViewNavigationRoutes.feedAddProfile,
        screenContent = {
            FeedHistoryScreen(
                showFullWeek = false,
                feedHistoryViewModelFactory = FeedHistoryViewModelFactory(
                    feedHistoryRepository = mockFeedHistoryRepositorySuccess
                ),
                mainViewModel = mockMainViewModel,
            )
        },
        onNavigate = {}
    )
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
internal fun FeedHistoryScreenPreviewError() {
    MainScaffold(
        mainViewModel = _root_ide_package_.com.laimburggasse.aquariumcontrol.balling.entry.mockMainViewModel,
        navigationRouteSource = ViewNavigationRoutes.feedAddProfile,
        screenContent = {
            FeedHistoryScreen(
                showFullWeek = false,
                feedHistoryViewModelFactory = FeedHistoryViewModelFactory(
                    feedHistoryRepository = mockFeedHistoryRepositoryError
                ),
                mainViewModel = mockMainViewModel,
            )
        },
        onNavigate = {}
    )
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
internal fun FeedHistoryScreenPreviewEmpty() {
    MainScaffold(
        mainViewModel = _root_ide_package_.com.laimburggasse.aquariumcontrol.balling.entry.mockMainViewModel,
        navigationRouteSource = ViewNavigationRoutes.feedAddProfile,
        screenContent = {
            FeedHistoryScreen(
                showFullWeek = false,
                feedHistoryViewModelFactory = FeedHistoryViewModelFactory(
                    feedHistoryRepository = mockFeedHistoryRepositoryEmpty
                ),
                mainViewModel = mockMainViewModel,
            )
        },
        onNavigate = {}
    )
}

// @todo: Reactivate
/* deactivated until after update of DataFetchResult
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
internal fun FeedHistoryScreenPreviewLoading() {
    FeedHistoryScreen(
        showFullWeek = false,
        feedHistoryViewModel = mockFeedHistoryViewModelLoading,
        mainViewModel = mockMainViewModel
    )
}
*/
