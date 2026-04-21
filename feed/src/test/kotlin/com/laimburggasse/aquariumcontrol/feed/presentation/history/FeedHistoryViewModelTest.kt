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

package com.laimburggasse.aquariumcontrol.feed.presentation.history

import app.cash.turbine.test
import com.laimburggasse.aquariumcontrol.feed.domain.model.FeedHistoryLogEntry
import com.laimburggasse.aquariumcontrol.feed.domain.repository.FeedHistoryRepository
import com.laimburggasse.aquariumcontrol.feed.testing.FeedMockData.mockFeedHistoryLogEntries
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
class FeedHistoryViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var repository: FeedHistoryRepository<FeedHistoryLogEntry>
    private lateinit var viewModel: FeedHistoryViewModel

    // Flow used to push updates to the ViewModel
    private val repositoryFlow = MutableStateFlow<DataFetchResult<List<FeedHistoryLogEntry>>>(DataFetchResult.Loading)

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = mockk(relaxed = true)

        // Stub the repository flow
        every { repository.feedHistoryEventFlow } returns repositoryFlow

        viewModel = FeedHistoryViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is Loading`() = runTest {
        viewModel.feedHistoryUiState.test {
            assertEquals(FeedHistoryUiState.Loading, awaitItem())
        }
    }

    @Test
    fun `successful fetch with data updates state to Success`() = runTest {
        viewModel.feedHistoryUiState.test {
            assertEquals(FeedHistoryUiState.Loading, awaitItem())

            // Act: Push success data
            repositoryFlow.value = DataFetchResult.Success(mockFeedHistoryLogEntries)

            val item = awaitItem()
            assertTrue(item is FeedHistoryUiState.Success)
            assertEquals(2, (item as FeedHistoryUiState.Success).feedHistoryLogEntries.size)
        }
    }

    @Test
    fun `successful fetch with empty list updates state to Empty`() = runTest {
        viewModel.feedHistoryUiState.test {
            awaitItem() // Skip Loading

            // Act: Push empty list
            repositoryFlow.value = DataFetchResult.Success(emptyList())

            assertEquals(FeedHistoryUiState.Empty, awaitItem())
        }
    }

    @Test
    fun `failed fetch updates state to Error`() = runTest {
        val errorMsg = "Network Timeout"
        viewModel.feedHistoryUiState.test {
            awaitItem() // Skip Loading

            // Act: Push error
            repositoryFlow.value = DataFetchResult.Error(
                errorMessage = errorMsg,
                exception = Exception()
            )

            val item = awaitItem()
            assertTrue(item is FeedHistoryUiState.Error)
            assertEquals(errorMsg, (item as FeedHistoryUiState.Error).errorMessage)
        }
    }

    @Test
    fun `Refresh event triggers triggerRefresh in repository`() = runTest {
        // Act
        viewModel.onEvent(FeedHistoryUiEvent.Refresh)

        // Wait for the viewModelScope.launch to execute
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        coVerify(exactly = 1) { repository.triggerRefresh() }
    }

    @Test
    fun `SetRange event triggers setHistoryTimeRange in repository`() = runTest {
        // Act: Request full week
        viewModel.onEvent(FeedHistoryUiEvent.SetRange(showFullWeek = true))

        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        coVerify(exactly = 1) { repository.setHistoryTimeRange(fetchFullWeek = true) }
    }
}