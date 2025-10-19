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

    private val subfolder = "timedata"

    @Test
    fun timeDataScreenErrorDarkTest() {
        composeTestRule.setContent {
            TimeDataScreen(
                timeDataViewModelFactory = mockTimeDataViewModelFactoryError,
                darkTheme = true
            )
        }
        composeTestRule.waitForIdle()
        ScreenshotTestHelper.performScreenshotTest(
            goldenName = "time_data_screen_error_dark",
            recordMode = recordMode,
            subfolder = subfolder,
            imageBitmap = composeTestRule.onRoot().captureToImage()
        )
    }

    @Test
    fun timeDataScreenErrorLightTest() {
        composeTestRule.setContent {
            TimeDataScreen(
                timeDataViewModelFactory = mockTimeDataViewModelFactoryError,
                darkTheme = false
            )
        }
        composeTestRule.waitForIdle()
        ScreenshotTestHelper.performScreenshotTest(
            goldenName = "time_data_screen_error_light",
            recordMode = recordMode,
            subfolder = subfolder,
            imageBitmap = composeTestRule.onRoot().captureToImage()
        )
    }

    @Test
    fun timeDataScreenSuccessDarkTest() {
        composeTestRule.setContent {
            TimeDataScreen(
                timeDataViewModelFactory = mockTimeDataViewModelFactorySuccess,
                darkTheme = true
            )
        }
        composeTestRule.waitForIdle()
        ScreenshotTestHelper.performScreenshotTest(
            goldenName = "time_data_screen_error_dark",
            recordMode = recordMode,
            subfolder = subfolder,
            imageBitmap = composeTestRule.onRoot().captureToImage()
        )
    }

    @Test
    fun timeDataScreenSuccessLightTest() {
        composeTestRule.setContent {
            TimeDataScreen(
                timeDataViewModelFactory = mockTimeDataViewModelFactorySuccess,
                darkTheme = false
            )
        }
        composeTestRule.waitForIdle()
        ScreenshotTestHelper.performScreenshotTest(
            goldenName = "time_data_screen_error_light",
            recordMode = recordMode,
            subfolder = subfolder,
            imageBitmap = composeTestRule.onRoot().captureToImage()
        )
    }

}
