package com.laimburggasse.aquariumcontrol.feed

import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.laimburggasse.aquariumcontrol.MockMainViewModel
import com.laimburggasse.aquariumcontrol.ScreenshotTestHelper
import com.laimburggasse.aquariumcontrol.feed.schedule.FeedProfileScheduleScreen
import com.laimburggasse.aquariumcontrol.feed.schedule.FeedScheduleEntry
import com.laimburggasse.aquariumcontrol.feed.schedule.FeedScheduleEntryImport
import com.laimburggasse.aquariumcontrol.feed.schedule.FeedScheduleUiState
import com.laimburggasse.aquariumcontrol.feed.schedule.MockFeedScheduleViewModel
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FeedProfileScheduleScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    // Set this to true to record new golden images.
    private val recordMode = false

    private val subfolder = "feed"

    fun generateMockFeedProfile(): FeedProfile {
        val mockFeedProfileImport = FeedProfileImport()
        mockFeedProfileImport.profileId = 1
        mockFeedProfileImport.profileName = "Test Profile 1"

        return FeedProfile(mockFeedProfileImport)
    }

    fun generateMockFeedScheduleEntry(): FeedScheduleEntry {
        return FeedScheduleEntryImport(
            timestampString = "2024-02-16 12:00:00",
            profileName = "MockProfile",
            isDaily = 1
        ).transformToFeedScheduleEntry()
    }

    fun generateMockMainViewModel(): MockMainViewModel {
        val mockMainViewModel = MockMainViewModel()
        mockMainViewModel.updateSelectedFeedProfile(feedProfile = generateMockFeedProfile())
        mockMainViewModel.updateSelectedFeedScheduleEntry(feedScheduleEntry = generateMockFeedScheduleEntry())
        return mockMainViewModel
    }

    @Test
    fun feedScheduleScreenDarkTest() {
        val mockFeedProfileImport = FeedProfileImport()
        mockFeedProfileImport.profileId = 1
        mockFeedProfileImport.profileName = "Test Profile 1"
        val mockFeedProfile = FeedProfile(mockFeedProfileImport)
        val mockFeedScheduleEntry: FeedScheduleEntry = generateMockFeedScheduleEntry()
        val mockFeedScheduleEntries: List<FeedScheduleEntry> = listOf(mockFeedScheduleEntry)
        val mockFeedScheduleViewModel = MockFeedScheduleViewModel(
            feedScheduleUiState = FeedScheduleUiState.Success(feedScheduleEntries = mockFeedScheduleEntries),
            isRefreshing = false,
            feedScheduleEntries = mockFeedScheduleEntries
        )
        val mockFeedProfiles: List<FeedProfile> = listOf(mockFeedProfile)
        val mockFeedProfilesViewModel = MockFeedProfilesViewModel(
            feedProfilesUiState = FeedProfilesUiState.Success(mockFeedProfiles),
            isRefreshing = false,
            feedProfiles = mockFeedProfiles
        )
        mockMainViewModel.setMyFeedScheduleViewModel(mockFeedScheduleViewModel)
        mockMainViewModel.setMyFeedProfileViewModel(mockFeedProfilesViewModel)

        composeTestRule.setContent {
            FeedProfileScheduleScreen(
                mainViewModel = generateMockMainViewModel(),
                darkTheme = true,
                profileName = mockFeedProfile.profileName,
                milliSecondsSinceEpoch = mockFeedScheduleEntry.timestamp.time
            )
        }
        composeTestRule.waitForIdle()
        ScreenshotTestHelper.performScreenshotTest(
            goldenName = "feed_profile_schedule_screen_dark",
            recordMode = recordMode,
            subfolder = subfolder,
            imageBitmap = composeTestRule.onRoot().captureToImage()
        )
    }

    @Test
    fun feedScheduleScreenLightTest() {
        val mockFeedProfileImport = FeedProfileImport()
        mockFeedProfileImport.profileId = 1
        mockFeedProfileImport.profileName = "Test Profile 1"
        val mockFeedProfile = FeedProfile(mockFeedProfileImport)
        val mockFeedScheduleEntry: FeedScheduleEntry = generateMockFeedScheduleEntry()
        val mockFeedScheduleEntries: List<FeedScheduleEntry> = listOf(mockFeedScheduleEntry)
        val mockFeedScheduleViewModel = MockFeedScheduleViewModel(
            feedScheduleUiState = FeedScheduleUiState.Success(feedScheduleEntries = mockFeedScheduleEntries),
            isRefreshing = false,
            feedScheduleEntries = mockFeedScheduleEntries
        )
        val mockFeedProfiles: List<FeedProfile> = listOf(mockFeedProfile)
        val mockFeedProfilesViewModel = MockFeedProfilesViewModel(
            feedProfilesUiState = FeedProfilesUiState.Success(mockFeedProfiles),
            isRefreshing = false,
            feedProfiles = mockFeedProfiles
        )
        mockMainViewModel.setMyFeedScheduleViewModel(mockFeedScheduleViewModel)
        mockMainViewModel.setMyFeedProfileViewModel(mockFeedProfilesViewModel)
        composeTestRule.setContent {
            FeedProfileScheduleScreen(
                mainViewModel = generateMockMainViewModel(),
                darkTheme = false,
                profileName = mockFeedProfile.profileName,
                milliSecondsSinceEpoch = mockFeedScheduleEntry.timestamp.time
            )
        }
        composeTestRule.waitForIdle()
        ScreenshotTestHelper.performScreenshotTest(
            goldenName = "feed_profile_schedule_screen_light",
            recordMode = recordMode,
            subfolder = subfolder,
            imageBitmap = composeTestRule.onRoot().captureToImage()
        )
    }

}
