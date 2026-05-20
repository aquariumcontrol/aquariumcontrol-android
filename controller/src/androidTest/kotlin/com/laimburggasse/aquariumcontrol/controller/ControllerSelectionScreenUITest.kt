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

import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.test.hasAnyAncestor
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.platform.app.InstrumentationRegistry
import com.laimburggasse.aquariumcontrol.controller.presentation.navigation.ControllerNavigationEvent
import com.laimburggasse.aquariumcontrol.controller.presentation.selection.ControllerSelectionScreen
import com.laimburggasse.aquariumcontrol.controller.testing.ControllerMockData.mockControllerProfileList
import org.junit.Assert
import org.junit.Rule
import org.junit.Test

class ControllerSelectionScreenUITest {
    @get:Rule
    val composeTestRule = createComposeRule()

    private val context = InstrumentationRegistry.getInstrumentation().targetContext

    @Test
    fun tapping_ControllerProfileSummary_navigates_to_SummaryScreen() {
        var capturedEvent: ControllerNavigationEvent? = null

        val targetProfile = mockControllerProfileList.first()

        composeTestRule.setContent {
            ControllerSelectionScreen(
                controllerProfileList = mockControllerProfileList,
                onEvent = {},
                progressToDefaultNavigation = remember { mutableFloatStateOf(value = 0.3f) },
                onNavigateFromController = { event, _ -> capturedEvent = event},
                defaultProfileId = remember { mutableLongStateOf(value = 1L) },
                canStartWaitForNavigation = remember { mutableStateOf(value = true) },
            )
        }

        // Find the profile summary by its label and tap it
        composeTestRule.onNodeWithText(targetProfile.name).performClick()

        // Assert navigation to ControllerEditScreen occurred with the correct ID
        Assert.assertEquals(ControllerNavigationEvent.ToSummary, capturedEvent)
    }

    @Test
    fun tapping_EditIcon_navigates_to_ControllerEditScreen() {
        var capturedEvent: ControllerNavigationEvent? = null

        val editDescription = context.getString(R.string.edit)
        val targetProfile = mockControllerProfileList.first()

        composeTestRule.setContent {
            ControllerSelectionScreen(
                controllerProfileList = mockControllerProfileList,
                onEvent = {},
                progressToDefaultNavigation = remember { mutableFloatStateOf(value = 0.3f) },
                onNavigateFromController = { event, _ -> capturedEvent = event},
                defaultProfileId = remember { mutableLongStateOf(value = 1L) },
                canStartWaitForNavigation = remember { mutableStateOf(value = true) },
            )
        }

        // Find the 'Edit' node that is a child/descendant of the specific profile name
        composeTestRule
            .onNode(
                hasContentDescription(editDescription) and
                        hasAnyAncestor(hasText(targetProfile.name))
            )
            .performClick()

        // Assert navigation to ControllerEditScreen occurred with the correct ID
        Assert.assertEquals(ControllerNavigationEvent.ToControllerEdit, capturedEvent)
    }
}