/*
// import.AquariumControl.domain.repository.TimeDataRepository
// import.AquariumControl.data.repository.TimeDataRepositoryImpl
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

package com.laimburggasse.aquariumcontrol.timedata.data

import app.cash.turbine.test
import com.laimburggasse.aquariumcontrol.common.data.remote.DataFetchResult
import com.laimburggasse.aquariumcontrol.common.di.IoDispatcher
import com.laimburggasse.aquariumcontrol.common.domain.SessionManager
import com.laimburggasse.aquariumcontrol.common.testing.ControllerMockData.mockControllerProfileList
import com.laimburggasse.aquariumcontrol.common.testing.MockSessionManagerImpl
import com.laimburggasse.aquariumcontrol.common.util.Logger
import com.laimburggasse.aquariumcontrol.timedata.data.repository.TimeDataRepositoryImpl
import com.laimburggasse.aquariumcontrol.timedata.domain.model.*
import com.laimburggasse.aquariumcontrol.timedata.domain.repository.TimeDataRepository
import com.laimburggasse.aquariumcontrol.timedata.testing.*
import io.mockk.*
import java.util.Calendar




import app.cash.turbine.test







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
class TimeDataRepositoryImplTest {

    private lateinit var repository: TimeDataRepositoryImpl
    private val testDispatcher = StandardTestDispatcher()
    private val testScope = TestScope(testDispatcher)
    private val mockSamples = generateMockTimeData()
    private val mockTimeDataApiService = MockTimeDataApiServiceImpl(
        mockSamples
    )
    private val mockSessionManagerImpl = MockSessionManagerImpl(
        controllerProfiles = mockControllerProfileList
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = TimeDataRepositoryImpl(
            logger = mockk(relaxed = true),
            sessionManager = mockSessionManagerImpl,
            apiService = mockTimeDataApiService,
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
    fun `timeDataSamples emits Loading then Success when controller is selected`() = runTest {
        repository.timeDataSamples.test {
            // Initial state from stateIn
            assertEquals(DataFetchResult.Loading, awaitItem())

            // Act: Select a controller
            mockSessionManagerImpl.selectControllerById(0)

            testDispatcher.scheduler.advanceUntilIdle()

            // The flow first emits Loading from inside flatMapLatest (conflated because it's already Loading)
            // Then it emits the Success result
            val result = awaitItem()
            assertTrue(result is DataFetchResult.Success)
            assertEquals(mockSamples, (result as DataFetchResult.Success).data)

            // Ensure no further emissions occur
            expectNoEvents()
        }
    }

    @Test
    fun `triggerRefresh causes a new fetch`() = runTest {
        mockSessionManagerImpl.selectControllerById(0)

        repository.timeDataSamples.test {
            // Initial Loading and Success
            assertTrue(awaitItem() is DataFetchResult.Loading)
            testDispatcher.scheduler.advanceUntilIdle()
            assertTrue(awaitItem() is DataFetchResult.Success)

            // Act: Trigger refresh
            repository.triggerRefresh()
            
            // Advance just enough to trigger Loading but not finish the API call
            testDispatcher.scheduler.runCurrent()
            assertTrue(awaitItem() is DataFetchResult.Loading)
            
            // Advance to finish the API call
            testDispatcher.scheduler.advanceUntilIdle()
            val result = awaitItem()
            assertTrue(result is DataFetchResult.Success)
            assertEquals(mockSamples, (result as DataFetchResult.Success).data)
        }
    }

    @Test
    fun `timeDataSamples emits Error result on API failure`() = runTest {
        mockSessionManagerImpl.selectControllerById(0)
        
        // induce failure
        mockTimeDataApiService.setMockTimeDataSamples(null)

        repository.timeDataSamples.test {
             assertTrue(awaitItem() is DataFetchResult.Loading)
             testDispatcher.scheduler.advanceUntilIdle()
             val result = awaitItem()
             assertTrue(result is DataFetchResult.Error)
        }
    }
}