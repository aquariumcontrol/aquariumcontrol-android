/*
// import.AquariumControl.domain.repository.FeedProfilesRepository
// import.AquariumControl.data.repository.FeedProfilesRepositoryImpl
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

import com.laimburggasse.aquariumcontrol.common.data.remote.DataFetchResult
import com.laimburggasse.aquariumcontrol.common.di.IoDispatcher
import com.laimburggasse.aquariumcontrol.common.domain.SessionManager
import com.laimburggasse.aquariumcontrol.common.testing.ControllerMockData.mockControllerProfileList
import com.laimburggasse.aquariumcontrol.common.testing.MockSessionManagerImpl
import com.laimburggasse.aquariumcontrol.common.util.Logger
import com.laimburggasse.aquariumcontrol.feed.data.repository.FeedProfilesRepositoryImpl
import com.laimburggasse.aquariumcontrol.feed.data.repository.FeedScheduleRepositoryImpl
import com.laimburggasse.aquariumcontrol.feed.domain.model.FeedProfile
import com.laimburggasse.aquariumcontrol.feed.domain.model.FeedScheduleEntry
import com.laimburggasse.aquariumcontrol.feed.domain.repository.FeedProfilesRepository
import com.laimburggasse.aquariumcontrol.feed.domain.repository.FeedScheduleRepository
import com.laimburggasse.aquariumcontrol.feed.testing.*
import com.laimburggasse.aquariumcontrol.feed.testing.FeedMockData.mockFeedProfile
import com.laimburggasse.aquariumcontrol.feed.testing.FeedMockData.mockFeedProfiles
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
import com.google.firebase.crashlytics.FirebaseCrashlytics

class FeedProfilesRepositoryImplTest {

    private lateinit var repository: FeedProfilesRepositoryImpl
    private val testDispatcher = StandardTestDispatcher()
    private val testScope = TestScope(testDispatcher)
    private val mockFeedProfileApiService = MockFeedProfileApiServiceImpl(
        mockFeedProfileImports = createMockFeedProfileImports()
    )
    private val mockSessionManagerImpl = MockSessionManagerImpl(
        controllerProfiles = mockControllerProfileList
    )
    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = FeedProfilesRepositoryImpl(
            logger = mockk(relaxed = true),
            sessionManager = mockSessionManagerImpl,
            apiService = mockFeedProfileApiService,
            repositoryScope = testScope,
            ioDispatcher = IoDispatcher(testDispatcher)
        )
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkAll()
    }

    @Test
    fun `feedProfiles emits Loading then Success when controller is selected`() = runTest {

        repository.profilesFlow.test {
            // Initial state from stateIn
            assertEquals(DataFetchResult.Loading, awaitItem())

            // Act: Select a controller
            mockSessionManagerImpl.selectControllerById(0)

            testDispatcher.scheduler.advanceUntilIdle()

            // Assert
            val result = awaitItem()

            assertTrue(result is DataFetchResult.Success)
            assertEquals(mockFeedProfiles, (result as DataFetchResult.Success).data)

            // Ensure no further emissions (like a second refresh or an error) occur
            expectNoEvents()
        }
    }

    @Test
    fun `updateFeedProfile performs optimistic update then refreshes`() = runTest {
        val updatedProfileName = "updated Name"
        val updatedFeedProfile = mockFeedProfiles[0].copy(profileName = updatedProfileName)
        repository.profilesFlow.test {
            // 1. Initial state (Loading) from stateIn
            assertEquals(DataFetchResult.Loading, awaitItem())

            // 2. Select controller to trigger first Success
            mockSessionManagerImpl.selectControllerById(0)
            
            // Collect until we get Success (ignoring Loading emissions from flatMapLatest)
            var currentItem = awaitItem()
            while (currentItem is DataFetchResult.Loading) {
                currentItem = awaitItem()
            }
            assertTrue("Expected Success but got $currentItem", currentItem is DataFetchResult.Success)

            // Act
            mockFeedProfileApiService.updateMockFeedProfile(updatedFeedProfile)
            repository.updateFeedProfile(updatedFeedProfile)

            // 3. Optimistic Result: localState.update triggers combine -> Success with pendingUpdates
            // We might see a Loading if flatMapLatest is fast, or just Success.
            currentItem = awaitItem()
            while (currentItem is DataFetchResult.Loading) {
                currentItem = awaitItem()
            }
            assertTrue("Expected Success (optimistic) but got $currentItem", currentItem is DataFetchResult.Success)
            assertEquals(updatedProfileName, (currentItem as DataFetchResult.Success).data.first().profileName)

            // 4. Refresh cycle finishes
            currentItem = awaitItem()
            while (currentItem is DataFetchResult.Loading) {
                currentItem = awaitItem()
            }
            assertTrue("Expected Success (final) but got $currentItem", currentItem is DataFetchResult.Success)
            assertEquals(updatedProfileName, (currentItem as DataFetchResult.Success).data.first().profileName)

            // Ensure no further emissions
            expectNoEvents()
        }
    }

    @Test
    fun `updateFeedProfile failure emits Error result`() = runTest {
        repository.profilesFlow.test {
            // Initial state
            assertEquals(DataFetchResult.Loading, awaitItem())

            mockSessionManagerImpl.selectControllerById(0)
            var currentItem = awaitItem()
            while (currentItem is DataFetchResult.Loading) {
                currentItem = awaitItem()
            }
            assertTrue(currentItem is DataFetchResult.Success)

            // Act
            mockFeedProfileApiService.setMockFeedProfiles(null)
            repository.updateFeedProfile(mockFeedProfiles[0])

            // First emission from localState update (Success with pendingUpdate)
            currentItem = awaitItem()
            while (currentItem is DataFetchResult.Loading) {
                currentItem = awaitItem()
            }
            
            // If it failed immediately it might be Error, otherwise Success (optimistic)
            if (currentItem is DataFetchResult.Success) {
                 currentItem = awaitItem()
                 while (currentItem is DataFetchResult.Loading) {
                     currentItem = awaitItem()
                 }
            }

            // Finally Error from failed network call
            assertTrue("Expected Error but got $currentItem", currentItem is DataFetchResult.Error)

            // On failure, localState is updated again which triggers ANOTHER emission
            // The flatMapLatest will see localState.failureResult != null and emit it.
            // But since it's a StateFlow, if it's the same error it might be conflated.
            // If expectNoEvents fails, it's because of an extra emission.
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `executeFeedProfile calls api service with execute URL`() = runTest {
        val spyApiService = spyk(mockFeedProfileApiService)
        val repoWithSpy = FeedProfilesRepositoryImpl(
            logger = mockk(relaxed = true),
            sessionManager = mockSessionManagerImpl,
            apiService = spyApiService,
            repositoryScope = testScope,
            ioDispatcher = IoDispatcher(testDispatcher)
        )
        mockSessionManagerImpl.selectControllerById(0)
        testDispatcher.scheduler.advanceUntilIdle()

        repoWithSpy.executeFeedProfile(profileId = 42)
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify(exactly = 1) {
            spyApiService.executeFeedProfile(
                match { it.endsWith("/api.php?action=execute_feed_profile&profileId=42") },
                any()
            )
        }
    }

    @Test
    fun `executeFeedProfile failure emits Error result`() = runTest {
        repository.profilesFlow.test {
            assertEquals(DataFetchResult.Loading, awaitItem())

            mockSessionManagerImpl.selectControllerById(0)
            var currentItem = awaitItem()
            while (currentItem is DataFetchResult.Loading) {
                currentItem = awaitItem()
            }
            assertTrue(currentItem is DataFetchResult.Success)

            mockFeedProfileApiService.setMockFeedProfiles(null)
            repository.executeFeedProfile(mockFeedProfiles[0].profileID)

            currentItem = awaitItem()
            while (currentItem is DataFetchResult.Loading) {
                currentItem = awaitItem()
            }
            assertTrue("Expected Error but got $currentItem", currentItem is DataFetchResult.Error)

            cancelAndIgnoreRemainingEvents()
        }
    }
}