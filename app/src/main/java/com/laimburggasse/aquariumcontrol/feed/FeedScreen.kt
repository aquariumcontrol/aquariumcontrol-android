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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.laimburggasse.aquariumcontrol.MainViewModel
import com.laimburggasse.aquariumcontrol.MockMainViewModel
import com.laimburggasse.aquariumcontrol.R
import com.laimburggasse.aquariumcontrol.ViewNavigationRoutes
import com.laimburggasse.aquariumcontrol.feed.schedule.FeedScheduleEntry
import com.laimburggasse.aquariumcontrol.feed.schedule.FeedScheduleEntryImport
import com.laimburggasse.aquariumcontrol.feed.schedule.FeedScheduleUiState
import com.laimburggasse.aquariumcontrol.feed.schedule.FeedScheduleViewModel
import com.laimburggasse.aquariumcontrol.feed.schedule.FeedScheduleViewModelFactory
import com.laimburggasse.aquariumcontrol.feed.schedule.MockFeedScheduleRepository
import com.laimburggasse.aquariumcontrol.feed.schedule.mockMainViewModel
import com.laimburggasse.aquariumcontrol.lib_composables.ScreenHeadlineComposable
import com.laimburggasse.aquariumcontrol.maindropdownmenu.MainDropdownMenuComposable
import com.laimburggasse.aquariumcontrol.ui.theme.AquariumControllerTheme
import com.laimburggasse.aquariumcontrol.webapi.DataFetchResult

@Composable
fun FeedScreen(
    navHostController: NavHostController? = null,
    mainViewModel: MainViewModel,
    feedScheduleViewModelFactory: FeedScheduleViewModelFactory,
    feedProfilesViewModelFactory: FeedProfilesViewModelFactory,
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

    val configuration: Configuration = LocalConfiguration.current

    val feedProfilesViewModel: FeedProfilesViewModel = viewModel(factory = feedProfilesViewModelFactory)
    val feedScheduleViewModel: FeedScheduleViewModel = viewModel(factory = feedScheduleViewModelFactory)
    val feedProfilesUiState: FeedProfilesUiState by feedProfilesViewModel.feedProfilesUiState.collectAsState()
    val feedScheduleUiState: FeedScheduleUiState by feedScheduleViewModel.feedScheduleUiState.collectAsState()

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
                            if (feedProfilesUiState is FeedProfilesUiState.Loading
                                || feedScheduleUiState is FeedScheduleUiState.Loading) {
                                CircularProgressIndicator(
                                    modifier = Modifier
                                        .size(size = 150.dp),
                                    color = MaterialTheme.colorScheme.primary
                                )
                            } else {
                                Row {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        FeedProfileSelectionComposable(
                                            modifier = Modifier.align(Alignment.End),
                                            navHostController,
                                            feedProfilesViewModelFactory,
                                        )
                                    }
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        FeedScheduleSelectionComposable(
                                            navHostController,
                                            feedScheduleViewModel,
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
                            if (feedProfilesUiState is FeedProfilesUiState.Loading
                                || feedScheduleUiState is FeedScheduleUiState.Loading) {
                                CircularProgressIndicator(
                                    modifier = Modifier
                                        .size(150.dp),
                                    color = MaterialTheme.colorScheme.primary
                                )
                            } else {
                                FeedProfileSelectionComposable(
                                    modifier = Modifier.align(Alignment.End),
                                    navHostController,
                                    feedProfilesViewModelFactory,
                                )
                                FeedScheduleSelectionComposable(
                                    navHostController,
                                    feedScheduleViewModel,
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

val mockFeedScheduleRepository = MockFeedScheduleRepository(
    mockFeedScheduleEntries = DataFetchResult.Success(data = createMockFeedSchedule())
)

val mockFeedProfilesRepository = MockFeedProfilesRepository(
    mockFeedProfiles = DataFetchResult.Success(data = createMockFeedProfiles())
)

val mockFeedScheduleViewModelFactory = FeedScheduleViewModelFactory(
    feedScheduleRepository = mockFeedScheduleRepository,
    feedProfilesRepository = mockFeedProfilesRepository
)


val mockFeedProfilesViewModelFactory = FeedProfilesViewModelFactory(
    feedProfilesRepository = mockFeedProfilesRepository
)

val mockMainViewModel = MockMainViewModel()

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
    FeedScreen(
        feedProfilesViewModelFactory = mockFeedProfilesViewModelFactory,
        mainViewModel = mockMainViewModel,
        feedScheduleViewModelFactory = mockFeedScheduleViewModelFactory,
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
    val mockFeedProfilesRepositoryError = MockFeedProfilesRepository(
        mockFeedProfiles = DataFetchResult.Error(
            exception = null,
            errorMessage = "Error loading feed profiles"
        )
    )

    FeedScreen(
        mainViewModel = mockMainViewModel,
        feedProfilesViewModelFactory = FeedProfilesViewModelFactory(
            feedProfilesRepository = mockFeedProfilesRepositoryError
        ),
        feedScheduleViewModelFactory = mockFeedScheduleViewModelFactory,
    )
}