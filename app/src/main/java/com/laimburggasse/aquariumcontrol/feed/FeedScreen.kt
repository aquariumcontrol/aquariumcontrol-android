package com.laimburggasse.aquariumcontrol.feed

import android.content.res.Configuration
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.laimburggasse.aquariumcontrol.MainViewModel
import com.laimburggasse.aquariumcontrol.R
import com.laimburggasse.aquariumcontrol.ViewNavigationRoutes
import com.laimburggasse.aquariumcontrol.feed.schedule.FeedScheduleEntry
import com.laimburggasse.aquariumcontrol.feed.schedule.FeedScheduleEntryImport
import com.laimburggasse.aquariumcontrol.feed.schedule.FeedScheduleUiState
import com.laimburggasse.aquariumcontrol.feed.schedule.FeedScheduleViewModel
import com.laimburggasse.aquariumcontrol.feed.schedule.FeedScheduleViewModelInterface
import com.laimburggasse.aquariumcontrol.feed.schedule.MockFeedScheduleViewModel
import com.laimburggasse.aquariumcontrol.lib_composables.ScreenHeadlineComposable
import com.laimburggasse.aquariumcontrol.maindropdownmenu.MainDropdownMenuComposable
import com.laimburggasse.aquariumcontrol.ui.theme.AquariumControllerTheme

@Composable
fun FeedScreen(
    navHostController: NavHostController? = null,
    mainViewModel: MainViewModel,
    feedProfilesViewModel: FeedProfilesViewModelInterface = viewModel<FeedProfilesViewModel>(),
    feedScheduleViewModel: FeedScheduleViewModelInterface = viewModel<FeedScheduleViewModel>(),
    darkTheme: Boolean? = null
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

    var isLoadingProfiles: Boolean by remember {
        mutableStateOf(value = feedProfilesViewModel.feedProfilesUiState is FeedProfilesUiState.Loading)
    }
    var isLoadingScheduleEntries: Boolean by remember {
        mutableStateOf(value = feedScheduleViewModel.feedScheduleUiState is FeedScheduleUiState.Loading)
    }

    fun loadData() {
        if (mainViewModel.getControllerAddress() != null) {
            feedScheduleViewModel.loadData(mainViewModel)
            feedProfilesViewModel.loadData(mainViewModel)
        }
    }

    LaunchedEffect(Unit) {
        loadData()
    }

    AquariumControllerTheme(darkTheme) {
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
                            if (isLoadingProfiles || isLoadingScheduleEntries) {
                                CircularProgressIndicator(
                                    modifier = Modifier
                                        .size(150.dp),
                                    color = MaterialTheme.colorScheme.primary
                                )
                            } else {
                                Row {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        FeedProfileSelectionComposable(
                                            navHostController = navHostController,
                                            myFeedProfilesViewModel = feedProfilesViewModel,
                                            modifier = Modifier.align(Alignment.End),
                                            mainViewModel = mainViewModel,
                                            feedScheduleViewModel = feedScheduleViewModel
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
                        }
                        else -> {
                            FeedHistorySelectionButtonsComposable(
                                navHostController = navHostController,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            if (isLoadingProfiles || isLoadingScheduleEntries) {
                                CircularProgressIndicator(
                                    modifier = Modifier
                                        .size(150.dp),
                                    color = MaterialTheme.colorScheme.primary
                                )
                            } else {
                                FeedProfileSelectionComposable(
                                    modifier = Modifier.align(Alignment.End),
                                    myFeedProfilesViewModel = feedProfilesViewModel,
                                    mainViewModel = mainViewModel,
                                    navHostController = navHostController,
                                    feedScheduleViewModel = feedScheduleViewModel
                                )
                                FeedScheduleSelectionComposable(
                                    myFeedScheduleViewModel = feedScheduleViewModel,
                                    mainViewModel = mainViewModel
                                )
                            }
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
fun FeedScreenPreviewSuccess() {
    val mockFeedProfiles: List<FeedProfile> = createMockFeedProfiles()
    val mockFeedScheduleEntries: List<FeedScheduleEntry> = createMockFeedSchedule()

    val mockFeedProfilesViewModel = MockFeedProfilesViewModel(
        feedProfilesUiState = FeedProfilesUiState.Success(feedProfiles = mockFeedProfiles),
        isRefreshing = false,
        feedProfiles = mockFeedProfiles
    )

    val mockFeedScheduleViewModel = MockFeedScheduleViewModel(
        feedScheduleUiState = FeedScheduleUiState.Success(feedScheduleEntries = mockFeedScheduleEntries),
        isRefreshing = false
    )

    mockMainViewModel.setMyFeedScheduleViewModel(mockFeedScheduleViewModel)
    mockMainViewModel.setMyFeedProfileViewModel(mockFeedProfilesViewModel)

    FeedScreen(
        mainViewModel = mockMainViewModel,
        feedProfilesViewModel = mockFeedProfilesViewModel,
        feedScheduleViewModel = mockFeedScheduleViewModel,
    )
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
fun FeedScreenPreviewError() {
    val mockFeedProfilesViewModel = MockFeedProfilesViewModel(
        feedProfilesUiState = FeedProfilesUiState.Error(errorMessage = "Error loading feed profiles"),
        isRefreshing = false,
        feedProfiles = emptyList()
    )

    val mockFeedScheduleViewModel = MockFeedScheduleViewModel(
        feedScheduleUiState = FeedScheduleUiState.Error(errorMessage = "Error loading feed schedule"),
        isRefreshing = false
    )

    mockMainViewModel.setMyFeedScheduleViewModel(mockFeedScheduleViewModel)
    mockMainViewModel.setMyFeedProfileViewModel(mockFeedProfilesViewModel)

    FeedScreen(
        mainViewModel = mockMainViewModel,
        feedProfilesViewModel = mockFeedProfilesViewModel,
        feedScheduleViewModel = mockFeedScheduleViewModel,
    )
}