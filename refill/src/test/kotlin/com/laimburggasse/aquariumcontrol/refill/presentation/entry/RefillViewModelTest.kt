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

package com.laimburggasse.aquariumcontrol.refill.presentation.entry

import app.cash.turbine.test
import com.laimburggasse.aquariumcontrol.common.data.remote.DataFetchResult
import com.laimburggasse.aquariumcontrol.refill.domain.model.RefillState
import com.laimburggasse.aquariumcontrol.refill.domain.repository.RefillRepository
import com.laimburggasse.aquariumcontrol.refill.testing.RefillMockData.mockRefillStateActive
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
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
class RefillViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var repository: RefillRepository
    private lateinit var viewModel: RefillViewModel

    // Controlled sources for the repository mocks
    private val refillStateFlow = MutableStateFlow<DataFetchResult<RefillState>>(DataFetchResult.Loading)
    private val errorEventsFlow = MutableSharedFlow<String>()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = mockk(relaxed = true)

        // Stub repository flows
        every { repository.refillStateFlow } returns refillStateFlow
        every { repository.errorEvents } returns errorEventsFlow

        viewModel = RefillViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is Loading`() = runTest {
        viewModel.refillStateUiState.test {
            assertEquals(RefillStateUiState.Loading, awaitItem())
        }
    }

    @Test
    fun `successful repository fetch updates state to Success`() = runTest {
        viewModel.refillStateUiState.test {
            assertEquals(RefillStateUiState.Loading, awaitItem()) // Initial

            // Act
            refillStateFlow.value = DataFetchResult.Success(mockRefillStateActive)

            val state = awaitItem()
            assertTrue(state is RefillStateUiState.Success)
            assertEquals(mockRefillStateActive, (state as RefillStateUiState.Success).refillState)
        }
    }

    @Test
    fun `failed repository fetch updates state to Error`() = runTest {
        val errorMsg = "Connection Timeout"
        viewModel.refillStateUiState.test {
            awaitItem() // Skip loading

            // Act
            refillStateFlow.value = DataFetchResult.Error(errorMessage = errorMsg, exception = Exception())

            val state = awaitItem()
            assertTrue(state is RefillStateUiState.Error)
            assertEquals(errorMsg, (state as RefillStateUiState.Error).errorMessage)
        }
    }

    @Test
    fun `repository error events are re-emitted as ShowToast screen events`() = runTest {
        viewModel.events.test {
            val errorMsg = "Refill sensor error"

            // Act: Simulate repository emitting an error message
            errorEventsFlow.emit(errorMsg)

            val event = awaitItem()
            assertTrue(event is RefillScreenEvent.ShowToast)
            assertEquals(errorMsg, (event as RefillScreenEvent.ShowToast).message)
        }
    }

    @Test
    fun `onRefillEvent StartControl calls sendStartRequest on repository`() = runTest {
        // Act
        viewModel.onRefillEvent(RefillUiEvent.StartControl)
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        coVerify(exactly = 1) { repository.sendStartRequest() }
    }

    @Test
    fun `onRefillEvent StopControl calls sendStopRequest on repository`() = runTest {
        // Act
        viewModel.onRefillEvent(RefillUiEvent.StopControl)
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        coVerify(exactly = 1) { repository.sendStopRequest() }
    }

    @Test
    fun `onRefillEvent ResetErrors calls sendResetRequest on repository`() = runTest {
        // Act
        viewModel.onRefillEvent(RefillUiEvent.ResetErrors)
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        coVerify(exactly = 1) { repository.sendResetRequest() }
    }

    @Test
    fun `onRefillEvent Refresh calls triggerRefresh on repository`() = runTest {
        // Act
        viewModel.onRefillEvent(RefillUiEvent.Refresh)
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        coVerify(exactly = 1) { repository.triggerRefresh() }
    }
}
