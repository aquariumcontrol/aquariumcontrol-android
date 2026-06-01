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

package com.laimburggasse.aquariumcontrol.heating.data

import app.cash.turbine.test
import com.laimburggasse.aquariumcontrol.common.data.remote.DataFetchResult
import com.laimburggasse.aquariumcontrol.common.di.IoDispatcher
import com.laimburggasse.aquariumcontrol.common.domain.SessionManager
import com.laimburggasse.aquariumcontrol.common.testing.ControllerMockData.mockControllerProfileList
import com.laimburggasse.aquariumcontrol.common.testing.MockSessionManagerImpl
import com.laimburggasse.aquariumcontrol.common.util.Logger
import com.laimburggasse.aquariumcontrol.heating.data.repository.HeatingSetValsRepositoryImpl
import com.laimburggasse.aquariumcontrol.heating.domain.model.HeatingSetVals
import com.laimburggasse.aquariumcontrol.heating.domain.repository.HeatingSetValsRepository
import com.laimburggasse.aquariumcontrol.heating.testing.HeatingMockData.mockHeatingSetVals
import com.laimburggasse.aquariumcontrol.heating.testing.MockHeatingSetValsApiServiceImpl
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
class HeatingSetValsRepositoryImplTest {

    private lateinit var repository: HeatingSetValsRepositoryImpl
    private val testDispatcher = StandardTestDispatcher()
    private val testScope = TestScope(testDispatcher)
    private val mockHeatingSetValsApiService = MockHeatingSetValsApiServiceImpl(
        mockHeatingSetVals,
    )
    private val mockSessionManagerImpl = MockSessionManagerImpl(
        controllerProfiles = mockControllerProfileList
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = HeatingSetValsRepositoryImpl(
            logger = mockk(relaxed = true),
            sessionManager = mockSessionManagerImpl,
            apiService = mockHeatingSetValsApiService,
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
    fun `heatingSetValsFlow emits Loading then Success when controller is selected`() = runTest {

        repository.heatingSetValsFlow.test {
            // Initial state from stateIn
            assertEquals(DataFetchResult.Loading, awaitItem())

            // Act: Select a controller
            mockSessionManagerImpl.selectControllerById(0)

            testDispatcher.scheduler.advanceUntilIdle()

            // Assert
            val result = awaitItem()

            assertTrue(result is DataFetchResult.Success)
            assertEquals(mockHeatingSetVals, (result as DataFetchResult.Success).data)

            // Ensure no further emissions (like a second refresh or an error) occur
            expectNoEvents()
        }
    }

    @Test
    fun `updateLocalValues performs optimistic update then refreshes`() = runTest {
        mockSessionManagerImpl.selectControllerById(0)
        val updatedSwitchOnTemp = 24.0
        val updatedHeatingSetVals = mockHeatingSetVals.copy(heatingSwitchOnTemp = updatedSwitchOnTemp)
        repository.heatingSetValsFlow.test {
            assertTrue(awaitItem() is DataFetchResult.Loading)
            assertTrue(awaitItem() is DataFetchResult.Success)

            // Act
            mockHeatingSetValsApiService.updateMockHeatingSetVals(updatedHeatingSetVals)
            repository.updateLocalValues(updatedHeatingSetVals)

            // Optimistic update should emit Success immediately
            val result = awaitItem()
            assertTrue(result is DataFetchResult.Success)
            assertEquals(updatedSwitchOnTemp, (result as DataFetchResult.Success).data.heatingSwitchOnTemp, 0.0)

            // Ensure no further emissions (like a second refresh or an error) occur
            expectNoEvents()
        }
    }

    @Test
    fun `updateValues performs remote update then refreshes`() = runTest {
        mockSessionManagerImpl.selectControllerById(0)
        val updatedSwitchOffTemp = 26.0
        val updatedHeatingSetVals = mockHeatingSetVals.copy(heatingSwitchOffTemp = updatedSwitchOffTemp)
        repository.heatingSetValsFlow.test {
            assertTrue(awaitItem() is DataFetchResult.Loading)
            assertTrue(awaitItem() is DataFetchResult.Success)

            // Act
            mockHeatingSetValsApiService.updateMockHeatingSetVals(updatedHeatingSetVals)
            repository.updateLocalValues(updatedHeatingSetVals)
            
            // Optimistic update
            assertTrue(awaitItem() is DataFetchResult.Success)

            // Act: Remote update
            // We use a slightly different value for the network result to force an emission after pending is cleared
            val networkResult = updatedHeatingSetVals.copy(heatingSwitchOnTemp = 23.1)
            mockHeatingSetValsApiService.updateMockHeatingSetVals(networkResult)
            
            repository.updateValues()

            testDispatcher.scheduler.advanceUntilIdle()
            
            // After updateValues delay(500), pendingUpdate becomes null.
            // Since networkResult is different from pending, we get a new emission.
            val result = awaitItem()
            assertTrue(result is DataFetchResult.Success)
            assertEquals(23.1, (result as DataFetchResult.Success).data.heatingSwitchOnTemp, 0.0)

            // Ensure no further emissions (like a second refresh or an error) occur
            expectNoEvents()
        }
    }

    @Test
    fun `triggerRefresh from error state clearing failureResult`() = runTest {
        mockSessionManagerImpl.selectControllerById(0)

        repository.heatingSetValsFlow.test {
            assertTrue(awaitItem() is DataFetchResult.Loading)
            assertTrue(awaitItem() is DataFetchResult.Success)

            // Act: Induce error
            mockHeatingSetValsApiService.setForceError(true)
            repository.triggerRefresh()

            assertTrue(awaitItem() is DataFetchResult.Loading)
            val errorResult = awaitItem()
            assertTrue(errorResult is DataFetchResult.Error)

            // Act: Refresh
            mockHeatingSetValsApiService.setForceError(false)
            repository.triggerRefresh()

            // When failureResult is cleared, it triggers network fetch
            assertTrue(awaitItem() is DataFetchResult.Loading)
            val successResult = awaitItem()
            assertTrue(successResult is DataFetchResult.Success)
            assertEquals(mockHeatingSetVals, (successResult as DataFetchResult.Success).data)

            // Ensure no further emissions (like a second refresh or an error) occur
            expectNoEvents()
        }
    }

    @Test
    fun `reset clears state`() = runTest {
        mockSessionManagerImpl.selectControllerById(0)

        repository.heatingSetValsFlow.test {
            assertTrue(awaitItem() is DataFetchResult.Loading)
            assertTrue(awaitItem() is DataFetchResult.Success)

            // Act: Optimistic update
            repository.updateLocalValues(mockHeatingSetVals.copy(heatingSwitchOnTemp = 99.0))
            assertTrue(awaitItem() is DataFetchResult.Success)

            // Act: Reset
            repository.reset()

            // reset() clears pendingUpdate, so it should emit the last network state (Success)
            val result = awaitItem()
            assertTrue(result is DataFetchResult.Success)
            assertEquals(mockHeatingSetVals.heatingSwitchOnTemp, (result as DataFetchResult.Success).data.heatingSwitchOnTemp, 0.0)

            // Ensure no further emissions
            expectNoEvents()
        }
    }

    @Test
    fun `triggerRefresh from success state emits refreshTrigger`() = runTest {
        mockSessionManagerImpl.selectControllerById(0)

        repository.heatingSetValsFlow.test {
            assertTrue(awaitItem() is DataFetchResult.Loading)
            assertTrue(awaitItem() is DataFetchResult.Success)

            // Act: Refresh
            repository.triggerRefresh()

            assertTrue(awaitItem() is DataFetchResult.Loading)
            assertTrue(awaitItem() is DataFetchResult.Success)

            // Ensure no further emissions (like a second refresh or an error) occur
            expectNoEvents()
        }
    }

    @Test
    fun `updateValues failure emits Error result`() = runTest {
        mockSessionManagerImpl.selectControllerById(0)

        repository.heatingSetValsFlow.test {
            assertTrue(awaitItem() is DataFetchResult.Loading)
            assertTrue(awaitItem() is DataFetchResult.Success)

            // Act: induce error with next API call
            mockHeatingSetValsApiService.setForceError(true)

            repository.updateValues()

            testDispatcher.scheduler.advanceUntilIdle()

            val result = awaitItem()
            assertTrue(result is DataFetchResult.Error)

            // Ensure no further emissions (like a second refresh or an error) occur
            expectNoEvents()
        }
    }
}
