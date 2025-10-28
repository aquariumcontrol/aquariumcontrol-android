package com.laimburggasse.aquariumcontrol.controller

import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.laimburggasse.aquariumcontrol.MainScaffold
import com.laimburggasse.aquariumcontrol.ScreenshotTestHelper
import com.laimburggasse.aquariumcontrol.ViewNavigationRoutes
import com.laimburggasse.aquariumcontrol.controller.common.ControllerProfile
import com.laimburggasse.aquariumcontrol.controller.selection.ControllerSelectionState
import com.laimburggasse.aquariumcontrol.controller.selection.ControllerSelectionScreen
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

    val mockControllerProfile1 = ControllerProfile(
        id = 1,
        name = "Controller 1",
        address = "192.168.1.1",
        port = 80,
        user = "user",
        password = "password",
    )
    val mockControllerProfile2 = ControllerProfile(
        id = 2,
        name = "Controller 2",
        address = "servername.external.com",
        port = 80,
        user = "userXYZ",
        password = "passwordXYZ",
    )

    val controllerProfileList = listOf(
        mockControllerProfile1, mockControllerProfile2
    )

    val mockControllerSelectionState = ControllerSelectionState()

    @Test
    fun controllerSelectionScreenDarkTest() {
        composeTestRule.setContent {
            mockControllerSelectionState.controllerProfileList = controllerProfileList
            MainScaffold(
                isControllerLoaded = true,
                navigationRouteSource = ViewNavigationRoutes.controllerAdd,
                screenContent = {
                    ControllerSelectionScreen(
                        state = mockControllerSelectionState,
                        onEvent = {},
                        defaultProfileId = remember {mutableLongStateOf(value = 1)},
                        onNavigateFromController = {event, id -> },
                        canStartWaitForNavigation = remember {mutableStateOf(value = true)},
                        progressToDefaultNavigation = remember {mutableFloatStateOf(value = 0.3f)}
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
            mockControllerSelectionState.controllerProfileList = controllerProfileList
            MainScaffold(
                isControllerLoaded = true,
                navigationRouteSource = ViewNavigationRoutes.controllerAdd,
                screenContent = {
                    ControllerSelectionScreen(
                        state = mockControllerSelectionState,
                        onEvent = {},
                        defaultProfileId = remember {mutableLongStateOf(value = 1)},
                        onNavigateFromController = {event, id -> },
                        canStartWaitForNavigation = remember {mutableStateOf(value = true)},
                        progressToDefaultNavigation = remember {mutableFloatStateOf(value = 0.3f)}
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
