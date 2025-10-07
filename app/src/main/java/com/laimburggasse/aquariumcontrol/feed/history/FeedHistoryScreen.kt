package com.laimburggasse.aquariumcontrol.feed.history

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material3.Surface
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.laimburggasse.aquariumcontrol.MainViewModel
import com.laimburggasse.aquariumcontrol.R
import com.laimburggasse.aquariumcontrol.ViewNavigationRoutes
import com.laimburggasse.aquariumcontrol.feed.mockMainViewModel
import com.laimburggasse.aquariumcontrol.lib_composables.ScreenHeadlineComposable
import com.laimburggasse.aquariumcontrol.maindropdownmenu.MainDropdownMenuComposable
import com.laimburggasse.aquariumcontrol.ui.theme.AquariumControllerTheme

@Composable
fun FeedHistoryScreen(
    navHostController: NavHostController? = null,
    showFullWeek: Boolean,
    mainViewModel: MainViewModel,
    feedHistoryViewModel: FeedHistoryViewModelInterface = viewModel<FeedHistoryViewModel>(),
    darkTheme: Boolean? = null
) {
    // This screen displays:
    // - headline
    // - sub headline (last 7 days or last 24 hours)
    // - list of feed events
    val isControllerAddressValid: Boolean = !mainViewModel.getControllerAddress().isNullOrBlank()
    val isRefreshing: Boolean by remember { feedHistoryViewModel::isRefreshing }
    val feedHistoryUiState: FeedHistoryUiState by remember { feedHistoryViewModel::feedHistoryUiState }


    // Use a LaunchedEffect to fetch data on initial composition
    @Suppress("UNCHECKED_CAST")
    LaunchedEffect(key1 = isControllerAddressValid) { // Key on controller address validity
        if (isControllerAddressValid) {
            feedHistoryViewModel.loadData(mainViewModel, showFullWeek)
        }
    }

    AquariumControllerTheme(darkTheme) {
        Surface(
            color = MaterialTheme.colorScheme.background
        ) {
            Scaffold(
                topBar = { },
                bottomBar = { },
                floatingActionButton = {},
                snackbarHost = { SnackbarHost(remember { SnackbarHostState() })}
            ) { innerPadding ->
                Box {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        ScreenHeadlineComposable(
                            text = stringResource(R.string.feed_log)
                        )

                        val subHeadlineText: String =
                            if (showFullWeek) {
                                stringResource(R.string.feed_events_last_7d)
                            } else {
                                stringResource(R.string.feed_events_last_24h)
                            }
                        Text(
                            text = subHeadlineText,
                        )
                        PullToRefreshBox(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center,
                            state = rememberPullToRefreshState(),
                            isRefreshing = isRefreshing,
                            onRefresh = { feedHistoryViewModel.loadData(mainViewModel, showFullWeek) },
                            indicator = {
                                if (isRefreshing) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(150.dp),
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(16.dp),
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
                                        )                                    }
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
                                        val feedEvents = (feedHistoryUiState as FeedHistoryUiState.Success).feedHistory
                                        if (LocalInspectionMode.current) {
                                            Column(modifier = Modifier.fillMaxSize()) {
                                                feedEvents.forEach { feedHistoryEvent ->
                                                    FeedHistoryEventComposable(feedHistoryEvent)
                                                }
                                            }
                                        }
                                        else {
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
                MainDropdownMenuComposable(
                    navHostController = navHostController,
                    navigationRouteSource = ViewNavigationRoutes.feedHistory,
                    mainViewModel = mainViewModel
                )
            }
        }
    }
}

val mockFeedEvents = listOf(
    FeedHistoryEvent(
        timestamp = "2025-01-18 12:00:00",
        feederOnTime = 20000,
        profileName = "Mock feed profile 1",
        idx = 1
    ),
    FeedHistoryEvent(
        timestamp = "2025-01-18 12:00:00",
        feederOnTime = 10000,
        profileName = "Mock feed profile 2",
        idx = 2
    ),
)

val mockFeedHistoryUiStateSuccess = FeedHistoryUiState.Success(feedHistory = mockFeedEvents)

val mockFeedHistoryViewModelSuccess = MockFeedHistoryViewModel(mockFeedHistoryUiStateSuccess)

val mockFeedHistoryUiStateError = FeedHistoryUiState.Error(errorMessage = "Error message")

val mockFeedHistoryViewModelError = MockFeedHistoryViewModel(mockFeedHistoryUiStateError)

val mockFeedHistoryUiStateLoading = FeedHistoryUiState.Loading

val mockFeedHistoryViewModelLoading = MockFeedHistoryViewModel(mockFeedHistoryUiStateLoading)

val mockFeedHistoryUiStateEmpty = FeedHistoryUiState.Empty

val mockFeedHistoryViewModelEmpty = MockFeedHistoryViewModel(mockFeedHistoryUiStateEmpty)

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
    FeedHistoryScreen(
        showFullWeek = false,
        feedHistoryViewModel = mockFeedHistoryViewModelSuccess,
        mainViewModel = mockMainViewModel
    )
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
internal fun FeedHistoryScreenPreviewError() {
    FeedHistoryScreen(
        showFullWeek = false,
        feedHistoryViewModel = mockFeedHistoryViewModelError,
        mainViewModel = mockMainViewModel
    )
}

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

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
internal fun FeedHistoryScreenPreviewEmpty() {
    FeedHistoryScreen(
        showFullWeek = false,
        feedHistoryViewModel = mockFeedHistoryViewModelEmpty,
        mainViewModel = mockMainViewModel
    )
}
