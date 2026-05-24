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

package com.laimburggasse.aquariumcontrol.heating.presentation

import app.cash.turbine.test
import com.laimburggasse.aquariumcontrol.common.data.remote.DataFetchResult
import com.laimburggasse.aquariumcontrol.heating.domain.model.HeatingStatsEntry
import com.laimburggasse.aquariumcontrol.heating.domain.repository.HeatingStatsRepository
import com.laimburggasse.aquariumcontrol.heating.testing.HeatingMockData
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
class HeatingStatsViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var repository: HeatingStatsRepository
    private lateinit var viewModel: HeatingStatsViewModel
    private val repositoryFlow = MutableStateFlow<DataFetchResult<List<HeatingStatsEntry>>>(DataFetchResult.Loading)

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = mockk()

        // Stub the repository to return our controlled flow
        every { repository.heatingStatsFlow } returns repositoryFlow

        viewModel = HeatingStatsViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is Loading`() = runTest {
        viewModel.heatingStatsUiState.test {
            // StateFlow immediately emits its initial value
            assertEquals(HeatingStatsUiState.Loading, awaitItem())
        }
    }

    @Test
    fun `successful fetch updates state to Success`() = runTest {
        viewModel.heatingStatsUiState.test {
            awaitItem() // Skip Loading

            // Act: Simulate repository returning data
            repositoryFlow.value = DataFetchResult.Success(HeatingMockData.mockHeatingStatsData)

            val item = awaitItem()
            assertTrue(item is HeatingStatsUiState.Success)
            assertEquals(HeatingMockData.mockHeatingStatsData, (item as HeatingStatsUiState.Success).heatingStats)
        }
    }

    @Test
    fun `error fetch updates state to Error`() = runTest {
        viewModel.heatingStatsUiState.test {
            awaitItem() // Skip Loading

            // Act: Simulate repository returning an error
            val errorMessage = "Network Error"
            repositoryFlow.value = DataFetchResult.Error(Exception(errorMessage), errorMessage)

            val item = awaitItem()
            assertTrue(item is HeatingStatsUiState.Error)
            assertEquals(errorMessage, (item as HeatingStatsUiState.Error).errorMessage)
        }
    }

    @Test
    fun `onHeatingStatsEvent Refresh calls repository triggerRefresh`() = runTest {
        coEvery { repository.triggerRefresh() } just Runs

        // Act
        viewModel.onHeatingStatsEvent(HeatingStatsUiEvent.Refresh)

        // Ensure the viewModelScope.launch has finished executing
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        coVerify(exactly = 1) { repository.triggerRefresh() }
    }
}
