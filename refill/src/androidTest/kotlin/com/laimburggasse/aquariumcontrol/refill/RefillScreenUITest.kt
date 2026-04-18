/*
 * Copyright 2026 Uwe Martin
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.laimburggasse.aquariumcontrol.refill

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.laimburggasse.aquariumcontrol.common.data.remote.WebApiParams
import com.laimburggasse.aquariumcontrol.common.presentation.MainScaffold
import com.laimburggasse.aquariumcontrol.common.presentation.ViewNavigationRoutes
import com.laimburggasse.aquariumcontrol.refill.domain.model.RefillState
import com.laimburggasse.aquariumcontrol.refill.presentation.entry.RefillScreen
import com.laimburggasse.aquariumcontrol.refill.presentation.entry.RefillStateUiState
import com.laimburggasse.aquariumcontrol.refill.presentation.navigation.RefillNavigationEvent
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RefillScreenUITest {
    @get:Rule
    val composeTestRule = createComposeRule()
    private val context = InstrumentationRegistry.getInstrumentation().targetContext

    @Test
    fun tapping_24h_history_button_navigates_to_RefillHistory24h() {
        var capturedEvent: RefillNavigationEvent? = null

        composeTestRule.setContent {
            MainScaffold(
                isControllerLoaded = true,
                navigationRouteSource = ViewNavigationRoutes.refill,
                screenContent = {
                    RefillScreen(
                        refillStateUiState = RefillStateUiState.Success(RefillState.ACTIVE),
                        onNavigateFromRefill = { event -> capturedEvent = event}
                    ) { _ -> }
                },
                onNavigate = {},
                darkTheme = true
            )
        }
        composeTestRule.waitForIdle()

        val label24h = context.getString(R.string.view_refill_events_24h)

        // check if the state is displayed
        composeTestRule.onNodeWithText(label24h).performClick()

        // Assert the callback was triggered with the expected enum value
        Assert.assertEquals(RefillNavigationEvent.ToRefillHistory24h, capturedEvent)
    }

    @Test
    fun tapping_7d_history_button_navigates_to_RefillHistory7d() {
        var capturedEvent: RefillNavigationEvent? = null

        composeTestRule.setContent {
            MainScaffold(
                isControllerLoaded = true,
                navigationRouteSource = ViewNavigationRoutes.refill,
                screenContent = {
                    RefillScreen(
                        refillStateUiState = RefillStateUiState.Success(RefillState.ACTIVE),
                        onNavigateFromRefill = { event -> capturedEvent = event}
                    ) { _ -> }
                },
                onNavigate = {},
                darkTheme = true
            )
        }
        composeTestRule.waitForIdle()

        val label7d = context.getString(R.string.view_refill_events_7d)

        // check if the state is displayed
        composeTestRule.onNodeWithText(label7d).performClick()

        // Assert the callback was triggered with the expected enum value
        Assert.assertEquals(RefillNavigationEvent.ToRefillHistory7d, capturedEvent)
    }

    @Test
    fun refill_state_is_displayed() {
        composeTestRule.setContent {
            MainScaffold(
                isControllerLoaded = true,
                navigationRouteSource = ViewNavigationRoutes.refill,
                screenContent = {
                    RefillScreen(
                        refillStateUiState = RefillStateUiState.Success(RefillState.ACTIVE),
                        onNavigateFromRefill = { _ -> }
                    ) { _ -> }
                },
                onNavigate = {},
                darkTheme = true
            )
        }
        composeTestRule.waitForIdle()

        // check if the state is displayed
        composeTestRule.onNodeWithText(WebApiParams.ACTIVE).assertIsDisplayed()
    }
}
