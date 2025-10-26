package com.laimburggasse.aquariumcontrol.schedule

import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.laimburggasse.aquariumcontrol.MainScaffold
import com.laimburggasse.aquariumcontrol.ScreenshotTestHelper
import com.laimburggasse.aquariumcontrol.ViewNavigationRoutes
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ScheduleScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    // Set this to true to record new golden images.
    private val recordMode = false

    private val subfolder = "schedule"

    @Test
    fun scheduleScreenDarkTest() {
        composeTestRule.setContent {
            MainScaffold(
                isControllerLoaded = true,
                navigationRouteSource = ViewNavigationRoutes.schedule,
                screenContent = {
                    ScheduleScreen(
                        scheduleUiState = mockScheduleUiStateSuccess,
                        onScheduleEvent = {}
                    )
                },
                onNavigate = {},
                darkTheme = true
            )
        }
        composeTestRule.waitForIdle()
        ScreenshotTestHelper.performScreenshotTest(
            goldenName = "schedule_screen_dark",
            recordMode = recordMode,
            subfolder = subfolder,
            imageBitmap = composeTestRule.onRoot().captureToImage()
        )
    }

    @Test
    fun scheduleScreenLightTest() {
        composeTestRule.setContent {
            MainScaffold(
                isControllerLoaded = true,
                navigationRouteSource = ViewNavigationRoutes.schedule,
                screenContent = {
                    ScheduleScreen(
                        scheduleUiState = mockScheduleUiStateSuccess,
                        onScheduleEvent = {}
                    )
                },
                onNavigate = {},
                darkTheme = false
            )
        }
        composeTestRule.waitForIdle()
        ScreenshotTestHelper.performScreenshotTest(
            goldenName = "schedule_screen_light",
            recordMode = recordMode,
            subfolder = subfolder,
            imageBitmap = composeTestRule.onRoot().captureToImage()
        )
    }
}
