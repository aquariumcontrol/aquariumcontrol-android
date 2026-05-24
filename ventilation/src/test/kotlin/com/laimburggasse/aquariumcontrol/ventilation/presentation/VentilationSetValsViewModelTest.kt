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

package com.laimburggasse.aquariumcontrol.ventilation.presentation

import app.cash.turbine.test
import com.laimburggasse.aquariumcontrol.common.data.remote.DataFetchResult
import com.laimburggasse.aquariumcontrol.ventilation.domain.model.VentilationSetVals
import com.laimburggasse.aquariumcontrol.ventilation.domain.repository.VentilationSetValsRepository
import com.laimburggasse.aquariumcontrol.ventilation.testing.VentilationMockData.mockVentilationSetVals
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
class VentilationSetValsViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var repository: VentilationSetValsRepository
    private lateinit var viewModel: VentilationSetValsViewModel

    // Controlled source for repository updates
    private val repositoryFlow = MutableStateFlow<DataFetchResult<VentilationSetVals>>(DataFetchResult.Loading)

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = mockk(relaxed = true)

        // Stub the repository flow
        every { repository.ventilationSetValsFlow } returns repositoryFlow

        viewModel = VentilationSetValsViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is Loading`() = runTest {
        viewModel.ventilationSetValsUiState.test {
            assertEquals(VentilationSetValsUiState.Loading, awaitItem())
        }
    }

    @Test
    fun `successful fetch updates state to Success and validates data`() = runTest {
        viewModel.ventilationSetValsUiState.test {
            awaitItem() // Loading

            // Act
            repositoryFlow.value = DataFetchResult.Success<Any>(mockVentilationSetVals)

            val state = awaitItem()
            assertTrue(state is VentilationSetValsUiState.Success)
            assertEquals(
                mockVentilationSetVals.ventilationSwitchOffTemp,
                (state as VentilationSetValsUiState.Success).ventilationSetVals.ventilationSwitchOffTemp,
                0.0
            )

            // Verify the validity side effect
            assertTrue(viewModel.ventilationSetValsValid.value == true)
        }
    }

    @Test
    fun `invalid data in fetch result sets ventilationSetValsValid to false`() = runTest {
        // Arrange: Create invalid values (Off > On)
        val invalidVals = VentilationSetVals(
            ventilationSwitchOffTemp = 30.0,
            ventilationSwitchOnTemp = 20.0
        )

        viewModel.ventilationSetValsUiState.test {
            awaitItem() // Loading

            // Act
            repositoryFlow.value = DataFetchResult.Success<Any>(invalidVals)
            awaitItem()

            // Assert
            assertFalse(viewModel.ventilationSetValsValid.value == true)
        }
    }

    @Test
    fun `failed fetch updates state to Error`() = runTest {
        val errorMsg = "API Error"
        viewModel.ventilationSetValsUiState.test {
            awaitItem() // Loading

            // Act
            repositoryFlow.value = DataFetchResult.Error(Exception(), errorMsg)

            val state = awaitItem()
            assertTrue(state is VentilationSetValsUiState.Error)
            assertEquals(errorMsg, (state as VentilationSetValsUiState.Error).errorMessage)
        }
    }

    @Test
    fun `ChangeLowerValue event updates local repository using current state`() = runTest {
        // Arrange: Start with success
        repositoryFlow.value = DataFetchResult.Success<Any>(mockVentilationSetVals)
        testDispatcher.scheduler.advanceUntilIdle()

        // Act
        viewModel.onVentilationSetValsEvent(VentilationSetValsUiEvent.ChangeLowerValue(23.0f))

        // Assert
        verify {
            repository.updateLocalValues(match { it.ventilationSwitchOffTemp == 23.0 })
        }
    }

    @Test
    fun `ChangeHigherValue event updates local repository using current state`() = runTest {
        // Arrange: Start with success
        repositoryFlow.value = DataFetchResult.Success<Any>(mockVentilationSetVals)
        testDispatcher.scheduler.advanceUntilIdle()

        // Act
        viewModel.onVentilationSetValsEvent(VentilationSetValsUiEvent.ChangeUpperValue(28.0f))

        // Assert
        verify {
            repository.updateLocalValues(match { it.ventilationSwitchOnTemp == 28.0 })
        }
    }

    @Test
    fun `ChangeRange event updates local repository`() = runTest {
        // Act
        viewModel.onVentilationSetValsEvent(VentilationSetValsUiEvent.ChangeRange(22.0f, 25.0f))

        // Assert
        verify {
            repository.updateLocalValues(match {
                it.ventilationSwitchOffTemp == 22.0 && it.ventilationSwitchOnTemp == 25.0
            })
        }
    }

    @Test
    fun `Update event triggers repository update`() = runTest {
        // Act
        viewModel.onVentilationSetValsEvent(VentilationSetValsUiEvent.Update)
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        coVerify(exactly = 1) { repository.updateValues() }
    }

    @Test
    fun `Reset event triggers repository reset`() = runTest {
        // Act
        viewModel.onVentilationSetValsEvent(VentilationSetValsUiEvent.Reset)

        // Assert
        verify(exactly = 1) { repository.reset() }
    }

    @Test
    fun `Refresh event triggers repository refresh`() = runTest {
        // Act
        viewModel.onVentilationSetValsEvent(VentilationSetValsUiEvent.Refresh)
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        coVerify(exactly = 1) { repository.triggerRefresh() }
    }
}