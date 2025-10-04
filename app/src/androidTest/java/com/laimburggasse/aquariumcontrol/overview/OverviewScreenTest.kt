package com.laimburggasse.aquariumcontrol.overview

import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.laimburggasse.aquariumcontrol.ScreenshotTestHelper
import com.laimburggasse.aquariumcontrol.controller.ControllerProfile
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class OverviewScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    // Set this to true to record new golden images.
    private val recordMode = false

    val mockControllerProfile = ControllerProfile(
        name = "Controller 1",
        address = "192.168.1.1",
        port = 80,
        user = "user",
    )

    @Test
    fun overviewScreenDarkTest() {
        composeTestRule.setContent {
            OverviewScreen(
                mockController = mockControllerProfile,
                mockOverviewData = createMockOverviewData(),
                darkTheme = true
            )
        }
        composeTestRule.waitForIdle()
        ScreenshotTestHelper.performScreenshotTest(
            goldenName = "overview_screen_dark",
            recordMode = recordMode,
            imageBitmap = composeTestRule.onRoot().captureToImage()
        )
    }

    @Test
    fun overviewScreenLightTest() {
        composeTestRule.setContent {
            OverviewScreen(
                mockController = mockControllerProfile,
                mockOverviewData = createMockOverviewData(),
                darkTheme = false
            )
        }
        composeTestRule.waitForIdle()
        ScreenshotTestHelper.performScreenshotTest(
            goldenName = "overview_screen_light",
            recordMode = recordMode,
            imageBitmap = composeTestRule.onRoot().captureToImage()
        )
    }
}
