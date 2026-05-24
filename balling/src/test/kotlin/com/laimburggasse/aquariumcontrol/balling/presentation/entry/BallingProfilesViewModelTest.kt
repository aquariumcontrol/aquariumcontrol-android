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

package com.laimburggasse.aquariumcontrol.balling.presentation.entry

import app.cash.turbine.test
import com.laimburggasse.aquariumcontrol.balling.domain.model.BallingProfile
import com.laimburggasse.aquariumcontrol.balling.domain.repository.BallingProfilesRepository
import com.laimburggasse.aquariumcontrol.balling.testing.*
import com.laimburggasse.aquariumcontrol.common.data.remote.DataFetchResult
import io.mockk.*


import app.cash.turbine.test
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
class BallingProfilesViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var repository: BallingProfilesRepository
    private lateinit var viewModel: BallingProfilesViewModel
    private val repositoryFlow = MutableStateFlow<DataFetchResult<List<BallingProfile>>>(DataFetchResult.Loading)

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = mockk()

        // Stub the repository to return our controlled flow
        every { repository.ballingProfiles } returns repositoryFlow

        viewModel = BallingProfilesViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is Loading`() = runTest {
        viewModel.ballingProfilesUiState.test {
            // StateFlow immediately emits its initial value
            assertEquals(BallingProfilesUiState.Loading, awaitItem())
        }
    }

    @Test
    fun `successful fetch with data updates state to Success`() = runTest {
        viewModel.ballingProfilesUiState.test {
            awaitItem() // Skip Loading

            // Act: Simulate repository returning data
            repositoryFlow.value = DataFetchResult.Success(BallingMockData.mockBallingProfiles)

            val item = awaitItem()
            assertTrue(item is BallingProfilesUiState.Success)
            assertEquals(2, (item as BallingProfilesUiState.Success).ballingProfiles.size)
        }
    }

    @Test
    fun `successful fetch with empty data updates state to Empty`() = runTest {
        viewModel.ballingProfilesUiState.test {
            awaitItem() // Skip Loading

            // Act: Simulate repository returning an empty list
            repositoryFlow.value = DataFetchResult.Success(emptyList())

            assertEquals(BallingProfilesUiState.Empty, awaitItem())
        }
    }

    @Test
    fun `error fetch updates state to Error`() = runTest {
        viewModel.ballingProfilesUiState.test {
            awaitItem() // Skip Loading

            // Act: Simulate repository returning an error
            val errorMessage = "Network Error"
            repositoryFlow.value = DataFetchResult.Error(Exception(errorMessage), errorMessage)

            val item = awaitItem()
            assertTrue(item is BallingProfilesUiState.Error)
            assertEquals(errorMessage, (item as BallingProfilesUiState.Error).errorMessage)
        }
    }

    @Test
    fun `onRefresh calls repository triggerRefresh`() = runTest {
        coEvery { repository.triggerRefresh() } just Runs

        // Act
        viewModel.onRefresh()

        // Ensure the viewModelScope.launch has finished executing
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        coVerify(exactly = 1) { repository.triggerRefresh() }
    }
}