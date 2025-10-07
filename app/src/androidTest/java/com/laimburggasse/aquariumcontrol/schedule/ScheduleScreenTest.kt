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

    private val subfolder = "schedule"

    val mockScheduleEntries = ScheduleEntries(
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
    val mockScheduleUiStateSuccess = ScheduleUiState.Success(mockScheduleEntries)
    val mockScheduleViewModelSuccess = MockScheduleViewModel(scheduleUiState = mockScheduleUiStateSuccess)

    @Test
    fun scheduleScreenDarkTest() {
        composeTestRule.setContent {
            ScheduleScreen(
                mainViewModel = mockMainViewModel,
                scheduleViewModel = mockScheduleViewModelSuccess,
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
            ScheduleScreen(
                mainViewModel = mockMainViewModel,
                scheduleViewModel = mockScheduleViewModelSuccess,
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
