package com.example.aquariumcontroller.feed.history

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.example.aquariumcontroller.MainDropdownMenuComposable
import com.example.aquariumcontroller.MainViewModel
import com.example.aquariumcontroller.R
import com.example.aquariumcontroller.ViewNavigationRoutes
import com.example.aquariumcontroller.ui.theme.AquariumControllerTheme
import kotlinx.coroutines.Dispatchers
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

    val isControllerLoaded = mainViewModel?.isControllerLoaded() ?: false

    val isControllerAddressValid: Boolean = !mainViewModel?.getControllerAddress().isNullOrBlank()

    if ((mainViewModel != null) && (isControllerAddressValid)) {
        val url = when {
            showFullWeek -> "http://" + mainViewModel.getControllerAddress() + ApiUrls.FEED_GET_EVENTS_7D
            else -> "http://" + mainViewModel.getControllerAddress() + ApiUrls.FEED_GET_EVENTS_24H
        }
        val reader = FeedHistoryEventReader(url)

        LaunchedEffect(Unit) {
            withContext(Dispatchers.IO) {
                feedEvents = reader.readBallingEvents()
            }
        }
    }

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

@Preview(showBackground = true)
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

    AquariumControllerTheme {
        FeedHistoryScreen(showFullWeek = false, mockFeedHistoryEvents = mockEvents)
    }
}