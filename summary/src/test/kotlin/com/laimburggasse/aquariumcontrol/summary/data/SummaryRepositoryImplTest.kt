/*
// import.AquariumControl.domain.repository.SummaryRepository
// import.AquariumControl.data.repository.SummaryRepositoryImpl
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

package com.laimburggasse.aquariumcontrol.summary.data

import com.laimburggasse.aquariumcontrol.common.data.remote.DataFetchResult
import com.laimburggasse.aquariumcontrol.common.di.IoDispatcher
import com.laimburggasse.aquariumcontrol.common.domain.SessionManager
import com.laimburggasse.aquariumcontrol.common.testing.ControllerMockData.mockControllerProfileList
import com.laimburggasse.aquariumcontrol.common.testing.MockSessionManagerImpl
import com.laimburggasse.aquariumcontrol.common.util.Logger
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
class SummaryRepositoryImplTest {

    private lateinit var repository: SummaryRepositoryImpl
    private val testDispatcher = StandardTestDispatcher()
    private val testScope = TestScope(testDispatcher)
    private val mockSummaryDataApiService = MockSummaryDataApiServiceImpl(
        mockSummaryData
    )
    private val mockSessionManagerImpl = MockSessionManagerImpl(
        controllerProfiles = mockControllerProfileList
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = SummaryRepositoryImpl(
            logger = mockk(relaxed = true),
            sessionManager = mockSessionManagerImpl,
            apiService = mockSummaryDataApiService,
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
    fun `summaryData emits Loading then Success when controller is selected and fetch is triggered`() = runTest {
        repository.summaryData.test {
            // Initial state from stateIn
            assertEquals(DataFetchResult.Loading, awaitItem())

            // Act: Select a controller
            mockSessionManagerImpl.selectControllerById(0)
            
            repository.fetchSummaryData()

            testDispatcher.scheduler.advanceUntilIdle()

            // The flow transitions from Loading (initial) to Success.
            // Since it was already Loading, it won't emit Loading again.
            val result = awaitItem()
            assertTrue(result is DataFetchResult.Success)
            assertEquals(mockSummaryData, (result as DataFetchResult.Success).data)

            // Ensure no further emissions occur
            expectNoEvents()
        }
    }

    @Test
    fun `fetchSummaryData causes a re-fetch`() = runTest {
        mockSessionManagerImpl.selectControllerById(0)
        repository.fetchSummaryData()

        repository.summaryData.test {
            // Consume initial states
            assertTrue(awaitItem() is DataFetchResult.Loading)
            testDispatcher.scheduler.advanceUntilIdle()
            assertTrue(awaitItem() is DataFetchResult.Success)

            // Act: Trigger refresh
            repository.fetchSummaryData()
            
            // Advance just enough to trigger Loading but not finish the API call
            testDispatcher.scheduler.runCurrent()
            assertTrue(awaitItem() is DataFetchResult.Loading)
            
            // Advance to finish the API call
            testDispatcher.scheduler.advanceUntilIdle()
            val result = awaitItem()
            assertTrue(result is DataFetchResult.Success)
            assertEquals(mockSummaryData, (result as DataFetchResult.Success).data)
        }
    }

    @Test
    fun `summaryData emits Error result on API failure`() = runTest {
        mockSessionManagerImpl.selectControllerById(0)
        
        // induce failure
        mockSummaryDataApiService.setSummaryData(null)

        repository.summaryData.test {
             assertEquals(DataFetchResult.Loading, awaitItem())
             
             repository.fetchSummaryData()
             testDispatcher.scheduler.advanceUntilIdle()
             
             // In this case, it goes from Loading (initial) to Error.
             // Loading from flatMapLatest is conflated.
             val result = awaitItem()
             assertTrue(result is DataFetchResult.Error)
        }
    }
}