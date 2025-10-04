package com.laimburggasse.aquariumcontrol.timedata

import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.laimburggasse.aquariumcontrol.ScreenshotTestHelper
import com.laimburggasse.aquariumcontrol.timedata.TimeDataUiState.Loading
import com.laimburggasse.aquariumcontrol.timedata.TimeDataUiState.Error
import com.laimburggasse.aquariumcontrol.timedata.TimeDataUiState.Success
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TimeDataScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    // Set this to true to record new golden images.
    private val recordMode = false

    @Test
    fun timeDataScreenLoadingDarkTest() {
        composeTestRule.setContent {
            TimeDataScreen(mockTimeDataUiState = Loading, darkTheme = true)
        }
        composeTestRule.waitForIdle()
        ScreenshotTestHelper.performScreenshotTest(
            goldenName = "time_data_screen_loading_dark",
            recordMode = recordMode,
            imageBitmap = composeTestRule.onRoot().captureToImage()
        )
    }

    @Test
    fun timeDataScreenLoadingLightTest() {
        composeTestRule.setContent {
            TimeDataScreen(mockTimeDataUiState = Loading, darkTheme = false)
        }
        composeTestRule.waitForIdle()
        ScreenshotTestHelper.performScreenshotTest(
            goldenName = "time_data_screen_loading_light",
            recordMode = recordMode,
            imageBitmap = composeTestRule.onRoot().captureToImage()
        )
    }

    @Test
    fun timeDataScreenErrorDarkTest() {
        composeTestRule.setContent {
            TimeDataScreen(
                mockTimeDataUiState = Error("Error message"),
                darkTheme = true
            )
        }
        composeTestRule.waitForIdle()
        ScreenshotTestHelper.performScreenshotTest(
            goldenName = "time_data_screen_error_dark",
            recordMode = recordMode,
            imageBitmap = composeTestRule.onRoot().captureToImage()
        )
    }

    @Test
    fun timeDataScreenErrorLightTest() {
        composeTestRule.setContent {
            TimeDataScreen(
                mockTimeDataUiState = Error("Error message"),
                darkTheme = false
            )
        }
        composeTestRule.waitForIdle()
        ScreenshotTestHelper.performScreenshotTest(
            goldenName = "time_data_screen_error_light",
            recordMode = recordMode,
            imageBitmap = composeTestRule.onRoot().captureToImage()
        )
    }

    @Test
    fun timeDataScreenSuccessDarkTest() {
        composeTestRule.setContent {
            TimeDataScreen(
                mockTimeDataUiState = Success(TimeDataSets(generateMockTimeData())),
                darkTheme = true
            )
        }
        composeTestRule.waitForIdle()
        ScreenshotTestHelper.performScreenshotTest(
            goldenName = "time_data_screen_error_dark",
            recordMode = recordMode,
            imageBitmap = composeTestRule.onRoot().captureToImage()
        )
    }

    @Test
    fun timeDataScreenSuccessLightTest() {
        composeTestRule.setContent {
            TimeDataScreen(
                mockTimeDataUiState = Success(TimeDataSets(generateMockTimeData())),
                darkTheme = false
            )
        }
        composeTestRule.waitForIdle()
        ScreenshotTestHelper.performScreenshotTest(
            goldenName = "time_data_screen_error_light",
            recordMode = recordMode,
            imageBitmap = composeTestRule.onRoot().captureToImage()
        )
    }

}
