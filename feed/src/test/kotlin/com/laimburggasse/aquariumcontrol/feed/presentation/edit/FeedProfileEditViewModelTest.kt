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

package com.laimburggasse.aquariumcontrol.feed.presentation.edit

import android.os.Process
import app.cash.turbine.ReceiveTurbine
import app.cash.turbine.test
import com.laimburggasse.aquariumcontrol.feed.domain.model.FeedProfile
import com.laimburggasse.aquariumcontrol.feed.domain.repository.FeedProfilesRepository
import com.laimburggasse.aquariumcontrol.feed.testing.*
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class FeedProfileEditViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var repository: FeedProfilesRepository
    private lateinit var viewModel: FeedProfileEditViewModel
    private lateinit var mockProfile: FeedProfile

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        mockkStatic(android.util.Log::class)
        mockkStatic(android.graphics.Color::class)
        mockkStatic(Process::class)
        mockkStatic(com.github.mikephil.charting.utils.Utils::class)

        every { android.util.Log.e(any(), any()) } returns 0
        every { android.util.Log.e(any(), any(), any()) } returns 0
        every { android.util.Log.d(any(), any()) } returns 0
        every { Process.myPid() } returns 1
        every { android.graphics.Color.rgb(any<Int>(), any(), any()) } returns 0
        every { com.github.mikephil.charting.utils.Utils.init(any<android.content.Context>()) } just runs
        every { com.github.mikephil.charting.utils.Utils.convertDpToPixel(any<Float>()) } returns 10f

        repository = mockk(relaxed = true)
        mockProfile = createMockFeedProfile().copy(profileID = 10)
        coEvery { repository.findProfileById(10) } returns mockProfile

        viewModel = FeedProfileEditViewModel(repository)
        viewModel.setProfileId(10)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkAll()
    }

    private suspend fun ReceiveTurbine<FeedProfileEditUiState>.skipItemsUntilLoaded(
        expectedId: Int
    ): FeedProfileEditUiState {
        while (true) {
            val item = awaitItem()
            if (item.feedProfile?.profileID == expectedId) return item
        }
    }

    @Test
    fun `setProfileId loads profile from repository`() = runTest {
        viewModel.uiState.test {
            val state = skipItemsUntilLoaded(10)
            assertEquals("Test Profile", state.feedProfile?.profileName)
            coVerify(atLeast = 1) { repository.findProfileById(10) }
        }
    }

    @Test
    fun `DurationChange event updates specific phase duration`() = runTest {
        viewModel.uiState.test {
            skipItemsUntilLoaded(10)
            val menuItemId = mapIndexToMenuItemId(3, FeedProfileEditPhaseType.PAUSE, 0)

            viewModel.onFeedProfileEditUiEvent(
                FeedProfileEditUiEvent.DurationChange(menuItemId, newDuration = 99.0)
            )

            testDispatcher.scheduler.advanceUntilIdle()

            val state = awaitItem()
            assertEquals(99.0, state.feedProfile?.feedProfilePhases?.get(0)?.duration)
        }
    }

    @Test
    fun `SwitchToggle event updates specific hardware state in phase`() = runTest {
        viewModel.uiState.test {
            skipItemsUntilLoaded(10)

            val menuItemId = mapIndexToMenuItemId(3, FeedProfileEditPhaseType.PAUSE, 0)

            // Act: Toggle Skimmer in phase 0 to FALSE (it starts as TRUE in mock data)
            viewModel.onFeedProfileEditUiEvent(
                FeedProfileEditUiEvent.SwitchToggle(
                    menuItemId,
                    toggleButtonType = FeedProfileEditToggleButtonType.Skimmer,
                    isChecked = false
                )
            )
            
            testDispatcher.scheduler.advanceUntilIdle()

            // Skip until we see the change
            var found = false
            for (i in 0..10) {
                val state = awaitItem()
                if (state.feedProfile?.feedProfilePhases?.get(0)?.skimmer == false) {
                    found = true
                    break
                }
            }
            assertTrue("Skimmer toggle not reflected in state", found)
        }
    }

    @Test
    fun `Reset event restores original values from repository`() = runTest {
        viewModel.uiState.test {
            skipItemsUntilLoaded(10)

            val menuItemId = mapIndexToMenuItemId(3, FeedProfileEditPhaseType.PAUSE, 0)

            // Modify state
            viewModel.onFeedProfileEditUiEvent(
                FeedProfileEditUiEvent.DurationChange(menuItemId, newDuration = 5.0)
            )
            awaitItem() // Skip modified state

            // Act
            viewModel.onFeedProfileEditUiEvent(FeedProfileEditUiEvent.Reset)

            val state = awaitItem()
            // Should be back to original mock value (1.0 in createMockFeedProfile for pause01Duration)
            assertEquals(1.0, state.feedProfile?.feedProfilePhases?.get(0)?.duration)
        }
    }

    @Test
    fun `Update event calls repository updateFeedProfile`() = runTest {
        testDispatcher.scheduler.advanceUntilIdle() // Ensure init finished
        viewModel.onFeedProfileEditUiEvent(FeedProfileEditUiEvent.Update)
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify(exactly = 1) { repository.updateFeedProfile(any()) }
    }

    @Test
    fun `Execute event calls repository executeFeedProfile with loaded profile id`() = runTest {
        testDispatcher.scheduler.advanceUntilIdle() // Ensure init finished
        viewModel.onFeedProfileEditUiEvent(FeedProfileEditUiEvent.Execute)
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify(exactly = 1) { repository.executeFeedProfile(10) }
    }

    @Test
    fun `Execute event does nothing when profile is not loaded`() = runTest {
        coEvery { repository.findProfileById(999) } returns null
        viewModel.setProfileId(999)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.onFeedProfileEditUiEvent(FeedProfileEditUiEvent.Execute)
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify(exactly = 0) { repository.executeFeedProfile(any()) }
    }

    @Test
    fun `ExpandChildren event updates expandedItemIds set`() = runTest {
        viewModel.uiState.test {
            skipItemsUntilLoaded(10)

            // Act
            viewModel.onFeedProfileEditUiEvent(FeedProfileEditUiEvent.ExpandChildren(menuItemId = 5))

            val state = awaitItem()
            assertTrue(5 in state.expandedItemIds)

            // Toggle back
            viewModel.onFeedProfileEditUiEvent(FeedProfileEditUiEvent.ExpandChildren(menuItemId = 5))
            val state2 = awaitItem()
            assertFalse(5 in state2.expandedItemIds)
        }
    }
}
