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

package com.laimburggasse.aquariumcontrol.controller.presentation.selection

import app.cash.turbine.test
import com.laimburggasse.aquariumcontrol.common.data.local.ControllerProfileDao
import com.laimburggasse.aquariumcontrol.common.domain.ControllerProfile
import com.laimburggasse.aquariumcontrol.common.domain.SessionManager
import com.laimburggasse.aquariumcontrol.controller.testing.ControllerMockData.mockControllerProfileList
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ControllerSelectionViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var dao: ControllerProfileDao
    private lateinit var sessionManager: SessionManager
    private lateinit var viewModel: ControllerSelectionViewModel

    private val profilesFlow = MutableStateFlow<List<ControllerProfile>>(emptyList())
    private val isFirstLoadFlow = MutableStateFlow(true)
    private val defaultIdFlow = MutableStateFlow<Long?>(null)


    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        dao = mockk(relaxed = true)
        sessionManager = mockk(relaxed = true)

        // Stub the reactive sources
        every { dao.getAllControllerProfiles() } returns profilesFlow
        every { sessionManager.isFirstLoad } returns isFirstLoadFlow
        every { sessionManager.defaultControllerProfileId } returns defaultIdFlow

        viewModel = ControllerSelectionViewModel(dao, sessionManager)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state shows empty list and no navigation`() = runTest {
        viewModel.controllerProfiles.test {
            assertEquals(emptyList<ControllerProfile>(), awaitItem())
        }
        viewModel.canStartWaitForDefaultNavigation.test {
            assertFalse(awaitItem())
        }
    }

    @Test
    fun `canStartWaitForDefaultNavigation becomes true when profiles, firstload, and defaultId exist`() = runTest {
        viewModel.canStartWaitForDefaultNavigation.test {
            assertFalse(awaitItem()) // Initial

            // Act: Meet all conditions
            profilesFlow.value = mockControllerProfileList
            defaultIdFlow.value = 1L
            isFirstLoadFlow.value = true

            assertTrue(awaitItem())
        }
    }

    @Test
    fun `progressToDefaultNavigation runs countdown when navigation is possible`() = runTest {
        viewModel.progressToDefaultNavigation.test {
            assertEquals(0f, awaitItem()) // Initial

            // Trigger navigation conditions
            profilesFlow.value = mockControllerProfileList
            defaultIdFlow.value = 1L
            isFirstLoadFlow.value = true

            // The countdown starts. 
            // We can check the first few increments (interval is 50ms)
            assertTrue(awaitItem() > 0f)

            // Advance time by 3 seconds to reach the end
            testDispatcher.scheduler.advanceTimeBy(3000)

            // Collect the final emission
            val finalProgress = expectMostRecentItem()
            assertEquals(1.0f, finalProgress, 0.01f)
        }
    }

    @Test
    fun `progressToDefaultNavigation resets if conditions are no longer met`() = runTest {
        viewModel.progressToDefaultNavigation.test {
            awaitItem() // 0f

            // Start countdown
            profilesFlow.value = mockControllerProfileList
            defaultIdFlow.value = 1L
            isFirstLoadFlow.value = true
            awaitItem() // started > 0f

            // Act: Change firstLoad to false (user interacted or already navigated)
            isFirstLoadFlow.value = false

            assertEquals(0f, awaitItem())
        }
    }

    @Test
    fun `SelectControllerProfile event updates session manager`() = runTest {
        profilesFlow.value = mockControllerProfileList
        val mockControllerProfile1 = mockControllerProfileList[0]

        // Act
        viewModel.onEvent(ControllerSelectionUiEvent.SelectControllerProfile(mockControllerProfile1))

        // Assert
        coVerify(exactly = 1) {
            sessionManager.updateSelectedControllerProfile(mockControllerProfile1)
        }
    }

    @Test
    fun `DeleteControllerSelectionProfile event calls dao`() = runTest {
        profilesFlow.value = mockControllerProfileList
        val mockControllerProfile1 = mockControllerProfileList[0]

        // Act
        viewModel.onEvent(ControllerSelectionUiEvent.DeleteControllerSelectionProfile(mockControllerProfile1))

        // Ensure launch completes
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        coVerify(exactly = 1) {
            dao.deleteControllerProfile(mockControllerProfile1)
        }
    }
}