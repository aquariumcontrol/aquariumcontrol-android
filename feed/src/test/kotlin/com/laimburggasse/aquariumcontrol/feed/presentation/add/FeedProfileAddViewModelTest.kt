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

package com.laimburggasse.aquariumcontrol.feed.presentation.add

import app.cash.turbine.test
import com.laimburggasse.aquariumcontrol.feed.domain.model.FeedProfile
import com.laimburggasse.aquariumcontrol.feed.domain.repository.FeedProfilesRepository
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
class FeedProfileAddViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var repository: FeedProfilesRepository
    private lateinit var viewModel: FeedProfileAddViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = mockk(relaxed = true)
        viewModel = FeedProfileAddViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is correct`() = runTest {
        assertEquals("", viewModel.feedProfileName.value)
        assertFalse(viewModel.feedProfileNameExists.value)
        assertEquals(FeedProfileAddUiState.Idle, viewModel.feedProfileAddUiState.value)
    }

    @Test
    fun `setFeedProfileName updates name and checks existence`() = runTest {
        val mockFeedProfile = FeedProfile(profileID = 0, profileName = "dummy", feedProfilePhases = emptyList())
        // Stub repository to simulate the name already exists
        every { repository.findProfileByName("dummy") } returns mockFeedProfile

        viewModel.feedProfileName.test {
            assertEquals("", awaitItem()) // Initial

            viewModel.onFeedProfileAddEvent(FeedProfileAddUiEvent.SetFeedProfileName(mockFeedProfile.profileName))

            assertEquals(mockFeedProfile.profileName, awaitItem())
            assertTrue(viewModel.feedProfileNameExists.value)
        }
    }

    @Test
    fun `setFeedProfileName with unique name sets nameExists to false`() = runTest {
        val uniqueName = "Unique Name"
        every { repository.findProfileByName(uniqueName) } returns null

        viewModel.onFeedProfileAddEvent(FeedProfileAddUiEvent.SetFeedProfileName(uniqueName))

        assertEquals(uniqueName, viewModel.feedProfileName.value)
        assertFalse(viewModel.feedProfileNameExists.value)
    }

    @Test
    fun `addFeedProfile triggers repository and updates state to Success`() = runTest {
        val profileName = "Test Profile"
        viewModel.onFeedProfileAddEvent(FeedProfileAddUiEvent.SetFeedProfileName(profileName))

        viewModel.feedProfileAddUiState.test {
            assertEquals(FeedProfileAddUiState.Idle, awaitItem()) // Initial

            viewModel.onFeedProfileAddEvent(FeedProfileAddUiEvent.Add)

            // Verify Loading state
            assertEquals(FeedProfileAddUiState.Loading, awaitItem())

            // Advance dispatcher to allow the launch block to finish
            testDispatcher.scheduler.advanceUntilIdle()

            // Verify Success state
            assertEquals(FeedProfileAddUiState.Success, awaitItem())

            // Verify repository was called correctly
            coVerify(exactly = 1) { repository.addFeedProfile(profileName) }
        }
    }

    @Test
    fun `onStateHandled resets UI state to Idle`() = runTest {
        // First get into Success state
        viewModel.onFeedProfileAddEvent(FeedProfileAddUiEvent.Add)
        testDispatcher.scheduler.advanceUntilIdle()
        assertEquals(FeedProfileAddUiState.Success, viewModel.feedProfileAddUiState.value)

        // Act
        viewModel.onFeedProfileAddEvent(FeedProfileAddUiEvent.HandleState)

        // Assert
        assertEquals(FeedProfileAddUiState.Idle, viewModel.feedProfileAddUiState.value)
    }

    @Test
    fun `multiple Add calls are ignored while Loading`() = runTest {
        viewModel.onFeedProfileAddEvent(FeedProfileAddUiEvent.Add)

        // Call add again immediately while state is Loading
        viewModel.onFeedProfileAddEvent(FeedProfileAddUiEvent.Add)

        testDispatcher.scheduler.advanceUntilIdle()

        // Repository should only have been called once
        coVerify(exactly = 1) { repository.addFeedProfile(any()) }
    }
}