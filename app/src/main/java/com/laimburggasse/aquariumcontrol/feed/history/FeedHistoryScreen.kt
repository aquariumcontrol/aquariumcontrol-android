package com.laimburggasse.aquariumcontrol.feed.history

import android.content.res.Configuration
import android.util.Log
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.laimburggasse.aquariumcontrol.MainViewModel
import com.laimburggasse.aquariumcontrol.R
import com.laimburggasse.aquariumcontrol.ViewNavigationRoutes
import com.laimburggasse.aquariumcontrol.lib_composables.ScreenHeadlineComposable
import com.laimburggasse.aquariumcontrol.maindropdownmenu.MainDropdownMenuComposable
import com.laimburggasse.aquariumcontrol.ui.theme.AquariumControllerTheme
import com.laimburggasse.aquariumcontrol.webapi.WebApiUrls
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun FeedHistoryScreen(
    navHostController: NavHostController? = null,
    showFullWeek: Boolean,
    mockFeedHistoryEvents: List<FeedHistoryEvent>? = null,
    mainViewModel: MainViewModel? = null
) {
    // This screen will only display the different feed events.
    // Hence, there is no need to have a view model.
    var feedEvents by remember { mutableStateOf(mockFeedHistoryEvents) }
    var isRefreshing by remember {
        mutableStateOf(
            mockFeedHistoryEvents?.isNotEmpty() == true
        )
    }

    val isControllerLoaded = mainViewModel?.isControllerLoaded() == true
    val coroutineScope = rememberCoroutineScope()
    val isControllerAddressValid: Boolean = !mainViewModel?.getControllerAddress().isNullOrBlank()

    // Use a LaunchedEffect to fetch data on initial composition
    @Suppress("UNCHECKED_CAST")
    LaunchedEffect(key1 = isControllerAddressValid) { // Key on controller address validity
        if (mainViewModel != null && isControllerAddressValid) { // Check for null and validity
            isRefreshing = true
            fetchFeedEvents(
                mainViewModel,
                showFullWeek
            ) { newFeedEvents ->
                feedEvents = newFeedEvents
            } // Pass the setter lambda
            isRefreshing = false
        }
    }

    AquariumControllerTheme {
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
                            modifier = Modifier
                                .fillMaxWidth(),
                            contentAlignment = Alignment.Center,
                            state = rememberPullToRefreshState(),
                            isRefreshing = isRefreshing,
                            onRefresh = {
                                // This lambda will be called when the user pulls to refresh
                                isRefreshing = true
                                // Perform the refresh operation here (e.g., fetch new data)
                                coroutineScope.launch {
                                    if (isControllerAddressValid) {
                                        fetchFeedEvents(
                                            mainViewModel, showFullWeek
                                        ) { newFeedEvents ->
                                            feedEvents = newFeedEvents
                                        } // Pass the setter lambda
                                        delay(timeMillis = 500)
                                    }
                                    isRefreshing = false // Reset the refresh state
                                }
                            },
                            indicator = {
                                if (isRefreshing) {
                                    CircularProgressIndicator(
                                        modifier = Modifier
                                            .size(150.dp),
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
                                if (isControllerLoaded || mockFeedHistoryEvents != null) {
                                    if (isControllerAddressValid || mockFeedHistoryEvents != null) {
                                        if (feedEvents == null) {
                                            Text(
                                                text = stringResource(R.string.loading_feed_events),
                                                style = TextStyle(
                                                    fontSize = 16.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            )
                                        } else if (feedEvents!!.isEmpty()) {
                                            Text(
                                                text = stringResource(R.string.no_feed_events_found),
                                                style = TextStyle(
                                                    fontSize = 16.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            )
                                        } else {
                                            LazyColumn(modifier = Modifier.fillMaxSize()) {
                                                items(feedEvents!!) { ballingEvent ->
                                                    Log.d(
                                                        this::class.simpleName,
                                                        "Feed events found: ${feedEvents!!.size}"
                                                    )
                                                    FeedHistoryEventComposable(ballingEvent)
                                                }
                                            }
                                        }
                                    } else {
                                        Text(
                                            text = stringResource(R.string.invalid_controller_address),
                                            style = TextStyle(
                                                fontSize = 16.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        )
                                    }
                                } else {
                                    Text(
                                        text = stringResource(R.string.controller_not_loaded),
                                        style = TextStyle(
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
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

private suspend fun fetchFeedEvents(
    mainViewModel: MainViewModel,
    showFullWeek: Boolean,
    updateFeedEvents: (List<FeedHistoryEvent>) -> Unit,
) {
    withContext(Dispatchers.IO) {
        val url = when {
            showFullWeek -> "http://" + mainViewModel.getControllerAddress() + WebApiUrls.FEED_GET_EVENTS_7D
            else -> "http://" + mainViewModel.getControllerAddress() + WebApiUrls.FEED_GET_EVENTS_24H
        }
        val reader = FeedHistoryEventReader(url)
        val feedEvents = reader.readFeedEvents()
        if (feedEvents != null) {
            updateFeedEvents(feedEvents) // Call the setter lambda
        }
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
internal fun FeedHistoryScreenPreview() {
    val mockEvents = listOf(
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

    FeedHistoryScreen(showFullWeek = false, mockFeedHistoryEvents = mockEvents)
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
internal fun FeedHistoryScreenEmptyPreview() {
    FeedHistoryScreen(showFullWeek = false, mockFeedHistoryEvents = emptyList())
}
