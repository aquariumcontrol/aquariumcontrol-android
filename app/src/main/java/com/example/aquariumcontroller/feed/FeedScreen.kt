package com.example.aquariumcontroller.feed

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.aquariumcontroller.ApiUrls
import com.example.aquariumcontroller.MainDropdownMenuComposable
import com.example.aquariumcontroller.MainViewModel
import com.example.aquariumcontroller.R
import com.example.aquariumcontroller.ViewNavigationRoutes
import com.example.aquariumcontroller.feed.schedule.FeedScheduleEntry
import com.example.aquariumcontroller.feed.schedule.FeedScheduleEntryComposable
import com.example.aquariumcontroller.feed.schedule.FeedScheduleEntryImport
import com.example.aquariumcontroller.feed.schedule.FeedScheduleEntryReader
import com.example.aquariumcontroller.feed.schedule.toFeedScheduleEntryList
import com.example.aquariumcontroller.ui.theme.AquariumControllerTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun FeedScreen(
    navHostController: NavHostController? = null,
    mockFeedProfiles: List<FeedProfile>? = null,
    mockFeedScheduleEntries: List<FeedScheduleEntry>? = null,
    mainViewModel: MainViewModel? = null
) {
    // This screen displays
    // - a button for visualizing all feed events of the last 24h
    // - a button for visualizing all feed events of the last 7 days
    // - feed profiles
    // - feed schedule entries
    // - floating action button for adding feed profile

    var feedProfiles by remember { mutableStateOf(mockFeedProfiles) }

    var feedScheduleEntries by remember { mutableStateOf(mockFeedScheduleEntries) }

    if (mainViewModel != null) {
        val profileUrl =
            "http://" + mainViewModel.getControllerAddress() + ApiUrls.FEED_GET_PROFILES
        val profileReader = FeedProfileReader(profileUrl)

        val scheduleUrl =
            "http://" + mainViewModel.getControllerAddress() + ApiUrls.FEED_GET_SCHEDULE
        val scheduleReader = FeedScheduleEntryReader(scheduleUrl)

        LaunchedEffect(Unit) {
            withContext(Dispatchers.IO) {
                val feedProfileImports = profileReader.readFeedProfiles()
                if (feedProfileImports != null) {
                    feedProfiles = feedProfileImports.toFeedProfileList()
                }

                val feedScheduleEntryImports = scheduleReader.readFeedScheduleEntries()
                if (feedScheduleEntryImports != null) {
                    feedScheduleEntries = feedScheduleEntryImports.toFeedScheduleEntryList()
                }
            }
        }
    }

    AquariumControllerTheme {
        // A surface container using the 'background' color from the theme
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                MainDropdownMenuComposable(
                    navHostController = navHostController,
                    navigationRouteSource = ViewNavigationRoutes.feed,
                    mainViewModel = mainViewModel
                )
                Text(
                    text = stringResource(id = R.string.FeedControl),
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(16.dp),
                )
                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    onClick = {
                        navHostController?.navigate(ViewNavigationRoutes.feedHistory24h)
                    },
                ) {
                    Text(
                        text = stringResource(id = R.string.ViewFeedEvents24h),
                        fontSize = 16.sp
                    )
                }
                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    onClick = {
                        navHostController?.navigate(ViewNavigationRoutes.feedHistory7d)
                    },
                ) {
                    Text(
                        text = stringResource(id = R.string.ViewFeedEvents7d),
                        fontSize = 16.sp
                    )
                }
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        if (feedProfiles != null) {
                            Spacer(modifier = Modifier.height(24.dp))
                            Text(text = "Feed Profiles")
                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            ) {
                                items(feedProfiles!!) { feedProfile ->
                                    FeedProfileSummaryComposable(
                                        navHostController = navHostController,
                                        mainViewModel = mainViewModel,
                                        feedProfile = feedProfile
                                    )
                                }
                            }
                        }
                        if (feedScheduleEntries != null) {
                            Spacer(modifier = Modifier.height(24.dp))
                            Text(text = "Feed Schedule")
                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            ) {
                                items(feedScheduleEntries!!) { feedScheduleEntry ->
                                    FeedScheduleEntryComposable(
                                        feedScheduleEntry = feedScheduleEntry
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun FeedScreenPreview() {
    // create mock feed profiles
    val mockFeedProfileImport1 = FeedProfileImport()
    mockFeedProfileImport1.profileId = 1
    mockFeedProfileImport1.profileName = "Test Profile 1"

    val mockFeedProfileImport2 = FeedProfileImport()
    mockFeedProfileImport2.profileId = 2
    mockFeedProfileImport2.profileName = "Test Profile 2"

    val mockFeedProfile1 = FeedProfile(mockFeedProfileImport1)
    val mockFeedProfile2 = FeedProfile(mockFeedProfileImport2)

    val mockFeedProfiles = listOf(mockFeedProfile1, mockFeedProfile2)

    // create mock feed schedule entries
    val mockFeedScheduleEntry1 = FeedScheduleEntryImport(
        timestampString = "2023-09-01 12:00:00",
        profileName = "Test Profile 1",
        isDaily = 1
    ).transformToFeedScheduleEntry()

    val mockFeedScheduleEntry2 = FeedScheduleEntryImport(
        timestampString = "2023-09-01 13:00:00",
        profileName = "Test Profile 2",
        isDaily = 0
    ).transformToFeedScheduleEntry()

    val mockFeedScheduleEntries = listOf(mockFeedScheduleEntry1, mockFeedScheduleEntry2)

    AquariumControllerTheme {
        FeedScreen(
            mockFeedProfiles = mockFeedProfiles,
            mockFeedScheduleEntries = mockFeedScheduleEntries
        )
    }
}