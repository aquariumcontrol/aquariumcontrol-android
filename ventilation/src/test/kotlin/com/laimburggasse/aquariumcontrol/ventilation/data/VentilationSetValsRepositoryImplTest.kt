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

package com.laimburggasse.aquariumcontrol.ventilation.data

import com.laimburggasse.aquariumcontrol.ventilation.testing.VentilationMockData.mockVentilationSetVals
import app.cash.turbine.test
import com.laimburggasse.aquariumcontrol.common.data.remote.DataFetchResult
import com.laimburggasse.aquariumcontrol.common.di.IoDispatcher
import com.laimburggasse.aquariumcontrol.common.domain.SessionManager
import com.laimburggasse.aquariumcontrol.common.testing.ControllerMockData.mockControllerProfileList
import com.laimburggasse.aquariumcontrol.common.testing.MockSessionManagerImpl
import com.laimburggasse.aquariumcontrol.common.util.Logger
import com.laimburggasse.aquariumcontrol.ventilation.data.repository.VentilationSetValsRepositoryImpl
import com.laimburggasse.aquariumcontrol.ventilation.domain.repository.VentilationSetValsRepository
import com.laimburggasse.aquariumcontrol.ventilation.testing.*
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class VentilationSetValsRepositoryImplTest {
    private lateinit var repository: VentilationSetValsRepositoryImpl
    private val testDispatcher = StandardTestDispatcher()
    private val testScope = TestScope(testDispatcher)
    private val mockVentilationSetValsApiService = MockVentilationSetValsApiServiceImpl(
        mockVentilationSetVals,
    )
    private val mockSessionManagerImpl = MockSessionManagerImpl(
        controllerProfiles = mockControllerProfileList
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = VentilationSetValsRepositoryImpl(
            logger = mockk(relaxed = true),
            sessionManager = mockSessionManagerImpl,
            apiService = mockVentilationSetValsApiService,
            repositoryScope = testScope,
            context = IoDispatcher(testDispatcher)
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkAll()
    }

    @Test
    fun `ventilationSetValsFlow emits Loading then Success when controller is selected`() = runTest {

        repository.ventilationSetValsFlow.test {
            // Initial state from stateIn
            assertEquals(DataFetchResult.Loading, awaitItem())

            // Act: Select a controller
            mockSessionManagerImpl.selectControllerById(0)

            testDispatcher.scheduler.advanceUntilIdle()

            // Assert
            val result = awaitItem()

            assertTrue(result is DataFetchResult.Success)
            assertEquals(
                mockVentilationSetVals, (result as DataFetchResult.Success).data
            )

            // Ensure no further emissions occur
            expectNoEvents()
        }
    }

    @Test
    fun `updateLocalValues performs optimistic update`() = runTest {
        mockSessionManagerImpl.selectControllerById(0)
        val updatedSetVals = mockVentilationSetVals.copy(ventilationSwitchOffTemp = 28.0)

        repository.ventilationSetValsFlow.test {
            // Initial Loading and Success
            assertTrue(awaitItem() is DataFetchResult.Loading)
            testDispatcher.scheduler.advanceUntilIdle()
            assertTrue(awaitItem() is DataFetchResult.Success)

            // Act: Update local values
            repository.updateLocalValues(updatedSetVals)

            // Assert: Optimistic update should be emitted immediately
            val result = awaitItem()
            assertTrue(result is DataFetchResult.Success)
            assertEquals(updatedSetVals, (result as DataFetchResult.Success).data)

            expectNoEvents()
        }
    }

    @Test
    fun `updateValues commits values then refreshes`() = runTest {
        mockSessionManagerImpl.selectControllerById(0)
        val updatedSetVals = mockVentilationSetVals.copy(ventilationSwitchOffTemp = 22.0)

        repository.ventilationSetValsFlow.test {
            // Initial Loading and Success
            assertTrue(awaitItem() is DataFetchResult.Loading)
            assertTrue(awaitItem() is DataFetchResult.Success)

            // Act: Update local values and then commit
            repository.updateLocalValues(updatedSetVals)
            assertTrue(awaitItem() is DataFetchResult.Success) // Consume optimistic update

            // Use a slightly different value for network result to ensure an emission after pending is cleared
            val networkResult = updatedSetVals.copy(ventilationSwitchOnTemp = 28.1)
            mockVentilationSetValsApiService.updateMockVentilationSetVals(networkResult)
            
            repository.updateValues()

            testDispatcher.scheduler.advanceUntilIdle()

            // After updateValues delay(500), pending becomes null. networkResult is different from pending.
            val result = awaitItem()
            assertTrue(result is DataFetchResult.Success)
            assertEquals(28.1, (result as DataFetchResult.Success).data.ventilationSwitchOnTemp, 0.0)
            
            expectNoEvents()
        }
    }

    @Test
    fun `updateValues failure emits Error result`() = runTest {
        mockSessionManagerImpl.selectControllerById(0)
        val updatedSetVals = mockVentilationSetVals.copy(ventilationSwitchOffTemp = 28.0)

        repository.ventilationSetValsFlow.test {
            // Initial Loading and Success
            assertTrue(awaitItem() is DataFetchResult.Loading)
            assertTrue(awaitItem() is DataFetchResult.Success)

            // Act: Optimistic update
            repository.updateLocalValues(updatedSetVals)
            assertTrue(awaitItem() is DataFetchResult.Success)

            // Induce failure in next API call
            mockVentilationSetValsApiService.setForceError(force = true)

            repository.updateValues()

            testDispatcher.scheduler.advanceUntilIdle()

            // Skip interim Success if it exists
            var result = awaitItem()
            if (result is DataFetchResult.Success) {
                result = awaitItem()
            }
            assertTrue(result is DataFetchResult.Error)
            
            expectNoEvents()
        }
    }

    @Test
    fun `reset clears pending update`() = runTest {
        mockSessionManagerImpl.selectControllerById(0)
        val updatedSetVals = mockVentilationSetVals.copy(ventilationSwitchOffTemp = 28.0)

        repository.ventilationSetValsFlow.test {
            // Initial Loading and Success
            assertTrue(awaitItem() is DataFetchResult.Loading)
            assertTrue(awaitItem() is DataFetchResult.Success)

            // Act: Update local values
            repository.updateLocalValues(updatedSetVals)
            assertTrue(awaitItem() is DataFetchResult.Success) // Consume optimistic update

            // Reset
            repository.reset()

            // Assert: Should revert to original data
            val result = awaitItem()
            assertTrue(result is DataFetchResult.Success)
            assertEquals(
                mockVentilationSetVals.ventilationSwitchOffTemp, (result as DataFetchResult.Success).data.ventilationSwitchOffTemp, 0.0
            )
            
            expectNoEvents()
        }
    }

    @Test
    fun `triggerRefresh re-fetches data`() = runTest {
        mockSessionManagerImpl.selectControllerById(0)

        repository.ventilationSetValsFlow.test {
            // Initial Loading and Success
            assertTrue(awaitItem() is DataFetchResult.Loading)
            assertTrue(awaitItem() is DataFetchResult.Success)

            // Act: Trigger refresh
            repository.triggerRefresh()

            // Assert: Should see Loading and then Success
            assertTrue(awaitItem() is DataFetchResult.Loading)
            val refreshSuccess = awaitItem()
            assertTrue(refreshSuccess is DataFetchResult.Success)
            assertEquals(
                mockVentilationSetVals,
                (refreshSuccess as DataFetchResult.Success).data
            )
            
            expectNoEvents()
        }
    }
}
