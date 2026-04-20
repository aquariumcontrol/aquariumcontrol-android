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

package com.laimburggasse.aquariumcontrol.overview.presentation

import app.cash.turbine.test
import com.laimburggasse.aquariumcontrol.common.data.remote.DataFetchResult
import com.laimburggasse.aquariumcontrol.overview.domain.model.OverviewData
import com.laimburggasse.aquariumcontrol.overview.domain.repository.OverviewRepository
import com.laimburggasse.aquariumcontrol.overview.testing.OverviewMockData.mockOverviewData
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
class OverviewViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var repository: OverviewRepository
    private lateinit var viewModel: OverviewViewModel

    // Controlled flow to simulate repository emissions
    // Replace 'Any' with your actual OverviewData domain model class
    private val repositoryFlow = MutableStateFlow<DataFetchResult<OverviewData>>(DataFetchResult.Loading)

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = mockk(relaxed = true)

        // Stub the repository to return our controlled flow
        every { repository.overviewData } returns repositoryFlow

        viewModel = OverviewViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is Loading`() = runTest {
        viewModel.overviewUiState.test {
            assertEquals(OverviewUiState.Loading, awaitItem())
        }
    }

    @Test
    fun `successful repository fetch updates state to Success`() = runTest {
        viewModel.overviewUiState.test {
            assertEquals(OverviewUiState.Loading, awaitItem())

            // Act: Emit success from repository
            repositoryFlow.value = DataFetchResult.Success(mockOverviewData)

            val state = awaitItem()
            assertTrue(state is OverviewUiState.Success)
            assertEquals(mockOverviewData, (state as OverviewUiState.Success).overviewData)
        }
    }

    @Test
    fun `failed repository fetch updates state to Error`() = runTest {
        val errorMsg = "Network Timeout"

        viewModel.overviewUiState.test {
            assertEquals(OverviewUiState.Loading, awaitItem())

            // Act: Emit error from repository
            repositoryFlow.value = DataFetchResult.Error(Exception(), errorMsg)

            val state = awaitItem()
            assertTrue(state is OverviewUiState.Error)
            assertEquals(errorMsg, (state as OverviewUiState.Error).errorMessage)
        }
    }

    @Test
    fun `Refresh event calls fetchOverviewData on repository`() = runTest {
        // The init block already calls loadData once, so we check for at least 1 or clear before event

        // Act
        viewModel.onOverviewEvent(OverviewUiEvent.Refresh)

        // Advance scheduler to allow the viewModelScope.launch inside loadData() to execute
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert: 2 calls total (1 from init, 1 from Refresh event)
        coVerify(exactly = 2) { repository.fetchOverviewData() }
    }

    @Test
    fun `init block calls fetchOverviewData on repository`() = runTest {
        // This is verified during setup/initialization
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify(exactly = 1) { repository.fetchOverviewData() }
    }
}