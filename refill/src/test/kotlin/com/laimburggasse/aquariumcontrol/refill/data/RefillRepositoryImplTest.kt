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

package com.laimburggasse.aquariumcontrol.refill.data

import app.cash.turbine.test
import com.google.firebase.Firebase
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.crashlytics.crashlytics
import com.laimburggasse.aquariumcontrol.common.data.remote.DataFetchResult
import com.laimburggasse.aquariumcontrol.common.di.IoDispatcher
import com.laimburggasse.aquariumcontrol.common.testing.ControllerMockData.mockControllerProfileList
import com.laimburggasse.aquariumcontrol.common.testing.MockSessionManagerImpl
import com.laimburggasse.aquariumcontrol.refill.testing.MockRefillStateApiServiceImpl
import com.laimburggasse.aquariumcontrol.refill.testing.RefillMockData.mockRefillStateActive
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
class RefillRepositoryImplTest {

    private lateinit var repository: RefillRepositoryImpl
    private val testDispatcher = StandardTestDispatcher()
    private val testScope = TestScope(testDispatcher)
    private val mockRefillApiService = MockRefillStateApiServiceImpl(
        mockRefillStateActive
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

        repository = RefillRepositoryImpl(
            sessionManager = mockSessionManagerImpl,
            apiService = mockRefillApiService,
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
    fun `refillStateFlow emits Loading then Success when controller is selected`() = runTest {
        repository.refillStateFlow.test {
            // Initial state from stateIn
            assertEquals(DataFetchResult.Loading, awaitItem())

            // Act: Select a controller
            mockSessionManagerImpl.selectControllerById(0)

            testDispatcher.scheduler.advanceUntilIdle()

            // The flow first emits Loading from inside flatMapLatest (conflated because it's already Loading)
            // Then it emits the Success result
            val result = awaitItem()
            assertTrue(result is DataFetchResult.Success)
            assertEquals(mockRefillStateActive, (result as DataFetchResult.Success).data)

            // Ensure no further emissions occur
            expectNoEvents()
        }
    }

    @Test
    fun `triggerRefresh causes a new fetch`() = runTest {
        mockSessionManagerImpl.selectControllerById(0)

        repository.refillStateFlow.test {
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
            assertEquals(mockRefillStateActive, (result as DataFetchResult.Success).data)
        }
    }

    @Test
    fun `refillStateFlow emits Error result on API failure`() = runTest {
        mockSessionManagerImpl.selectControllerById(0)
        
        // 1. Start with success data
        repository.refillStateFlow.test {
             assertTrue(awaitItem() is DataFetchResult.Loading)
             testDispatcher.scheduler.advanceUntilIdle()
             assertTrue(awaitItem() is DataFetchResult.Success)

             // 2. induce failure and trigger refresh
             mockRefillApiService.setForceError(force = true)
             repository.triggerRefresh()
             
             // 3. Observe the sequence
             testDispatcher.scheduler.runCurrent()
             assertTrue(awaitItem() is DataFetchResult.Loading)
             
             testDispatcher.scheduler.advanceUntilIdle()
             val result = awaitItem()
             assertTrue(result is DataFetchResult.Error)
        }
    }

    @Test
    fun `sendStartRequest calls API and emits success`() = runTest {
        mockSessionManagerImpl.selectControllerById(0)
        
        repository.sendStartRequest()
        testDispatcher.scheduler.advanceUntilIdle()
        
        // No easy way to verify start request other than no errors in errorEvents
        repository.errorEvents.test {
            expectNoEvents()
        }
    }
}
