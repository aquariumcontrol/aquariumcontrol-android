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

package com.laimburggasse.aquariumcontrol.schedule.data

import app.cash.turbine.test
import com.google.firebase.Firebase
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.crashlytics.crashlytics
import com.laimburggasse.aquariumcontrol.common.data.remote.DataFetchResult
import com.laimburggasse.aquariumcontrol.common.di.IoDispatcher
import com.laimburggasse.aquariumcontrol.common.testing.ControllerMockData.mockControllerProfileList
import com.laimburggasse.aquariumcontrol.common.testing.MockSessionManagerImpl
import com.laimburggasse.aquariumcontrol.schedule.data.remote.dto.ScheduleStringEntry
import com.laimburggasse.aquariumcontrol.schedule.domain.model.ScheduleType
import com.laimburggasse.aquariumcontrol.schedule.testing.MockScheduleApiServiceImpl
import com.laimburggasse.aquariumcontrol.schedule.testing.ScheduleMockData.mockScheduleEntries
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
class ScheduleRepositoryImplTest {

    private lateinit var repository: ScheduleRepositoryImpl
    private val testDispatcher = StandardTestDispatcher()
    private val testScope = TestScope(testDispatcher)
    
    private val mockScheduleStringEntries = listOf(
        ScheduleStringEntry(ScheduleType.BALLING.key, "02:00:00", "22:00:00", 0),
        ScheduleStringEntry(ScheduleType.REFILL.key, "03:00:00", "21:00:00", 1),
        ScheduleStringEntry(ScheduleType.VENTILATION.key, "04:00:00", "20:00:00", 0),
        ScheduleStringEntry(ScheduleType.HEATING.key, "05:00:00", "19:00:00", 1)
    )
    
    private val mockScheduleApiService = MockScheduleApiServiceImpl(
        mockScheduleStringEntries
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

        repository = ScheduleRepositoryImpl(
            sessionManager = mockSessionManagerImpl,
            apiService = mockScheduleApiService,
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
    fun `scheduleFlow emits Loading then Success when controller is selected`() = runTest {
        repository.scheduleFlow.test {
            // Initial state from stateIn
            assertEquals(DataFetchResult.Loading, awaitItem())

            // Act: Select a controller
            mockSessionManagerImpl.selectControllerById(0)

            testDispatcher.scheduler.advanceUntilIdle()

            val result = awaitItem()
            assertTrue(result is DataFetchResult.Success)
            assertEquals(mockScheduleEntries, (result as DataFetchResult.Success).data)

            // Ensure no further emissions occur
            expectNoEvents()
        }
    }

    @Test
    fun `updateLocalSchedule performs optimistic update`() = runTest {
        mockSessionManagerImpl.selectControllerById(0)
        val updatedEntries = mockScheduleEntries.copy(ballingStartTime = 8.0F)
        
        repository.scheduleFlow.test {
            // Initial Loading and Success
            assertTrue(awaitItem() is DataFetchResult.Loading)
            testDispatcher.scheduler.advanceUntilIdle()
            assertTrue(awaitItem() is DataFetchResult.Success)

            // Act: Update local values
            repository.updateLocalSchedule(updatedEntries)

            // Assert: Optimistic update should be emitted immediately
            val result = awaitItem()
            assertTrue(result is DataFetchResult.Success)
            assertEquals(updatedEntries, (result as DataFetchResult.Success).data)

            expectNoEvents()
        }
    }

    @Test
    fun `reset clears pending update`() = runTest {
        mockSessionManagerImpl.selectControllerById(0)
        val updatedEntries = mockScheduleEntries.copy(ballingStartTime = 8.0F)

        repository.scheduleFlow.test {
            // Initial Loading and Success
            assertTrue(awaitItem() is DataFetchResult.Loading)
            testDispatcher.scheduler.advanceUntilIdle()
            assertTrue(awaitItem() is DataFetchResult.Success)

            // Act: Update local values
            repository.updateLocalSchedule(updatedEntries)
            assertTrue(awaitItem() is DataFetchResult.Success) // Consume optimistic update

            // Reset
            repository.reset()

            // Assert: Should revert to original data
            val result = awaitItem()
            assertTrue(result is DataFetchResult.Success)
            assertEquals(mockScheduleEntries, (result as DataFetchResult.Success).data)
        }
    }

    @Test
    fun `triggerRefresh causes a new fetch`() = runTest {
        mockSessionManagerImpl.selectControllerById(0)

        repository.scheduleFlow.test {
            // Initial Loading and Success
            assertTrue(awaitItem() is DataFetchResult.Loading)
            testDispatcher.scheduler.advanceUntilIdle()
            assertTrue(awaitItem() is DataFetchResult.Success)

            // Act: Trigger refresh
            repository.triggerRefresh()
            
            // Assert: Should see Loading and then Success again
            testDispatcher.scheduler.runCurrent()
            assertTrue(awaitItem() is DataFetchResult.Loading)
            
            testDispatcher.scheduler.advanceUntilIdle()
            val result = awaitItem()
            assertTrue(result is DataFetchResult.Success)
            assertEquals(mockScheduleEntries, (result as DataFetchResult.Success).data)
        }
    }

    @Test
    fun `scheduleFlow emits Error result on API failure`() = runTest {
        mockSessionManagerImpl.selectControllerById(0)
        
        // induce failure
        mockScheduleApiService.setMockScheduleStringEntries(null)

        repository.scheduleFlow.test {
             assertTrue(awaitItem() is DataFetchResult.Loading)
             testDispatcher.scheduler.advanceUntilIdle()
             
             // Loading from flatMapLatest is conflated if it happens too fast.
             // But we have a delay(10) in mock service.

             val result = awaitItem()
             assertTrue(result is DataFetchResult.Error)
        }
    }
}
