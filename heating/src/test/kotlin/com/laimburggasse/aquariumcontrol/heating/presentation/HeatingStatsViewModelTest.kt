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

package com.laimburggasse.aquariumcontrol.heating.presentation

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
import com.google.firebase.crashlytics.FirebaseCrashlytics

@OptIn(ExperimentalCoroutinesApi::class)
class HeatingStatsViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var repository: HeatingStatsRepository
    private lateinit var viewModel: HeatingStatsViewModel

    // Controlled flow to simulate repository emissions
    private val repositoryFlow = MutableStateFlow<DataFetchResult<List<HeatingStatsEntry>>>(DataFetchResult.Loading)

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        repository = mockk(relaxed = true)
        every { repository.heatingStatsFlow } returns repositoryFlow

        viewModel = HeatingStatsViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkStatic(android.graphics.Color::class, android.util.Log::class)
    }

    @Test
    fun `initial state is Loading`() = runTest {
        viewModel.heatingStatsUiState.test {
            assertEquals(HeatingStatsUiState.Loading, awaitItem())
        }
    }

    @Test
    fun `successful fetch updates state to Success`() = runTest {
        viewModel.heatingStatsUiState.test {
            assertEquals(HeatingStatsUiState.Loading, awaitItem())

            // Act: Emit success
            repositoryFlow.value = DataFetchResult.Success(mockHeatingStatsData)

            val state = awaitItem()
            assertTrue("Expected Success but got $state", state is HeatingStatsUiState.Success)
            assertEquals(mockHeatingStatsData, (state as HeatingStatsUiState.Success).heatingStats)
        }
    }

    @Test
    fun `failed fetch updates state to Error`() = runTest {
        val errorMsg = "Failed to load heating statistics"

        viewModel.heatingStatsUiState.test {
            awaitItem() // Skip initial loading

            // Act: Emit error
            repositoryFlow.value = DataFetchResult.Error(Exception(), errorMsg)

            val state = awaitItem()
            assertTrue(state is HeatingStatsUiState.Error)
            assertEquals(errorMsg, (state as HeatingStatsUiState.Error).errorMessage)
        }
    }

    @Test
    fun `Refresh event calls triggerRefresh on repository`() = runTest {
        // Act
        viewModel.onHeatingStatsEvent(HeatingStatsUiEvent.Refresh)

        // Advance scheduler to ensure viewModelScope.launch finishes
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        coVerify(exactly = 1) { repository.triggerRefresh() }
    }
}