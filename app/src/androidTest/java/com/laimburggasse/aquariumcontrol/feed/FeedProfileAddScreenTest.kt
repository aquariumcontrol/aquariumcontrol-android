package com.laimburggasse.aquariumcontrol.feed

import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.laimburggasse.aquariumcontrol.MainScaffold
import com.laimburggasse.aquariumcontrol.ScreenshotTestHelper
import com.laimburggasse.aquariumcontrol.ViewNavigationRoutes
import com.laimburggasse.aquariumcontrol.feed.add.FeedProfileAddScreen
import com.laimburggasse.aquariumcontrol.feed.add.FeedProfileAddUiState
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(value = AndroidJUnit4::class)
class FeedProfileAddScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    // Set this to true to record new golden images.
    private val recordMode = false

    private val subfolder = "feed"

    @Test
    fun feedAddScreenDarkTest() {
        composeTestRule.setContent {
            MainScaffold(
                isControllerLoaded = true,
                navigationRouteSource = ViewNavigationRoutes.feedAddProfile,
                screenContent = {
                    FeedProfileAddScreen(
                        feedProfileName = "test",
                        feedProfileAddUiState = FeedProfileAddUiState.Idle,
                        onNavigateFromFeed = {event, id, isNew, time ->},
                        onFeedProfileAddEvent = {event ->}
                    )
                },
                onNavigate = {},
                darkTheme = true
            )
        }
        composeTestRule.waitForIdle()
        ScreenshotTestHelper.performScreenshotTest(
            goldenName = "feed_profile_add_screen_dark",
            recordMode = recordMode,
            subfolder = subfolder,
            imageBitmap = composeTestRule.onRoot().captureToImage()
        )
    }

    @Test
    fun feedAddScreenLightTest() {
        composeTestRule.setContent {
            MainScaffold(
                isControllerLoaded = true,
                navigationRouteSource = ViewNavigationRoutes.feedAddProfile,
                screenContent = {
                    FeedProfileAddScreen(
                        feedProfileName = "test",
                        feedProfileAddUiState = FeedProfileAddUiState.Idle,
                        onNavigateFromFeed = {event, id, isNew, time ->},
                        onFeedProfileAddEvent = {event ->}
                    )
                },
                onNavigate = {},
                darkTheme = false
            )
        }
        composeTestRule.waitForIdle()
        ScreenshotTestHelper.performScreenshotTest(
            goldenName = "feed_profile_add_screen_light",
            recordMode = recordMode,
            subfolder = subfolder,
            imageBitmap = composeTestRule.onRoot().captureToImage()
        )
    }
}
