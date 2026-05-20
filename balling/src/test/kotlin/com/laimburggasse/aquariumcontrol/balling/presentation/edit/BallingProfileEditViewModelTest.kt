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

package com.laimburggasse.aquariumcontrol.balling.presentation.edit

import app.cash.turbine.test
import com.laimburggasse.aquariumcontrol.balling.domain.model.BallingProfile
import com.laimburggasse.aquariumcontrol.balling.domain.repository.BallingProfilesRepository
import com.laimburggasse.aquariumcontrol.balling.testing.BallingMockData.mockBallingProfiles
import com.laimburggasse.aquariumcontrol.common.data.remote.DataFetchResult
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
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class BallingProfileEditViewModelTest {
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var repository: BallingProfilesRepository
    private lateinit var viewModel: BallingProfileEditViewModel

    private val repositoryFlow = MutableStateFlow<DataFetchResult<List<BallingProfile>>>(DataFetchResult.Loading)

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = mockk(relaxed = true)

        // Default behavior: return the repository flow
        every { repository.ballingProfiles } returns repositoryFlow
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is Loading`() = runTest {
        viewModel = BallingProfileEditViewModel(repository)
        // Profile ID not set yet

        viewModel.ballingProfileEditUiState.test {
            assertEquals(BallingProfileEditUiState.Loading, awaitItem())
        }
    }

    @Test
    fun `successful repository fetch updates UI state to Success`() = runTest {
        repositoryFlow.value = DataFetchResult.Success(mockBallingProfiles)
        val mockBallingProfile = mockBallingProfiles.first()

        viewModel = BallingProfileEditViewModel(repository)
        viewModel.setProfileId(mockBallingProfile.pumpId)

        viewModel.ballingProfileEditUiState.test {
            // First item is always Loading
            assertTrue(awaitItem() is BallingProfileEditUiState.Loading)

            val item = awaitItem()
            assertTrue(item is BallingProfileEditUiState.Success)
            val actualBallingProfile = (item as BallingProfileEditUiState.Success).ballingProfile
            assertEquals(
                mockBallingProfile.pumpId,
                actualBallingProfile.pumpId,
            )
            assertEquals(
                mockBallingProfile.volume,
                actualBallingProfile.volume,
                0.0
            )
            assertEquals(
                mockBallingProfile.label,
                actualBallingProfile.label
            )
        }
    }

    @Test
    fun `onVolumeChange updates unconfirmed edits without affecting repository`() = runTest {
        repositoryFlow.value = DataFetchResult.Success(mockBallingProfiles)

        viewModel = BallingProfileEditViewModel(repository)
        viewModel.setProfileId(1)

        viewModel.ballingProfileEditUiState.test {
            // First item is always Loading
            assertTrue(awaitItem() is BallingProfileEditUiState.Loading)

            awaitItem() // Initial Success

            val newDosingValue = 5.5
            viewModel.onEvent(BallingProfileEditUiEvent.ChangeDosingVolume(newDosingValue))

            val updatedState = awaitItem()
            assertTrue(updatedState is BallingProfileEditUiState.Success)
            val actualBallingProfile = (updatedState as BallingProfileEditUiState.Success).ballingProfile
            assertEquals(
                newDosingValue,
                actualBallingProfile.volume,
                0.0
            )

            // Verify repository was NOT called yet
            coVerify(exactly = 0) { repository.updateBallingProfile(any()) }
        }
    }

    @Test
    fun `onConfirmDosing triggers repository update`() = runTest {
        repositoryFlow.value = DataFetchResult.Success(mockBallingProfiles)

        viewModel = BallingProfileEditViewModel(repository)
        viewModel.setProfileId(1)

        val newDosingValue = 5.5

        viewModel.ballingProfileEditUiState.test {
            // First item is always Loading
            assertTrue(awaitItem() is BallingProfileEditUiState.Loading)

            awaitItem() // Skip initial success

            viewModel.onEvent(BallingProfileEditUiEvent.ConfirmDosingVolume(newDosingValue))

            // Give the ViewModel coroutine a chance to execute
            testDispatcher.scheduler.advanceUntilIdle()
        }

        coVerify(exactly = 1) {
            repository.updateBallingProfile(
                match { it.pumpId == 1 && it.volume == newDosingValue }
            )
        }
    }

    @Test
    fun `profile not found in list results in NotFound state`() = runTest {
        repositoryFlow.value = DataFetchResult.Success(mockBallingProfiles)

        viewModel = BallingProfileEditViewModel(repository)
        viewModel.setProfileId(999) // Non-existent ID

        viewModel.ballingProfileEditUiState.test {
            // First item is always Loading
            assertTrue(awaitItem() is BallingProfileEditUiState.Loading)

            assertTrue(awaitItem() is BallingProfileEditUiState.NotFound)
        }
    }
}
