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

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.ReceiveTurbine
import app.cash.turbine.test
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.laimburggasse.aquariumcontrol.feed.domain.model.FeedProfile
import com.laimburggasse.aquariumcontrol.feed.domain.repository.FeedProfilesRepository
import com.laimburggasse.aquariumcontrol.feed.testing.createMockFeedProfile
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import android.os.Process

@OptIn(ExperimentalCoroutinesApi::class)
class FeedProfileEditViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var repository: FeedProfilesRepository
    private lateinit var savedStateHandle: SavedStateHandle
    private lateinit var viewModel: FeedProfileEditViewModel
    private lateinit var mockProfile: FeedProfile

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        // 1. Mock Statics (Ensure Log is mocked BEFORE Utils)
        mockkStatic(android.util.Log::class)
        mockkStatic(android.graphics.Color::class)
        mockkStatic(Process::class)
        mockkStatic(FirebaseCrashlytics::class)
        // Corrected: Use mockkStatic for Java classes, not mockkObject
        mockkStatic(com.github.mikephil.charting.utils.Utils::class)

        // 2. Stub Log first to swallow library warnings during recording
        every { android.util.Log.e(any(), any()) } returns 0
        every { android.util.Log.e(any(), any(), any()) } returns 0
        every { android.util.Log.d(any(), any()) } returns 0

        // 3. Stub Framework requirements
        every { Process.myPid() } returns 1
        val mockCrashlytics = mockk<FirebaseCrashlytics>(relaxed = true)
        every { FirebaseCrashlytics.getInstance() } returns mockCrashlytics
        every { android.graphics.Color.rgb(any<Int>(), any(), any()) } returns 0

        // 4. Stub MPAndroidChart Utils
        // Stub INIT FIRST to satisfy internal library checks
        every { com.github.mikephil.charting.utils.Utils.init(any<android.content.Context>()) } just runs
        every { com.github.mikephil.charting.utils.Utils.init(any<android.content.res.Resources>()) } just runs

        // Use a simple stub for convertDpToPixel without the complex firstArg matcher
        // to minimize logic during MockK's recording phase
        every { com.github.mikephil.charting.utils.Utils.convertDpToPixel(any<Float>()) } answers { 10f }

        // 5. Initialize Repository and Mocks
        repository = mockk(relaxed = true)
        // Use relaxed = true for SavedStateHandle to prevent unmocked method crashes
        savedStateHandle = mockk(relaxed = true)

        mockProfile = createMockFeedProfile().copy(profileID = 10)

        // 6. Setup data flow
        every { savedStateHandle.get<Int>("feedProfileId") } returns 10
        coEvery { repository.findProfileById(10) } returns mockProfile

        // 7. Instantiate ViewModel LAST
        viewModel = FeedProfileEditViewModel(savedStateHandle, repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkStatic(
            android.graphics.Color::class,
            android.util.Log::class,
            Process::class,
            FirebaseCrashlytics::class,
            com.github.mikephil.charting.utils.Utils::class
        )
    }

    /**
     * Helper to skip the initial empty state and wait for the init block
     * coroutine to finish loading the profile from the repository.
     */
    private suspend fun ReceiveTurbine<FeedProfileEditUiState>.skipItemsUntilLoaded(
        expectedId: Int
    ): FeedProfileEditUiState {
        while (true) {
            val item = awaitItem()
            if (item.feedProfile?.profileID == expectedId) return item
        }
    }

    @Test
    fun `init block loads profile from repository using savedStateHandle ID`() = runTest {
        viewModel.uiState.test {
            val state = skipItemsUntilLoaded(10)
            assertEquals("Test Profile", state.feedProfile?.profileName)
            coVerify(exactly = 1) { repository.findProfileById(10) }
        }
    }

    @Test
    fun `DurationChange event updates specific phase duration`() = runTest {
        viewModel.uiState.test {
            skipItemsUntilLoaded(10)

            // Act: Change phase 0 duration (maps to menuItemId 0 in current implementation)
            viewModel.onFeedProfileEditUiEvent(
                FeedProfileEditUiEvent.DurationChange(menuItemId = 0, newDuration = 99.0)
            )

            // Yield to allow the ViewModel's internal flows to process the change
            testDispatcher.scheduler.advanceUntilIdle()

            val state = awaitItem()
            assertEquals(99.0, state.feedProfile?.feedProfilePhases?.get(0)?.duration)
        }
    }

    @Test
    fun `SwitchToggle event updates specific hardware state in phase`() = runTest {
        viewModel.uiState.test {
            skipItemsUntilLoaded(10)

            // Act: Toggle Skimmer in phase 1
            viewModel.onFeedProfileEditUiEvent(
                FeedProfileEditUiEvent.SwitchToggle(
                    menuItemId = 1,
                    toggleButtonType = FeedProfileEditToggleButtonType.Skimmer,
                    isChecked = true
                )
            )

            val state = awaitItem()
            assertTrue(state.feedProfile?.feedProfilePhases?.get(1)?.skimmer == true)
        }
    }

    @Test
    fun `Reset event restores original values from repository`() = runTest {
        viewModel.uiState.test {
            skipItemsUntilLoaded(10)

            // Modify state
            viewModel.onFeedProfileEditUiEvent(
                FeedProfileEditUiEvent.DurationChange(menuItemId = 0, newDuration = 5.0)
            )
            awaitItem() // Skip modified state

            // Act
            viewModel.onFeedProfileEditUiEvent(FeedProfileEditUiEvent.Reset)

            val state = awaitItem()
            // Should be back to original mock value (10.0 from createMockFeedProfile)
            assertEquals(10.0, state.feedProfile?.feedProfilePhases?.get(0)?.duration)
        }
    }

    @Test
    fun `Update event calls repository updateFeedProfile`() = runTest {
        // Act
        testDispatcher.scheduler.advanceUntilIdle() // Ensure init finished
        viewModel.onFeedProfileEditUiEvent(FeedProfileEditUiEvent.Update)
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        coVerify(exactly = 1) { repository.updateFeedProfile(any()) }
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
