package com.laimburggasse.aquariumcontrol.schedule

import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.laimburggasse.aquariumcontrol.ScreenshotTestHelper
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ScheduleScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    // Set this to true to record new golden images.
    private val recordMode = false

    val mockScheduleSetVals = ScheduleSetVals(
        ballingStartTime = 2F,
        ballingStopTime = 22F,
        ballingLimitationActive = false,
        refillStartTime = 3F,
        refillStopTime = 21F,
        refillLimitationActive = true,
        ventilationStartTime = 4F,
        ventilationStopTime = 20F,
        ventilationLimitationActive = false,
        heatingStartTime = 5F,
        heatingStopTime = 19F,
        heatingLimitationActive = true,
    )

    @Test
    fun refillAddScreenDarkTest() {
        composeTestRule.setContent {
            ScheduleScreen(navHostController = null, mockScheduleSetVals, darkTheme = true)
        }
        composeTestRule.waitForIdle()
        ScreenshotTestHelper.performScreenshotTest(
            goldenName = "schedule_screen_dark",
            recordMode = recordMode,
            imageBitmap = composeTestRule.onRoot().captureToImage()
        )
    }

    @Test
    fun refillAddScreenLightTest() {
        composeTestRule.setContent {
            ScheduleScreen(navHostController = null, mockScheduleSetVals, darkTheme = false)
        }
        composeTestRule.waitForIdle()
        ScreenshotTestHelper.performScreenshotTest(
            goldenName = "schedule_screen_light",
            recordMode = recordMode,
            imageBitmap = composeTestRule.onRoot().captureToImage()
        )
    }
}
