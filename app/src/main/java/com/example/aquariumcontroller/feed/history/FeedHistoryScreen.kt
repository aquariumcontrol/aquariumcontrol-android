package com.example.aquariumcontroller.feed.history

import android.content.res.Configuration
import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import com.example.aquariumcontroller.ApiUrls
import com.example.aquariumcontroller.lib_composables.MainDropdownMenuComposable
import com.example.aquariumcontroller.MainViewModel
import com.example.aquariumcontroller.R
import com.example.aquariumcontroller.ViewNavigationRoutes
import com.example.aquariumcontroller.ui.theme.AquariumControllerTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterialApi::class)
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
            mockFeedHistoryEvents?.isNotEmpty() ?: false
        )
    }

    val isControllerLoaded = mainViewModel?.isControllerLoaded() ?: false
    val coroutineScope = rememberCoroutineScope()
    val isControllerAddressValid: Boolean = !mainViewModel?.getControllerAddress().isNullOrBlank()

    // Use a LaunchedEffect to fetch data on initial composition
    @Suppress("UNCHECKED_CAST")
    LaunchedEffect(key1 = isControllerAddressValid) { // Key on controller address validity
        if (mainViewModel != null && isControllerAddressValid) { // Check for null and validity
            fetchFeedEvents(
                mainViewModel,
                showFullWeek
            ) { newFeedEvents ->
                feedEvents = newFeedEvents as List<FeedHistoryEvent>
            } // Pass the setter lambda
        }
    }

    @Suppress("UNCHECKED_CAST")
    val pullRefreshState = rememberPullRefreshState(
        refreshing = isRefreshing,
        onRefresh = {
            // This lambda will be called when the user pulls to refresh
            isRefreshing = true
            // Perform the refresh operation here (e.g., fetch new data)
            coroutineScope.launch {
                if (isControllerAddressValid) {
                    fetchFeedEvents(
                        mainViewModel!!, showFullWeek
                    ) { newFeedEvents ->
                        feedEvents = newFeedEvents as List<FeedHistoryEvent>
                    } // Pass the setter lambda
                    delay(500)
                    isRefreshing = false // Reset the refresh state
                }
            }
        }
    )

    AquariumControllerTheme {
        Surface(
            color = MaterialTheme.colorScheme.background
        ) {
            Scaffold(
            ) { innerPadding ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    MainDropdownMenuComposable(
                        navHostController = navHostController,
                        navigationRouteSource = ViewNavigationRoutes.balling,
                        mainViewModel = mainViewModel
                    )
                    val headlineText: String =
                        if (showFullWeek) {
                            stringResource(R.string.FeedEventsLast7Days)
                        } else {
                            stringResource(R.string.FeedEventsLast24h)
                        }
                    Text(
                        text = headlineText,
                        style = MaterialTheme.typography.headlineSmall,
                    )
                    Box(
                        modifier = Modifier
                            .pullRefresh(pullRefreshState)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isRefreshing) {  // Show only while isLoading is true
                            CircularProgressIndicator(
                                modifier = Modifier
                                    .size(150.dp),
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp)
                        ) {
                            if (isControllerLoaded || mockFeedHistoryEvents != null) {
                                if (isControllerAddressValid || mockFeedHistoryEvents != null) {
                                    if (feedEvents == null) {
                                        Text(
                                            text = "Loading feed events...",
                                            style = TextStyle(
                                                fontSize = 16.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        )
                                    } else if (feedEvents!!.isEmpty()) {
                                        Text(
                                            text = "No feed events found.",
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
                                        text = "Invalid controller address",
                                        style = TextStyle(
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                }
                            } else {
                                Text(
                                    text = "Controller not loaded.",
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
        }
    }
}

private suspend fun fetchFeedEvents(
    mainViewModel: MainViewModel,
    showFullWeek: Boolean,
    updateFeedEvents: (Any) -> Unit,
) {
    withContext(Dispatchers.IO) {
        val url = when {
            showFullWeek -> "http://" + mainViewModel.getControllerAddress() + ApiUrls.FEED_GET_EVENTS_7D
            else -> "http://" + mainViewModel.getControllerAddress() + ApiUrls.FEED_GET_EVENTS_24H
        }
        val reader = FeedHistoryEventReader(url)
        val feedEvents = reader.readFeedEvents()
        if (feedEvents != null) {
            updateFeedEvents(feedEvents) // Call the setter lambda
        }
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun FeedHistoryPreview() {
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