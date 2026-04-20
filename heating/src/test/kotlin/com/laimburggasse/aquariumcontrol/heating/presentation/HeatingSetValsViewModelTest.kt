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

import app.cash.turbine.test
import com.laimburggasse.aquariumcontrol.common.data.remote.DataFetchResult
import com.laimburggasse.aquariumcontrol.heating.domain.model.HeatingSetVals
import com.laimburggasse.aquariumcontrol.heating.domain.repository.HeatingSetValsRepository
import com.laimburggasse.aquariumcontrol.heating.testing.HeatingMockData.mockHeatingSetVals
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HeatingSetValsViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var repository: HeatingSetValsRepository
    private lateinit var viewModel: HeatingSetValsViewModel

    // Controlled source for repository updates
    private val repositoryFlow = MutableStateFlow<DataFetchResult<HeatingSetVals>>(DataFetchResult.Loading)

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = mockk(relaxed = true)

        // Stub the repository flow
        every { repository.heatingSetValsFlow } returns repositoryFlow

        viewModel = HeatingSetValsViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is Loading`() = runTest {
        viewModel.heatingSetValsUiState.test {
            assertEquals(HeatingSetValsUiState.Loading, awaitItem())
        }
    }

    @Test
    fun `successful fetch updates state to Success and validates data`() = runTest {
        viewModel.heatingSetValsUiState.test {
            awaitItem() // Loading

            // Act
            repositoryFlow.value = DataFetchResult.Success(mockHeatingSetVals)

            val state = awaitItem()

            // Ensure all side effects (like updating the validity StateFlow) are processed
            testDispatcher.scheduler.advanceUntilIdle()

            assertTrue(state is HeatingSetValsUiState.Success)
            assertEquals(
                mockHeatingSetVals.heatingSwitchOffTemp,
                (state as HeatingSetValsUiState.Success).heatingSetVals.heatingSwitchOffTemp,
                0.0
            )

            // Verify the validity side effect
            assertTrue(viewModel.heatingSetValsValid.value == true)
        }
    }

    @Test
    fun `invalid data in fetch result sets heatingSetValsValid to false`() = runTest {
        // Arrange: Create invalid values (Off > On)
        val invalidVals = HeatingSetVals(
            heatingSwitchOffTemp = 20.0,
            heatingSwitchOnTemp = 30.0
        )

        viewModel.heatingSetValsUiState.test {
            awaitItem() // Loading

            // Act
            repositoryFlow.value = DataFetchResult.Success(invalidVals)
            awaitItem()

            // Assert
            assertFalse(viewModel.heatingSetValsValid.value == true)
        }
    }

    @Test
    fun `failed fetch updates state to Error`() = runTest {
        val errorMsg = "API Error"
        viewModel.heatingSetValsUiState.test {
            awaitItem() // Loading

            // Act
            repositoryFlow.value = DataFetchResult.Error(Exception(), errorMsg)

            val state = awaitItem()
            assertTrue(state is HeatingSetValsUiState.Error)
            assertEquals(errorMsg, (state as HeatingSetValsUiState.Error).errorMessage)
        }
    }

    @Test
    fun `ChangeLowerValue event updates local repository using current state`() = runTest {
        // Arrange: Start with success
        repositoryFlow.value = DataFetchResult.Success(mockHeatingSetVals)
        testDispatcher.scheduler.advanceUntilIdle()

        // Act
        viewModel.onHeatingSetValsEvent(HeatingSetValsUiEvent.ChangeLowerValue(23.0f))

        // Assert
        verify {
            repository.updateLocalValues(match { it.heatingSwitchOnTemp == 23.0 })
        }
    }

    @Test
    fun `ChangeHigherValue event updates local repository using current state`() = runTest {
        // Arrange: Start with success
        repositoryFlow.value = DataFetchResult.Success(mockHeatingSetVals)
        testDispatcher.scheduler.advanceUntilIdle()

        // Act
        viewModel.onHeatingSetValsEvent(HeatingSetValsUiEvent.ChangeUpperValue(28.0f))

        // Assert
        verify {
            repository.updateLocalValues(match { it.heatingSwitchOffTemp == 28.0 })
        }
    }

    @Test
    fun `ChangeRange event updates local repository`() = runTest {
        // Act
        viewModel.onHeatingSetValsEvent(HeatingSetValsUiEvent.ChangeRange(22.0f, 25.0f))

        // Assert
        verify {
            repository.updateLocalValues(match {
                it.heatingSwitchOnTemp == 22.0 && it.heatingSwitchOffTemp == 25.0
            })
        }
    }

    @Test
    fun `Update event triggers repository update`() = runTest {
        // Act
        viewModel.onHeatingSetValsEvent(HeatingSetValsUiEvent.Update)
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        coVerify(exactly = 1) { repository.updateValues() }
    }

    @Test
    fun `Reset event triggers repository reset`() = runTest {
        // Act
        viewModel.onHeatingSetValsEvent(HeatingSetValsUiEvent.Reset)

        // Assert
        verify(exactly = 1) { repository.reset() }
    }

    @Test
    fun `Refresh event triggers repository refresh`() = runTest {
        // Act
        viewModel.onHeatingSetValsEvent(HeatingSetValsUiEvent.Refresh)
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        coVerify(exactly = 1) { repository.triggerRefresh() }
    }
}