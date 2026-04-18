/*
 *  Copyright 2026 Uwe Martin
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

package com.laimburggasse.aquariumcontrol.feed

import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.laimburggasse.aquariumcontrol.common.presentation.MainScaffold
import com.laimburggasse.aquariumcontrol.common.presentation.ViewNavigationRoutes
import com.laimburggasse.aquariumcontrol.feed.presentation.add.FeedProfileAddScreen
import com.laimburggasse.aquariumcontrol.feed.presentation.add.FeedProfileAddUiEvent
import com.laimburggasse.aquariumcontrol.feed.presentation.add.FeedProfileAddUiState
import org.junit.Assert
import org.junit.Rule
import org.junit.Test

class FeedProfileAddScreenUITest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun tapping_add_button_triggers_FeedProfileAddUiEvent() {
        var capturedEvent: FeedProfileAddUiEvent? = null

        composeTestRule.setContent {
            MainScaffold(
                isControllerLoaded = true,
                navigationRouteSource = ViewNavigationRoutes.controllerAdd,
                screenContent = {
                    FeedProfileAddScreen(
                        feedProfileName = "test",
                        feedProfileAddUiState = FeedProfileAddUiState.Idle,
                        onNavigateFromFeed = { _, _, _, _ -> },
                        onFeedProfileAddEvent = { event -> capturedEvent = event }
                    )
                },
                onNavigate = {},
                darkTheme = true
            )
        }

        composeTestRule.waitForIdle()

        val testTag = "feedProfileAddButton"
        // Find the button for adding a controller, check if it is clickable and click it
        composeTestRule.onNodeWithTag(testTag).assertIsEnabled()
        composeTestRule.onNodeWithTag(testTag).performClick()

        // Assert the callback was triggered with the expected enum value
        Assert.assertEquals(FeedProfileAddUiEvent.Add, capturedEvent)
    }
}