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

package com.laimburggasse.aquariumcontrol.feed.data

import app.cash.turbine.test
import com.google.firebase.Firebase
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.crashlytics.crashlytics
import com.laimburggasse.aquariumcontrol.common.data.remote.DataFetchResult
import com.laimburggasse.aquariumcontrol.common.di.IoDispatcher
import com.laimburggasse.aquariumcontrol.common.testing.ControllerMockData.mockControllerProfileList
import com.laimburggasse.aquariumcontrol.common.testing.MockSessionManagerImpl
import com.laimburggasse.aquariumcontrol.feed.testing.MockFeedScheduleEntryApiServiceImpl
import com.laimburggasse.aquariumcontrol.feed.testing.createMockFeedScheduleEntryImports
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
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
import java.util.Calendar

class FeedScheduleRepositoryImplTest {

    private lateinit var repository: FeedScheduleRepositoryImpl
    private val testDispatcher = StandardTestDispatcher()
    private val testScope = TestScope(testDispatcher)

    private val mockFeedScheduleEntryImports = createMockFeedScheduleEntryImports()
    private val mockFeedScheduleEntries = mockFeedScheduleEntryImports.map { it.transformToFeedScheduleEntry() }
    private val mockFeedScheduleApiService = MockFeedScheduleEntryApiServiceImpl(
        mockFeedScheduleEntryImports
    )
    private val mockSessionManagerImpl = MockSessionManagerImpl(
        controllerProfiles = mockControllerProfileList
    )

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        // Mock Firebase Crashlytics statics
        mockkStatic(FirebaseCrashlytics::class)
        val mockCrashlytics = mockk<FirebaseCrashlytics>(relaxed = true)
        every { FirebaseCrashlytics.getInstance() } returns mockCrashlytics
        every { Firebase.crashlytics } returns mockCrashlytics

        repository = FeedScheduleRepositoryImpl(
            sessionManager = mockSessionManagerImpl,
            apiService = mockFeedScheduleApiService,
            repositoryScope = testScope,
            context = IoDispatcher(testDispatcher)
        )
    }

    @OptIn(ExperimentalCoroutinesApi::class)
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

            // Assert
            val result = awaitItem()

            assertTrue(result is DataFetchResult.Success)
            assertEquals(mockFeedScheduleEntries, (result as DataFetchResult.Success).data)

            // Ensure no further emissions (like a second refresh or an error) occur
            expectNoEvents()
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `updateFeedSchedule performs optimistic update then refreshes`() = runTest {
        mockSessionManagerImpl.selectControllerById(0)
        val updatedTimestamp = mockFeedScheduleEntries[0].timestamp + 1000L
        val updatedMockFeedSchedule = mockFeedScheduleEntries[0].copy(timestamp = updatedTimestamp)
        repository.scheduleFlow.test {
            assertEquals(DataFetchResult.Loading, awaitItem())
            testDispatcher.scheduler.advanceUntilIdle()
            assertTrue(awaitItem() is DataFetchResult.Success)

            // Act
            // In FeedScheduleRepositoryImplTest.kt
            val oldTimestamp = mockFeedScheduleEntries[0].timestamp
            mockFeedScheduleApiService.updateMockFeedScheduleEntry(
                oldTimestamp, updatedMockFeedSchedule
            )
            repository.updateFeedScheduleEntry(
                feedProfileId = updatedMockFeedSchedule.profileId,
                feedProfileName = updatedMockFeedSchedule.profileName,
                repeatDaily = updatedMockFeedSchedule.isDaily,
                selectedCalendar = Calendar.getInstance().apply {
                    timeInMillis = updatedMockFeedSchedule.timestamp
                },
                millisSinceEpoch = mockFeedScheduleEntries[0].timestamp
            )

            testDispatcher.scheduler.advanceUntilIdle()

            // Optimistic Success
            val optimisticResult = awaitItem()
            assertTrue(optimisticResult is DataFetchResult.Success)
            val optimisticEntry = (optimisticResult as DataFetchResult.Success).data.first { it.profileId == updatedMockFeedSchedule.profileId }
            assertTrue(optimisticEntry.profileName.contains("(Saving...)"))

            // Advance time for the 3000ms delay in repository
            testDispatcher.scheduler.advanceTimeBy(3000L)
            testDispatcher.scheduler.runCurrent()

            // Loading from refresh
            assertTrue(awaitItem() is DataFetchResult.Loading)

            // Final Success
            val finalResult = awaitItem()
            assertTrue(finalResult is DataFetchResult.Success)
            assertEquals(updatedMockFeedSchedule.timestamp, (finalResult as DataFetchResult.Success).data.first { it.profileId == updatedMockFeedSchedule.profileId }.timestamp)

            // Ensure no further emissions (like a second refresh or an error) occur
            expectNoEvents()
        }
    }

    @Test
    fun `updateFeedSchedule failure emits Error result`() = runTest {
        mockSessionManagerImpl.selectControllerById(0)
        val updatedMockFeedSchedule = mockFeedScheduleEntries[0].copy()

        repository.scheduleFlow.test {
            assertEquals(DataFetchResult.Loading, awaitItem())
            testDispatcher.scheduler.advanceUntilIdle()
            assertTrue(awaitItem() is DataFetchResult.Success)

            // induce error with next API call
            mockFeedScheduleApiService.setMockFeedScheduleEntryImports(null)

            repository.updateFeedScheduleEntry(
                feedProfileId = updatedMockFeedSchedule.profileId,
                feedProfileName = updatedMockFeedSchedule.profileName,
                repeatDaily = updatedMockFeedSchedule.isDaily,
                selectedCalendar = Calendar.getInstance().apply {
                    timeInMillis = updatedMockFeedSchedule.timestamp
                },
                millisSinceEpoch = mockFeedScheduleEntries[0].timestamp
            )

            testDispatcher.scheduler.advanceUntilIdle()

            // Optimistic Success
            assertTrue(awaitItem() is DataFetchResult.Success)

            // Reverted
            assertTrue(awaitItem() is DataFetchResult.Success)

            // Error Result
            val result = awaitItem()
            assertTrue(result is DataFetchResult.Error)

            // Ensure no further emissions (like a second refresh or an error) occur
            expectNoEvents()
        }
    }
}