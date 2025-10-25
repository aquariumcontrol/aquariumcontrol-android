package com.laimburggasse.aquariumcontrol.info

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
class InfoScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    // Set this to true to record new golden images.
    private val recordMode = false

    private val subfolder = "info"

    @Test
    fun infoScreenDarkTest() {
        composeTestRule.setContent {
            MainScaffold(
                mainViewModel = _root_ide_package_.com.laimburggasse.aquariumcontrol.balling.entry.mockMainViewModel,
                navigationRouteSource = ViewNavigationRoutes.heating,
                screenContent = { InfoScreen() },
                onNavigate = {},
                darkTheme = true
            )
        }
        composeTestRule.waitForIdle()
        ScreenshotTestHelper.performScreenshotTest(
            goldenName = "info_screen_dark",
            recordMode = recordMode,
            subfolder = subfolder,
            imageBitmap = composeTestRule.onRoot().captureToImage()
        )
    }

    @Test
    fun infoScreenLightTest() {
        composeTestRule.setContent {
            MainScaffold(
                mainViewModel = _root_ide_package_.com.laimburggasse.aquariumcontrol.balling.entry.mockMainViewModel,
                navigationRouteSource = ViewNavigationRoutes.heating,
                screenContent = { InfoScreen() },
                onNavigate = {},
                darkTheme = false
            )
        }
        composeTestRule.waitForIdle()
        ScreenshotTestHelper.performScreenshotTest(
            goldenName = "info_screen_light",
            recordMode = recordMode,
            subfolder = subfolder,
            imageBitmap = composeTestRule.onRoot().captureToImage()
        )
    }

}
