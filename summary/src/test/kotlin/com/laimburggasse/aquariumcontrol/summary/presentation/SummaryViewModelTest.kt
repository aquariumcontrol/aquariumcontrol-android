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

package com.laimburggasse.aquariumcontrol.summary.presentation

import app.cash.turbine.test
import com.laimburggasse.aquariumcontrol.common.data.remote.DataFetchResult
import com.laimburggasse.aquariumcontrol.summary.domain.model.SummaryData
import com.laimburggasse.aquariumcontrol.summary.domain.repository.SummaryRepository
import com.laimburggasse.aquariumcontrol.summary.testing.SummaryMockData.mockSummaryData
import com.laimburggasse.aquariumcontrol.summary.testing.MockSummaryDataApiServiceImpl
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
class SummaryViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var repository: SummaryRepository
    private lateinit var viewModel: SummaryViewModel

    // Controlled flow to simulate repository emissions
    // Replace 'Any' with your actual OverviewData domain model class
    private val repositoryFlow = MutableStateFlow<DataFetchResult<SummaryData>>(DataFetchResult.Loading)

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = mockk(relaxed = true)

        // Stub the repository to return our controlled flow
        every { repository.summaryData } returns repositoryFlow

        viewModel = SummaryViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is Loading`() = runTest {
        viewModel.summaryUiState.test {
            assertEquals(SummaryUiState.Loading, awaitItem())
        }
    }

    @Test
    fun `successful repository fetch updates state to Success`() = runTest {
        viewModel.summaryUiState.test {
            assertEquals(SummaryUiState.Loading, awaitItem())

            // Act: Emit success from repository
            repositoryFlow.value = DataFetchResult.Success(mockSummaryData)

            val state = awaitItem()
            assertTrue(state is SummaryUiState.Success)
            assertEquals(mockSummaryData, (state as SummaryUiState.Success).summaryData)
        }
    }

    @Test
    fun `failed repository fetch updates state to Error`() = runTest {
        val errorMsg = "Network Timeout"

        viewModel.summaryUiState.test {
            assertEquals(SummaryUiState.Loading, awaitItem())

            // Act: Emit error from repository
            repositoryFlow.value = DataFetchResult.Error(Exception(), errorMsg)

            val state = awaitItem()
            assertTrue(state is SummaryUiState.Error)
            assertEquals(errorMsg, (state as SummaryUiState.Error).errorMessage)
        }
    }

    @Test
    fun `Refresh event calls fetchSummaryData on repository`() = runTest {
        // The init block already calls loadData once, so we check for at least 1 or clear before event

        // Act
        viewModel.onSummaryEvent(SummaryUiEvent.Refresh)

        // Advance scheduler to allow the viewModelScope.launch inside loadData() to execute
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert: 2 calls total (1 from init, 1 from Refresh event)
        coVerify(exactly = 2) { repository.fetchSummaryData() }
    }

    @Test
    fun `init block calls fetchSummaryData on repository`() = runTest {
        // This is verified during setup/initialization
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify(exactly = 1) { repository.fetchSummaryData() }
    }
}