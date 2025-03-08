package com.example.aquariumcontroller.feed

import android.content.res.Configuration
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.aquariumcontroller.MainViewModel
import com.example.aquariumcontroller.R
import com.example.aquariumcontroller.ViewNavigationRoutes
import com.example.aquariumcontroller.feed.schedule.FeedScheduleEntry
import com.example.aquariumcontroller.feed.schedule.FeedScheduleEntryImport
import com.example.aquariumcontroller.feed.schedule.FeedScheduleViewModel
import com.example.aquariumcontroller.feed.schedule.FeedScheduleViewModelFactory
import com.example.aquariumcontroller.maindropdownmenu.MainDropdownMenuComposable
import com.example.aquariumcontroller.lib_composables.ScreenHeadlineComposable
import com.example.aquariumcontroller.ui.theme.AquariumControllerTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun FeedScreen(
    navHostController: NavHostController? = null,
    mockFeedProfiles: List<FeedProfile>? = null,
    mockFeedScheduleEntries: List<FeedScheduleEntry>? = null,
    mainViewModel: MainViewModel? = null,
    myFeedProfileViewModel: FeedProfileViewModel? = null
) {
    // This screen displays
    // - headline
    // - a button for visualizing all feed events of the last 24h
    // - a button for visualizing all feed events of the last 7 days
    // - subhead line for feed profiles
    // - feed profiles
    // - subhead line for feed schedule
    // - feed schedule entries
    // - floating action button for adding feed profile

    val configuration = LocalConfiguration.current

    // instantiate view model with null pointer except if mock data is provided
    val feedScheduleViewModel: FeedScheduleViewModel = viewModel(
        factory = FeedScheduleViewModelFactory(feedScheduleEntries = mockFeedScheduleEntries)
    )

    // instantiate a new view model if there is no view model provided as parameter
    val feedProfileViewModel: FeedProfileViewModel = myFeedProfileViewModel
        ?: viewModel(
            factory = FeedProfileViewModelFactory(feedProfiles = mockFeedProfiles)
        )

    if (mainViewModel != null) {
        LaunchedEffect(Unit) {
            withContext(Dispatchers.IO) {
                if (mainViewModel.getControllerAddress() != null) {
                    feedScheduleViewModel.readFeedScheduleEntries(mainViewModel.getControllerAddress()!!)
                    feedProfileViewModel.readFeedProfiles(mainViewModel.getControllerAddress()!!)
                }
            }
        }
    }

    AquariumControllerTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Box {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    ScreenHeadlineComposable(
                        stringResource(id = R.string.feed_control)
                    )

                    when (configuration.orientation) {
                        Configuration.ORIENTATION_LANDSCAPE -> {
                            Row {
                                FeedHistorySelectionButtonsComposable(navHostController = navHostController)
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            Row {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    FeedProfileSelectionComposable(
                                        navHostController = navHostController,
                                        myFeedProfileViewModel = feedProfileViewModel,
                                        modifier = Modifier.align(Alignment.End)
                                    )
                                }
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    FeedScheduleSelectionComposable(
                                        myFeedScheduleViewModel = feedScheduleViewModel,
                                        mainViewModel = mainViewModel
                                    )
                                }
                            }
                        }

                        else -> {
                            FeedHistorySelectionButtonsComposable(
                                navHostController = navHostController,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            FeedProfileSelectionComposable(
                                navHostController = navHostController,
                                myFeedProfileViewModel = feedProfileViewModel,
                                modifier = Modifier.align(Alignment.End)
                            )
                            FeedScheduleSelectionComposable(
                                myFeedScheduleViewModel = feedScheduleViewModel,
                                mainViewModel = mainViewModel
                            )
                        }
                    }
                }
                MainDropdownMenuComposable(
                    navHostController = navHostController,
                    navigationRouteSource = ViewNavigationRoutes.feed,
                    mainViewModel = mainViewModel
                )
            }
        }
    }
}

fun createMockFeedProfiles(): List<FeedProfile> {
    val mockFeedProfileImport1 = FeedProfileImport()
    mockFeedProfileImport1.profileId = 1
    mockFeedProfileImport1.profileName = "Test Profile 1"

    val mockFeedProfileImport2 = FeedProfileImport()
    mockFeedProfileImport2.profileId = 2
    mockFeedProfileImport2.profileName = "Test Profile 2"

    val mockFeedProfile1 = FeedProfile(mockFeedProfileImport1)
    val mockFeedProfile2 = FeedProfile(mockFeedProfileImport2)

    return listOf(mockFeedProfile1, mockFeedProfile2)
}

fun createMockFeedSchedule(): List<FeedScheduleEntry> {
    val mockFeedProfileImport1 = FeedProfileImport()
    mockFeedProfileImport1.profileId = 1
    mockFeedProfileImport1.profileName = "Test Profile 1"

    val mockFeedProfileImport2 = FeedProfileImport()
    mockFeedProfileImport2.profileId = 2
    mockFeedProfileImport2.profileName = "Test Profile 2"

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

    return listOf(mockFeedScheduleEntry1, mockFeedScheduleEntry2)
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    device = "spec:width=800dp,height=400dp,orientation=landscape"
)
@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO,
    device = "spec:width=800dp,height=400dp,orientation=landscape"
)
@Composable
fun FeedScreenPreview() {
    FeedScreen(
        mockFeedProfiles = createMockFeedProfiles(),
        mockFeedScheduleEntries = createMockFeedSchedule()
    )
}