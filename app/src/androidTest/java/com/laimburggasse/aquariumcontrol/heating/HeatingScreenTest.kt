package com.laimburggasse.aquariumcontrol.heating

import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.laimburggasse.aquariumcontrol.MockMainViewModel
import com.laimburggasse.aquariumcontrol.ScreenshotTestHelper
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class HeatingScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    // Set this to true to record new golden images.
    private val recordMode = false

    private val subfolder = "heating"

    val mockMainViewModel = MockMainViewModel()
    @Test
    fun heatingScreenDarkTest() {
        val mockHeatingSetValsUiState = HeatingSetValsUiState.Success(
            HeatingSetVals(
                heatingSwitchOffTemp = 27.5,
                heatingSwitchOnTemp = 22.5
            )
        )
        val mockHeatingSetValsViewModel = MockHeatingSetValsViewModel(mockHeatingSetValsUiState)
        val mockHeatingStatsUiState = HeatingStatsUiState.Success(heatingStats = createMockStatsData())
        val mockHeatingStatsViewModel = MockHeatingStatsViewModel(mockHeatingStatsUiState)

        composeTestRule.setContent {
            HeatingScreen(
                mainViewModel = com.laimburggasse.aquariumcontrol.heating.mockMainViewModel,
                heatingStatsViewModel = mockHeatingStatsViewModel,
                heatingSetValsViewModel = mockHeatingSetValsViewModel,
                darkTheme = true
            )
        }
        composeTestRule.waitForIdle()
        ScreenshotTestHelper.performScreenshotTest(
            goldenName = "heating_screen_dark",
            recordMode = recordMode,
            subfolder = subfolder,
            imageBitmap = composeTestRule.onRoot().captureToImage()
        )
    }

    @Test
    fun heatingScreenLightTest() {
        val mockHeatingSetValsUiState = HeatingSetValsUiState.Success(
            HeatingSetVals(
                heatingSwitchOffTemp = 27.5,
                heatingSwitchOnTemp = 22.5
            )
        )
        val mockHeatingSetValsViewModel = MockHeatingSetValsViewModel(mockHeatingSetValsUiState)
        val mockHeatingStatsUiState = HeatingStatsUiState.Success(heatingStats = createMockStatsData())
        val mockHeatingStatsViewModel = MockHeatingStatsViewModel(mockHeatingStatsUiState)
        composeTestRule.setContent {
            HeatingScreen(
                mainViewModel = com.laimburggasse.aquariumcontrol.heating.mockMainViewModel,
                heatingStatsViewModel = mockHeatingStatsViewModel,
                heatingSetValsViewModel = mockHeatingSetValsViewModel,
                darkTheme = false
            )
        }
        composeTestRule.waitForIdle()
        ScreenshotTestHelper.performScreenshotTest(
            goldenName = "heating_screen_light",
            recordMode = recordMode,
            subfolder = subfolder,
            imageBitmap = composeTestRule.onRoot().captureToImage()
        )
    }

}
