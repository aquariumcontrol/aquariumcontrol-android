/*
 *  Copyright 2025 Uwe Martin
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.laimburggasse.aquariumcontrol.controller

import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.platform.app.InstrumentationRegistry
import com.laimburggasse.aquariumcontrol.common.presentation.MainScaffold
import com.laimburggasse.aquariumcontrol.common.presentation.ViewNavigationRoutes
import com.laimburggasse.aquariumcontrol.controller.presentation.components.ControllerAddEditUiEvent
import com.laimburggasse.aquariumcontrol.controller.presentation.components.ControllerAddEditUiState
import com.laimburggasse.aquariumcontrol.controller.presentation.edit.ControllerEditScreen
import com.laimburggasse.aquariumcontrol.controller.presentation.navigation.ControllerNavigationEvent
import org.junit.Assert
import org.junit.Rule
import org.junit.Test

class ControllerEditScreenUITest {
    @get:Rule
    val composeTestRule = createComposeRule()

    private val context = InstrumentationRegistry.getInstrumentation().targetContext

    @Test
    fun tapping_update_button_navigates_to_controller_selection_screen() {
        var capturedEvent: ControllerAddEditUiEvent? = null
        var capturedNavigationEvent: ControllerNavigationEvent? = null
        val labelUpdateButton = context.getString(R.string.update_controller_profile)

        // A state containing a valid profile
        val state = ControllerAddEditUiState(
            controllerProfileId = 0,
            controllerProfileName = "Dummy",
            controllerProfileAddress = "127.0.0.1",
            controllerProfilePort = 80,
            controllerProfileUser = "dummy",
            controllerProfilePassword = "dummy",
            validControllerAddress = true
        )

        composeTestRule.setContent {
            MainScaffold(
                isControllerLoaded = true,
                navigationRouteSource = ViewNavigationRoutes.controllerAdd,
                screenContent = {
                    ControllerEditScreen(
                        state,
                        onEvent = { event -> capturedEvent = event },
                        onNavigateFromController = { navigationEvent, _ -> capturedNavigationEvent = navigationEvent },
                        isDefaultProfileInitialValue = false,
                    )
                },
                onNavigate = {},
                darkTheme = true
            )
        }

        composeTestRule.waitForIdle()

        // Find the button for adding a controller, check if it is clickable and click it
        composeTestRule.onNodeWithText(labelUpdateButton).assertIsEnabled()
        composeTestRule.onNodeWithText(labelUpdateButton).performClick()

        // Assert the callbacks were triggered with the expected enum values
        Assert.assertEquals(ControllerAddEditUiEvent.UpdateControllerProfile, capturedEvent)
        Assert.assertEquals(ControllerNavigationEvent.ToControllerMain, capturedNavigationEvent)
    }
}