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
import com.laimburggasse.aquariumcontrol.common.testing.ControllerMockData.mockControllerProfileList
import com.laimburggasse.aquariumcontrol.common.testing.MockSessionManagerImpl
import com.laimburggasse.aquariumcontrol.feed.testing.FeedMockData.mockFeedProfiles
import com.laimburggasse.aquariumcontrol.feed.testing.MockFeedProfileApiServiceImpl
import com.laimburggasse.aquariumcontrol.feed.testing.createMockFeedProfileImports
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

        // Mock Firebase Crashlytics statics
        mockkStatic(FirebaseCrashlytics::class)
        val mockCrashlytics = mockk<FirebaseCrashlytics>(relaxed = true)
        every { FirebaseCrashlytics.getInstance() } returns mockCrashlytics
        every { Firebase.crashlytics } returns mockCrashlytics

        repository = FeedProfilesRepositoryImpl(
            sessionManager = mockSessionManagerImpl,
            apiService = mockFeedProfileApiService,
            repositoryScope = testScope
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
        mockSessionManagerImpl.selectControllerById(0)
        val updatedProfileName = "updated Name"
        val updatedFeedProfile = mockFeedProfiles[0].copy(profileName = updatedProfileName)
        repository.profilesFlow.test {
            // Initial states
            assertTrue(awaitItem() is DataFetchResult.Loading)
            testDispatcher.scheduler.advanceUntilIdle()
            assertTrue(awaitItem() is DataFetchResult.Success)

            // Act
            mockFeedProfileApiService.updateMockFeedProfile(updatedFeedProfile)
            repository.updateFeedProfile(updatedFeedProfile)

            // Optimistic Result (triggered by localState update in updateFeedProfile)
            assertTrue(awaitItem() is DataFetchResult.Loading)
            testDispatcher.scheduler.advanceUntilIdle()
            val optimisticResult2 = awaitItem()
            assertTrue(optimisticResult2 is DataFetchResult.Success)

            val resultProfile = (optimisticResult2 as DataFetchResult.Success).data.first()
            assertEquals(updatedProfileName, resultProfile.profileName)

            // Ensure no further emissions
            expectNoEvents()
        }
    }

    @Test
    fun `updateFeedProfile failure emits Error result`() = runTest {
        mockSessionManagerImpl.selectControllerById(0)

        repository.profilesFlow.test {
            // Initial states
            assertTrue(awaitItem() is DataFetchResult.Loading)
            testDispatcher.scheduler.advanceUntilIdle()
            assertTrue(awaitItem() is DataFetchResult.Success)

            // induce error with next API call
            mockFeedProfileApiService.setMockFeedProfiles(null)

            repository.updateFeedProfile(mockFeedProfiles[0])

            // First it will try an optimistic update (Loading then Success/Error)
            assertTrue(awaitItem() is DataFetchResult.Loading)
            testDispatcher.scheduler.advanceUntilIdle()

            val result = awaitItem()
            assertTrue(result is DataFetchResult.Error)

            // Ensure no further emissions
            expectNoEvents()
        }
    }
}