package com.laimburggasse.aquariumcontrol.feed.edit

import android.content.res.Configuration
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.github.mikephil.charting.data.LineData
import com.laimburggasse.aquariumcontrol.MainViewModel
import com.laimburggasse.aquariumcontrol.R
import com.laimburggasse.aquariumcontrol.ViewNavigationRoutes
import com.laimburggasse.aquariumcontrol.feed.FeedProfile
import com.laimburggasse.aquariumcontrol.feed.FeedProfileImport
import com.laimburggasse.aquariumcontrol.feed.mockFeedProfilesRepository
import com.laimburggasse.aquariumcontrol.feed.mockMainViewModel
import com.laimburggasse.aquariumcontrol.lib_composables.ScreenHeadlineComposable
import com.laimburggasse.aquariumcontrol.maindropdownmenu.MainDropdownMenuComposable
import com.laimburggasse.aquariumcontrol.ui.theme.AquariumControllerTheme

fun createFeedProfileEditMenuItems(currentFeedProfile: FeedProfile): List<FeedProfileEditMenuItem> {
    return List(10) { index ->
        FeedProfileEditMenuItem(
            id = index,
            level = 1,
            text = "Phase ${index + 1}",
            isToggleButton = false,
            isNumericInput = false,
            isChecked = false,
            duration = 0.0,
            children = mutableListOf(
                FeedProfileEditMenuItem(
                    id = 0,
                    level = 2,
                    text = "Pause",
                    isToggleButton = false,
                    isNumericInput = false,
                    isChecked = false,
                    duration = 0.0,
                    phaseType = FeedProfileEditPhaseType.PAUSE,
                    children = mutableListOf(
                        FeedProfileEditMenuItem(
                            id = 0,
                            level = 3,
                            text = "Duration",
                            isToggleButton = false,
                            phaseType = FeedProfileEditPhaseType.PAUSE,
                            isNumericInput = true,
                            isChecked = false,
                            duration = currentFeedProfile.feedProfilePhases[index * 2].duration,
                        ),
                        FeedProfileEditMenuItem(
                            id = (index * 2) * 10 + 1,
                            level = 3,
                            text = "Skimmer",
                            isToggleButton = true,
                            phaseType = FeedProfileEditPhaseType.PAUSE,
                            toggleButtonType = FeedProfileEditToggleButtonType.Skimmer,
                            isNumericInput = false,
                            isChecked = currentFeedProfile.feedProfilePhases[index * 2].skimmer,
                            duration = 0.0,
                        ),
                        FeedProfileEditMenuItem(
                            id = (index * 2) * 10 + 2,
                            level = 3,
                            text = "Main Pump 1",
                            phaseType = FeedProfileEditPhaseType.PAUSE,
                            toggleButtonType = FeedProfileEditToggleButtonType.MainPump1,
                            isNumericInput = false,
                            isChecked = currentFeedProfile.feedProfilePhases[index * 2].mainPump1,
                            duration = 0.0,
                        ),
                        FeedProfileEditMenuItem(
                            id = (index * 2) * 10 + 3,
                            level = 3,
                            text = "Main Pump 2",
                            phaseType = FeedProfileEditPhaseType.PAUSE,
                            toggleButtonType = FeedProfileEditToggleButtonType.MainPump2,
                            isNumericInput = false,
                            isChecked = currentFeedProfile.feedProfilePhases[index * 2].mainPump2,
                            duration = 0.0,
                        ),
                        FeedProfileEditMenuItem(
                            id = (index * 2) * 10 + 4,
                            level = 3,
                            text = "Aux. Pump 1",
                            phaseType = FeedProfileEditPhaseType.PAUSE,
                            toggleButtonType = FeedProfileEditToggleButtonType.AuxPump1,
                            isNumericInput = false,
                            isChecked = currentFeedProfile.feedProfilePhases[index * 2].auxPump1,
                            duration = 0.0,
                        ),
                        FeedProfileEditMenuItem(
                            id = (index * 2) * 10 + 5,
                            level = 3,
                            text = "Aux. Pump 2",
                            phaseType = FeedProfileEditPhaseType.PAUSE,
                            toggleButtonType = FeedProfileEditToggleButtonType.AuxPump2,
                            isNumericInput = false,
                            isChecked = currentFeedProfile.feedProfilePhases[index * 2].auxPump2,
                            duration = 0.0,
                        ),
                    )
                ),
                FeedProfileEditMenuItem(
                    id = 1,
                    level = 2,
                    text = "Feed",
                    isToggleButton = false,
                    isNumericInput = false,
                    isChecked = false,
                    duration = 0.0,
                    phaseType = FeedProfileEditPhaseType.FEED,
                    children = mutableListOf(
                        FeedProfileEditMenuItem(
                            id = (index * 2 + 1) * 10 + 0,
                            level = 3,
                            text = "Duration",
                            isToggleButton = false,
                            phaseType = FeedProfileEditPhaseType.FEED,
                            isNumericInput = true,
                            isChecked = false,
                            duration = currentFeedProfile.feedProfilePhases[index * 2 + 1].duration,
                        ),
                        FeedProfileEditMenuItem(
                            id = (index * 2 + 1) * 10 + 1,
                            level = 3,
                            text = "Skimmer",
                            isToggleButton = true,
                            phaseType = FeedProfileEditPhaseType.FEED,
                            toggleButtonType = FeedProfileEditToggleButtonType.Skimmer,
                            isNumericInput = false,
                            isChecked = currentFeedProfile.feedProfilePhases[index * 2 + 1].skimmer,
                            duration = 0.0,
                        ),
                        FeedProfileEditMenuItem(
                            id = (index * 2 + 1) * 10 + 2,
                            level = 3,
                            text = "Main Pump 1",
                            phaseType = FeedProfileEditPhaseType.FEED,
                            toggleButtonType = FeedProfileEditToggleButtonType.MainPump1,
                            isNumericInput = false,
                            isChecked = currentFeedProfile.feedProfilePhases[index * 2 + 1].mainPump1,
                            duration = 0.0,
                        ),
                        FeedProfileEditMenuItem(
                            id = (index * 2 + 1) * 10 + 3,
                            level = 3,
                            text = "Main Pump 2",
                            phaseType = FeedProfileEditPhaseType.FEED,
                            toggleButtonType = FeedProfileEditToggleButtonType.MainPump2,
                            isNumericInput = false,
                            isChecked = currentFeedProfile.feedProfilePhases[index * 2 + 1].mainPump2,
                            duration = 0.0,
                        ),
                        FeedProfileEditMenuItem(
                            id = (index * 2 + 1) * 10 + 4,
                            level = 3,
                            text = "Aux. Pump 1",
                            phaseType = FeedProfileEditPhaseType.FEED,
                            toggleButtonType = FeedProfileEditToggleButtonType.AuxPump1,
                            isNumericInput = false,
                            isChecked = currentFeedProfile.feedProfilePhases[index * 2 + 1].auxPump1,
                            duration = 0.0,
                        ),
                        FeedProfileEditMenuItem(
                            id = (index * 2 + 1) * 10 + 5,
                            level = 3,
                            text = "Aux. Pump 2",
                            phaseType = FeedProfileEditPhaseType.FEED,
                            toggleButtonType = FeedProfileEditToggleButtonType.AuxPump2,
                            isNumericInput = false,
                            isChecked = currentFeedProfile.feedProfilePhases[index * 2 + 1].auxPump2,
                            duration = 0.0,
                        )
                    )
                )
            )
        )
    }
}

@Composable
fun FeedProfileEditScreen(
    navHostController: NavHostController? = null,
    feedProfileEditViewModelFactory: FeedProfileEditViewModelFactory,
    mainViewModel: MainViewModel,
    darkTheme: Boolean? = null
) {
    // This screen displays
    // - a graphic visualizing the feed profile
    // - button for triggering update of feed profile
    // - button for triggering delete of feed profile
    // - button for scheduling the execution of the feed profile
    // - a hierarchical menu for editing the feed profile

    val feedProfileEditViewModel: FeedProfileEditViewModel = viewModel(
        factory = feedProfileEditViewModelFactory
    )

    val profile: FeedProfile by feedProfileEditViewModel.feedProfileUiState.collectAsState()

    val menuItems: List<FeedProfileEditMenuItem> = remember(key1 = profile) {
        createFeedProfileEditMenuItems(currentFeedProfile = profile)
    }

    val configuration: Configuration = LocalConfiguration.current

    AquariumControllerTheme(darkTheme) {
        // A surface container using the 'background' color from the theme
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.background
        ) {
            Box {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.verticalScroll(rememberScrollState())
                ) {
                    ScreenHeadlineComposable(
                        text = stringResource(id = R.string.feed_profile_editing)
                    )

                    val lineDataFeedProfileFeeder: LineData? by
                        feedProfileEditViewModel.lineDataFeedProfileFeeder.collectAsState()
                    val lineDataFeedProfileSkimmer: LineData? by
                        feedProfileEditViewModel.lineDataFeedProfileSkimmer.collectAsState()
                    val lineDataFeedProfileMainPump1: LineData? by
                        feedProfileEditViewModel.lineDataFeedProfileMainPump1.collectAsState()
                    val lineDataFeedProfileMainPump2: LineData? by
                        feedProfileEditViewModel.lineDataFeedProfileMainPump2.collectAsState()
                    val lineDataFeedProfileAuxPump1: LineData? by
                        feedProfileEditViewModel.lineDataFeedProfileAuxPump1.collectAsState()
                    val lineDataFeedProfileAuxPump2: LineData? by
                        feedProfileEditViewModel.lineDataFeedProfileAuxPump2.collectAsState()
                    val totalDuration: Float by
                        feedProfileEditViewModel.totalDuration.collectAsState()

                    FeedProfileDisplayComposable(
                        lineDataFeedProfileFeeder,
                        lineDataFeedProfileSkimmer,
                        lineDataFeedProfileMainPump1,
                        lineDataFeedProfileMainPump2,
                        lineDataFeedProfileAuxPump1,
                        lineDataFeedProfileAuxPump2,
                        totalDuration
                    )

                    FeedProfileDisplayLegendComposable()

                    when (configuration.orientation) {
                        Configuration.ORIENTATION_LANDSCAPE -> {
                            Row {
                                FeedProfileEditButtonsComposable(
                                    mainViewModel = mainViewModel,
                                    feedProfileEditViewModel = feedProfileEditViewModel
                                )
                            }
                        }
                        else -> {
                            FeedProfileEditButtonsComposable(
                                mainViewModel = mainViewModel,
                                feedProfileEditViewModel = feedProfileEditViewModel,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }

                    FeedProfileEditMenuComposable(
                        feedProfileEditMenuItems = menuItems,
                        feedProfileEditViewModel = feedProfileEditViewModel
                    )
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

fun createMockFeedProfile(): FeedProfile {
    val mockFeedProfileImport = FeedProfileImport()
    mockFeedProfileImport.profileId = 1
    mockFeedProfileImport.profileName = "Test Profile"
    mockFeedProfileImport.profileId = 1
    mockFeedProfileImport.profileName = "Test Profile"
    mockFeedProfileImport.pause01duration = 1000
    mockFeedProfileImport.pause02duration = 1000
    mockFeedProfileImport.pause03duration = 1000
    mockFeedProfileImport.pause04duration = 1000
    mockFeedProfileImport.pause05duration = 1000
    mockFeedProfileImport.pause06duration = 1000
    mockFeedProfileImport.pause07duration = 1000
    mockFeedProfileImport.pause08duration = 1000
    mockFeedProfileImport.pause09duration = 1000
    mockFeedProfileImport.pause10duration = 1000
    mockFeedProfileImport.feed01duration = 1000
    mockFeedProfileImport.feed02duration = 1000
    mockFeedProfileImport.feed03duration = 1000
    mockFeedProfileImport.feed04duration = 1000
    mockFeedProfileImport.feed05duration = 1000
    mockFeedProfileImport.feed06duration = 1000
    mockFeedProfileImport.feed07duration = 1000
    mockFeedProfileImport.feed08duration = 1000
    mockFeedProfileImport.feed09duration = 1000
    mockFeedProfileImport.feed10duration = 1000
    mockFeedProfileImport.pause01skimmer = 1
    mockFeedProfileImport.pause02skimmer = 1
    mockFeedProfileImport.pause03skimmer = 1
    mockFeedProfileImport.pause04skimmer = 1
    mockFeedProfileImport.pause05skimmer = 1
    mockFeedProfileImport.pause06skimmer = 1
    mockFeedProfileImport.pause07skimmer = 1
    mockFeedProfileImport.pause08skimmer = 1
    mockFeedProfileImport.pause09skimmer = 1
    mockFeedProfileImport.pause10skimmer = 0
    mockFeedProfileImport.feed01skimmer = 1
    mockFeedProfileImport.feed02skimmer = 1
    mockFeedProfileImport.feed03skimmer = 1
    mockFeedProfileImport.feed04skimmer = 1
    mockFeedProfileImport.feed05skimmer = 1
    mockFeedProfileImport.feed06skimmer = 1
    mockFeedProfileImport.feed07skimmer = 1
    mockFeedProfileImport.feed08skimmer = 1
    mockFeedProfileImport.feed09skimmer = 1
    mockFeedProfileImport.feed10skimmer = 0
    mockFeedProfileImport.pause01mainPump1 = 1
    mockFeedProfileImport.pause02mainPump1 = 1
    mockFeedProfileImport.pause03mainPump1 = 1
    mockFeedProfileImport.pause04mainPump1 = 1
    mockFeedProfileImport.pause05mainPump1 = 1
    mockFeedProfileImport.pause06mainPump1 = 1
    mockFeedProfileImport.pause07mainPump1 = 1
    mockFeedProfileImport.pause08mainPump1 = 1
    mockFeedProfileImport.pause09mainPump1 = 0
    mockFeedProfileImport.pause10mainPump1 = 0
    mockFeedProfileImport.feed01mainPump1 = 1
    mockFeedProfileImport.feed02mainPump1 = 1
    mockFeedProfileImport.feed03mainPump1 = 1
    mockFeedProfileImport.feed04mainPump1 = 1
    mockFeedProfileImport.feed05mainPump1 = 1
    mockFeedProfileImport.feed06mainPump1 = 1
    mockFeedProfileImport.feed07mainPump1 = 1
    mockFeedProfileImport.feed08mainPump1 = 1
    mockFeedProfileImport.feed09mainPump1 = 0
    mockFeedProfileImport.feed10mainPump1 = 0
    mockFeedProfileImport.pause01mainPump2 = 1
    mockFeedProfileImport.pause02mainPump2 = 1
    mockFeedProfileImport.pause03mainPump2 = 1
    mockFeedProfileImport.pause04mainPump2 = 1
    mockFeedProfileImport.pause05mainPump2 = 1
    mockFeedProfileImport.pause06mainPump2 = 1
    mockFeedProfileImport.pause07mainPump2 = 1
    mockFeedProfileImport.pause08mainPump2 = 0
    mockFeedProfileImport.pause09mainPump2 = 0
    mockFeedProfileImport.pause10mainPump2 = 0
    mockFeedProfileImport.feed01mainPump2 = 1
    mockFeedProfileImport.feed02mainPump2 = 1
    mockFeedProfileImport.feed03mainPump2 = 1
    mockFeedProfileImport.feed04mainPump2 = 1
    mockFeedProfileImport.feed05mainPump2 = 1
    mockFeedProfileImport.feed06mainPump2 = 1
    mockFeedProfileImport.feed07mainPump2 = 1
    mockFeedProfileImport.feed08mainPump2 = 0
    mockFeedProfileImport.feed09mainPump2 = 0
    mockFeedProfileImport.feed10mainPump2 = 0
    mockFeedProfileImport.pause01auxPump1 = 1
    mockFeedProfileImport.pause02auxPump1 = 1
    mockFeedProfileImport.pause03auxPump1 = 1
    mockFeedProfileImport.pause04auxPump1 = 1
    mockFeedProfileImport.pause05auxPump1 = 1
    mockFeedProfileImport.pause06auxPump1 = 1
    mockFeedProfileImport.pause07auxPump1 = 0
    mockFeedProfileImport.pause08auxPump1 = 0
    mockFeedProfileImport.pause09auxPump1 = 0
    mockFeedProfileImport.pause10auxPump1 = 0
    mockFeedProfileImport.feed01auxPump1 = 1
    mockFeedProfileImport.feed02auxPump1 = 1
    mockFeedProfileImport.feed03auxPump1 = 1
    mockFeedProfileImport.feed04auxPump1 = 1
    mockFeedProfileImport.feed05auxPump1 = 1
    mockFeedProfileImport.feed06auxPump1 = 1
    mockFeedProfileImport.feed07auxPump1 = 0
    mockFeedProfileImport.feed08auxPump1 = 0
    mockFeedProfileImport.feed09auxPump1 = 0
    mockFeedProfileImport.feed10auxPump1 = 0
    mockFeedProfileImport.pause01auxPump2 = 1
    mockFeedProfileImport.pause02auxPump2 = 1
    mockFeedProfileImport.pause03auxPump2 = 1
    mockFeedProfileImport.pause04auxPump2 = 1
    mockFeedProfileImport.pause05auxPump2 = 1
    mockFeedProfileImport.pause06auxPump2 = 1
    mockFeedProfileImport.pause07auxPump2 = 0
    mockFeedProfileImport.pause08auxPump2 = 0
    mockFeedProfileImport.pause09auxPump2 = 0
    mockFeedProfileImport.pause10auxPump2 = 0
    mockFeedProfileImport.feed01auxPump2 = 1
    mockFeedProfileImport.feed02auxPump2 = 1
    mockFeedProfileImport.feed03auxPump2 = 1
    mockFeedProfileImport.feed04auxPump2 = 1
    mockFeedProfileImport.feed05auxPump2 = 1
    mockFeedProfileImport.feed06auxPump2 = 1
    mockFeedProfileImport.feed07auxPump2 = 0
    mockFeedProfileImport.feed08auxPump2 = 0
    mockFeedProfileImport.feed09auxPump2 = 0
    mockFeedProfileImport.feed10auxPump2 = 0

    return FeedProfile(mockFeedProfileImport)
}

val mockFeedProfile = createMockFeedProfile()

val mockFeedProfileEditViewModelFactory = FeedProfileEditViewModelFactory(
    feedProfile = mockFeedProfile,
    feedProfilesRepository = mockFeedProfilesRepository
)

@Preview(showBackground = true, heightDp = 1100, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(showBackground = true, heightDp = 1100, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    device = "spec:width=600dp,height=1100dp,orientation=landscape"
)
@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO,
    device = "spec:width=600dp,height=1100dp,orientation=landscape"
)
@Composable
internal fun FeedProfileEditScreenPreview() {
    FeedProfileEditScreen(
        feedProfileEditViewModelFactory = mockFeedProfileEditViewModelFactory,
        mainViewModel = mockMainViewModel
    )
}