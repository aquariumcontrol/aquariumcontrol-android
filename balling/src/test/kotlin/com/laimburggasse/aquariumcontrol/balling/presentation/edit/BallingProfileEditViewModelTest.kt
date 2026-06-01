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
import com.laimburggasse.aquariumcontrol.balling.testing.BallingMockData
import com.laimburggasse.aquariumcontrol.common.data.remote.DataFetchResult
import io.mockk.*
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
        repository = mockk()

        // Stub the repository to return our controlled flow
        every { repository.ballingProfiles } returns repositoryFlow

        viewModel = BallingProfileEditViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is Loading`() = runTest {
        viewModel.ballingProfileEditUiState.test {
            // StateFlow immediately emits its initial value
            assertEquals(BallingProfileEditUiState.Loading, awaitItem())
        }
    }

    @Test
    fun `successful fetch with profile ID updates state to Success`() = runTest {
        viewModel.ballingProfileEditUiState.test {
            assertEquals(BallingProfileEditUiState.Loading, awaitItem())

            // Act: Set profile ID and simulate repository returning data
            viewModel.setProfileId(BallingMockData.mockBallingProfiles[0].pumpId)
            repositoryFlow.value = DataFetchResult.Success(BallingMockData.mockBallingProfiles)

            val item = awaitItem()
            assertTrue(item is BallingProfileEditUiState.Success)
            assertEquals(BallingMockData.mockBallingProfiles[0], (item as BallingProfileEditUiState.Success).ballingProfile)
        }
    }

    @Test
    fun `ChangeDosingVolume event updates unconfirmedProfile`() = runTest {
        viewModel.ballingProfileEditUiState.test {
            assertEquals(BallingProfileEditUiState.Loading, awaitItem())

            viewModel.setProfileId(BallingMockData.mockBallingProfiles[0].pumpId)
            repositoryFlow.value = DataFetchResult.Success(BallingMockData.mockBallingProfiles)
            assertTrue(awaitItem() is BallingProfileEditUiState.Success)

            // Act
            val newVolume = 5.0
            viewModel.onEvent(BallingProfileEditUiEvent.ChangeDosingVolume(newVolume))

            val item = awaitItem()
            assertTrue(item is BallingProfileEditUiState.Success)
            assertEquals(newVolume, (item as BallingProfileEditUiState.Success).ballingProfile.volume, 0.0)
        }
    }

    @Test
    fun `ConfirmDosingVolume event calls repository updateBallingProfile`() = runTest {
        coEvery { repository.updateBallingProfile(any()) } just Runs

        // Act & Assert within subscription
        viewModel.ballingProfileEditUiState.test {
            assertEquals(BallingProfileEditUiState.Loading, awaitItem())
            
            // Arrange
            viewModel.setProfileId(BallingMockData.mockBallingProfiles[0].pumpId)
            repositoryFlow.value = DataFetchResult.Success(BallingMockData.mockBallingProfiles)
            
            assertTrue(awaitItem() is BallingProfileEditUiState.Success)

            // Act
            val newVolume = 5.0
            viewModel.onEvent(BallingProfileEditUiEvent.ConfirmDosingVolume(newVolume))
            
            testDispatcher.scheduler.advanceUntilIdle()

            // Assert
            coVerify(exactly = 1) { repository.updateBallingProfile(match { it.volume == newVolume }) }
        }
    }
}
