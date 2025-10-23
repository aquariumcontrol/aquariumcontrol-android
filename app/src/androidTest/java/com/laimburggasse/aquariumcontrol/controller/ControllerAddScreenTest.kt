package com.laimburggasse.aquariumcontrol.controller

import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.laimburggasse.aquariumcontrol.MainScaffold
import com.laimburggasse.aquariumcontrol.ScreenshotTestHelper
import com.laimburggasse.aquariumcontrol.ViewNavigationRoutes
import com.laimburggasse.aquariumcontrol.balling.entry.mockMainViewModel
import com.laimburggasse.aquariumcontrol.controller.add.ControllerAddScreen
import com.laimburggasse.aquariumcontrol.controller.common.ControllerState
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ControllerAddScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    // Set this to true to record new golden images.
    private val recordMode = false

    private val subfolder = "controller"

    @Test
    fun controllerAddScreenDarkTest() {
        composeTestRule.setContent {
            MainScaffold(
                mainViewModel = mockMainViewModel,
                navigationRouteSource = ViewNavigationRoutes.controllerAdd,
                screenContent = {
                    ControllerAddScreen(
                        state = ControllerState(),
                        onEvent = {},
                        onNavigateFromController = { event, id -> }
                    ) },
                onNavigate = {},
                darkTheme = true
            )
        }
        composeTestRule.waitForIdle()
        ScreenshotTestHelper.performScreenshotTest(
            goldenName = "controller_add_screen_dark",
            recordMode = recordMode,
            subfolder = subfolder,
            imageBitmap = composeTestRule.onRoot().captureToImage()
        )
    }

    @Test
    fun controllerAddScreenLightTest() {
        composeTestRule.setContent {
            MainScaffold(
                mainViewModel = mockMainViewModel,
                navigationRouteSource = ViewNavigationRoutes.controllerAdd,
                screenContent = {
                    ControllerAddScreen(
                        state = ControllerState(),
                        onEvent = {},
                        onNavigateFromController = { event, id -> }
                    ) },
                onNavigate = {},
                darkTheme = false
            )
        }
        composeTestRule.waitForIdle()
        ScreenshotTestHelper.performScreenshotTest(
            goldenName = "controller_add_screen_light",
            recordMode = recordMode,
            subfolder = subfolder,
            imageBitmap = composeTestRule.onRoot().captureToImage()
        )
    }

}
