package com.laimburggasse.aquariumcontrol.feed

import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.laimburggasse.aquariumcontrol.MainScaffold
import com.laimburggasse.aquariumcontrol.ScreenshotTestHelper
import com.laimburggasse.aquariumcontrol.ViewNavigationRoutes
import com.laimburggasse.aquariumcontrol.feed.presentation.entry.FeedScreen
import com.laimburggasse.aquariumcontrol.feed.presentation.entry.mockFeedProfilesUiStateSuccess
import com.laimburggasse.aquariumcontrol.feed.presentation.entry.mockFeedScheduleSelectionUiStateSuccess
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FeedScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    // Set this to true to record new golden images.
    private val recordMode = false

    private val subfolder = "feed"

    @Test
    fun feedScreenDarkTest() {
        composeTestRule.setContent {
            MainScaffold(
                isControllerLoaded = true,
                navigationRouteSource = ViewNavigationRoutes.feed,
                screenContent = {
                    FeedScreen(
                        feedProfilesUiState = mockFeedProfilesUiStateSuccess,
                        feedScheduleSelectionUiState = mockFeedScheduleSelectionUiStateSuccess,
                        onNavigateFromFeed = { event, id, isNew, millis -> },
                        onFeedProfilesUiEvent = {},
                        onFeedSchedulesUiEvent = { event -> return@FeedScreen null }
                    )
                },
                onNavigate = {},
                darkTheme = true
            )
        }
        composeTestRule.waitForIdle()
        ScreenshotTestHelper.performScreenshotTest(
            goldenName = "feed_screen_dark",
            recordMode = recordMode,
            subfolder = subfolder,
            imageBitmap = composeTestRule.onRoot().captureToImage()
        )
    }

    @Test
    fun feedScreenLightTest() {
        composeTestRule.setContent {
            MainScaffold(
                isControllerLoaded = true,
                navigationRouteSource = ViewNavigationRoutes.feed,
                screenContent = {
                    FeedScreen(
                        feedProfilesUiState = mockFeedProfilesUiStateSuccess,
                        feedScheduleSelectionUiState = mockFeedScheduleSelectionUiStateSuccess,
                        onNavigateFromFeed = { event, id, isNew, millis -> },
                        onFeedProfilesUiEvent = {},
                        onFeedSchedulesUiEvent = { event -> return@FeedScreen null }
                    )
                },
                onNavigate = {},
                darkTheme = false
            )
        }
        composeTestRule.waitForIdle()
        ScreenshotTestHelper.performScreenshotTest(
            goldenName = "feed_screen_light",
            recordMode = recordMode,
            subfolder = subfolder,
            imageBitmap = composeTestRule.onRoot().captureToImage()
        )
    }

}
