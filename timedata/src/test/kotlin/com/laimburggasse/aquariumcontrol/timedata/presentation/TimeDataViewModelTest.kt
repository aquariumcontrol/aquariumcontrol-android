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

package com.laimburggasse.aquariumcontrol.timedata.presentation

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
class TimeDataViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var repository: TimeDataRepository
    private lateinit var viewModel: TimeDataViewModel

    // Controlled flow to simulate repository emissions
    private val repositoryFlow = MutableStateFlow<DataFetchResult<List<TimeDataSample>>>(DataFetchResult.Loading)

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        // Mock static Android methods used by MPAndroidChart during UI state mapping
        mockkStatic(android.graphics.Color::class)
        mockkStatic(android.util.Log::class)

        every { android.graphics.Color.rgb(any<Int>(), any(), any()) } returns 0
        every { android.graphics.Color.argb(any<Int>(), any(), any(), any()) } returns 0
        every { android.util.Log.e(any(), any()) } returns 0
        every { android.util.Log.e(any(), any(), any()) } returns 0

        repository = mockk(relaxed = true)

        every { repository.timeDataSamples } returns repositoryFlow

        viewModel = TimeDataViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkStatic(android.graphics.Color::class)
    }

    @Test
    fun `initial state is Loading`() = runTest {
        viewModel.timeDataUiState.test {
            assertEquals(TimeDataUiState.Loading, awaitItem())
        }
    }

    @Test
    fun `successful fetch with data updates state to Success`() = runTest {
        viewModel.timeDataUiState.test {
            assertEquals(TimeDataUiState.Loading, awaitItem())

            val mockData = generateMockTimeData()
            // Act: Emit success with data
            repositoryFlow.value = DataFetchResult.Success(mockData)

            val state = awaitItem()
            assertTrue(state is TimeDataUiState.Success)
        }
    }

    @Test
    fun `successful fetch with empty list updates state to Empty`() = runTest {
        viewModel.timeDataUiState.test {
            awaitItem() // Skip loading

            // Act: Emit success with empty list
            repositoryFlow.value = DataFetchResult.Success(emptyList())

            val state = awaitItem()
            assertTrue(state is TimeDataUiState.Empty)
        }
    }

    @Test
    fun `failed fetch updates state to Error`() = runTest {
        val errorMsg = "Failed to load time data"

        viewModel.timeDataUiState.test {
            awaitItem() // Skip loading

            // Act: Emit error
            repositoryFlow.value = DataFetchResult.Error(Exception(), errorMsg)

            val state = awaitItem()
            assertTrue(state is TimeDataUiState.Error)
            assertEquals(errorMsg, (state as TimeDataUiState.Error).errorMessage)
        }
    }

    @Test
    fun `Refresh event calls triggerRefresh on repository`() = runTest {
        // Act
        viewModel.onTimeDataEvent(TimeDataUiEvent.Refresh)

        // Use advanceUntilIdle to ensure the viewModelScope.launch block completes
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        coVerify(exactly = 1) { repository.triggerRefresh() }
    }
}