package com.laimburggasse.aquariumcontrol.heating

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.laimburggasse.aquariumcontrol.common.presentation.MainScaffold
import com.laimburggasse.aquariumcontrol.common.ScreenshotTestHelper
import com.laimburggasse.aquariumcontrol.common.presentation.ViewNavigationRoutes
import com.laimburggasse.aquariumcontrol.heating.presentation.HeatingScreen
import com.laimburggasse.aquariumcontrol.heating.presentation.mockHeatingSetValsUiState
import com.laimburggasse.aquariumcontrol.heating.presentation.mockHeatingStatsUiState
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

    @Test
    fun heatingScreenDarkTest() {
        composeTestRule.setContent {
            MainScaffold(
                isControllerLoaded = true,
                navigationRouteSource = ViewNavigationRoutes.heating,
                screenContent = {
                    HeatingScreen(
                        heatingSetValsUiState = mockHeatingSetValsUiState,
                        heatingSetValsValid = remember { mutableStateOf(value = true) },
                        onHeatingSetValsEvent = {},
                        heatingStatsUiState = mockHeatingStatsUiState,
                        onHeatingStatsEvent = {}
                    )
                },
                onNavigate = {},
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
        composeTestRule.setContent {
            MainScaffold(
                isControllerLoaded = true,
                navigationRouteSource = ViewNavigationRoutes.heating,
                screenContent = {
                    HeatingScreen(
                        heatingSetValsUiState = mockHeatingSetValsUiState,
                        heatingSetValsValid = remember { mutableStateOf(value = true) },
                        onHeatingSetValsEvent = {},
                        heatingStatsUiState = mockHeatingStatsUiState,
                        onHeatingStatsEvent = {}
                    )
                },
                onNavigate = {},
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
