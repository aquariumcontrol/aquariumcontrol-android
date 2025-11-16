package com.laimburggasse.aquariumcontrol.controller

import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.laimburggasse.aquariumcontrol.common.presentation.MainScaffold
import com.laimburggasse.aquariumcontrol.ScreenshotTestHelper
import com.laimburggasse.aquariumcontrol.common.presentation.ViewNavigationRoutes
import com.laimburggasse.aquariumcontrol.controller.presentation.selection.ControllerSelectionScreen
import com.laimburggasse.aquariumcontrol.controller.presentation.selection.mockControllerProfileList
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ControllerSelectionScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    // Set this to true to record new golden images.
    private val recordMode = false

    private val subfolder = "controller"


    @Test
    fun controllerSelectionScreenDarkTest() {
        composeTestRule.setContent {
            MainScaffold(
                isControllerLoaded = true,
                navigationRouteSource = ViewNavigationRoutes.controllerAdd,
                screenContent = {
                    ControllerSelectionScreen(
                        controllerProfileList = mockControllerProfileList,
                        onEvent = {},
                        progressToDefaultNavigation = remember { mutableFloatStateOf(value = 0.3f) },
                        onNavigateFromController = { event, profileId -> },
                        defaultProfileId = remember { mutableLongStateOf(value = 1L) },
                        canStartWaitForNavigation = remember { mutableStateOf(value = true) },
                    )
                },
                onNavigate = {},
                darkTheme = false
            )
        }
        composeTestRule.waitForIdle()
        ScreenshotTestHelper.performScreenshotTest(
            goldenName = "controller_selection_screen_dark",
            recordMode = recordMode,
            subfolder = subfolder,
            imageBitmap = composeTestRule.onRoot().captureToImage()
        )
    }

    @Test
    fun controllerSelectionScreenLightTest() {
        composeTestRule.setContent {
            MainScaffold(
                isControllerLoaded = true,
                navigationRouteSource = ViewNavigationRoutes.controllerAdd,
                screenContent = {
                    ControllerSelectionScreen(
                        controllerProfileList = mockControllerProfileList,
                        onEvent = {},
                        progressToDefaultNavigation = remember { mutableFloatStateOf(value = 0.3f) },
                        onNavigateFromController = { event, profileId -> },
                        defaultProfileId = remember { mutableLongStateOf(value = 1L) },
                        canStartWaitForNavigation = remember { mutableStateOf(value = true) },
                    )
                },
                onNavigate = {},
                darkTheme = false
            )
        }
        composeTestRule.waitForIdle()
        ScreenshotTestHelper.performScreenshotTest(
            goldenName = "controller_selection_screen_light",
            recordMode = recordMode,
            subfolder = subfolder,
            imageBitmap = composeTestRule.onRoot().captureToImage()
        )
    }

}
