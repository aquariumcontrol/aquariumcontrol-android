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

package com.laimburggasse.aquariumcontrol.overview.data

import app.cash.turbine.test
import com.google.firebase.Firebase
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.crashlytics.crashlytics
import com.laimburggasse.aquariumcontrol.common.data.remote.DataFetchResult
import com.laimburggasse.aquariumcontrol.common.testing.ControllerMockData.mockControllerProfileList
import com.laimburggasse.aquariumcontrol.common.testing.MockSessionManagerImpl
import com.laimburggasse.aquariumcontrol.overview.testing.OverviewMockData.mockOverviewData
import com.laimburggasse.aquariumcontrol.overview.testing.MockOverviewDataApiServiceImpl
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkAll
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
class OverviewRepositoryImplTest {

    private lateinit var repository: OverviewRepositoryImpl
    private val testDispatcher = StandardTestDispatcher()
    private val testScope = TestScope(testDispatcher)
    private val mockOverviewDataApiService = MockOverviewDataApiServiceImpl(
        mockOverviewData
    )
    private val mockSessionManagerImpl = MockSessionManagerImpl(
        controllerProfiles = mockControllerProfileList
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        // Mock Firebase Crashlytics statics
        mockkStatic(FirebaseCrashlytics::class)
        val mockCrashlytics = mockk<FirebaseCrashlytics>(relaxed = true)
        every { FirebaseCrashlytics.getInstance() } returns mockCrashlytics
        every { Firebase.crashlytics } returns mockCrashlytics

        repository = OverviewRepositoryImpl(
            sessionManager = mockSessionManagerImpl,
            apiService = mockOverviewDataApiService,
            repositoryScope = testScope,
            context = testDispatcher
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkAll()
    }

    @Test
    fun `overviewData emits Loading then Success when controller is selected and fetch is triggered`() = runTest {
        repository.overviewData.test {
            // Initial state from stateIn
            assertEquals(DataFetchResult.Loading, awaitItem())

            // Act: Select a controller
            mockSessionManagerImpl.selectControllerById(0)
            
            repository.fetchOverviewData()

            testDispatcher.scheduler.advanceUntilIdle()

            // The flow transitions from Loading (initial) to Success.
            // Since it was already Loading, it won't emit Loading again.
            val result = awaitItem()
            assertTrue(result is DataFetchResult.Success)
            assertEquals(mockOverviewData, (result as DataFetchResult.Success).data)

            // Ensure no further emissions occur
            expectNoEvents()
        }
    }

    @Test
    fun `fetchOverviewData causes a re-fetch`() = runTest {
        mockSessionManagerImpl.selectControllerById(0)
        repository.fetchOverviewData()

        repository.overviewData.test {
            // Consume initial states
            assertTrue(awaitItem() is DataFetchResult.Loading)
            testDispatcher.scheduler.advanceUntilIdle()
            assertTrue(awaitItem() is DataFetchResult.Success)

            // Act: Trigger refresh
            repository.fetchOverviewData()
            
            // Advance just enough to trigger Loading but not finish the API call
            testDispatcher.scheduler.runCurrent()
            assertTrue(awaitItem() is DataFetchResult.Loading)
            
            // Advance to finish the API call
            testDispatcher.scheduler.advanceUntilIdle()
            val result = awaitItem()
            assertTrue(result is DataFetchResult.Success)
            assertEquals(mockOverviewData, (result as DataFetchResult.Success).data)
        }
    }

    @Test
    fun `overviewData emits Error result on API failure`() = runTest {
        mockSessionManagerImpl.selectControllerById(0)
        
        // induce failure
        mockOverviewDataApiService.setOverviewData(null)

        repository.overviewData.test {
             assertEquals(DataFetchResult.Loading, awaitItem())
             
             repository.fetchOverviewData()
             testDispatcher.scheduler.advanceUntilIdle()
             
             // In this case, it goes from Loading (initial) to Error.
             // Loading from flatMapLatest is conflated.
             val result = awaitItem()
             assertTrue(result is DataFetchResult.Error)
        }
    }
}
