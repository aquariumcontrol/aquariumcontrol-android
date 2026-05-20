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
import com.google.firebase.Firebase
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.crashlytics.crashlytics
import com.laimburggasse.aquariumcontrol.common.data.remote.DataFetchResult
import com.laimburggasse.aquariumcontrol.common.di.IoDispatcher
import com.laimburggasse.aquariumcontrol.common.testing.ControllerMockData.mockControllerProfileList
import com.laimburggasse.aquariumcontrol.common.testing.MockSessionManagerImpl
import com.laimburggasse.aquariumcontrol.heating.testing.HeatingMockData.mockHeatingStatsData
import com.laimburggasse.aquariumcontrol.heating.testing.MockHeatingStatsApiServiceImpl
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
class HeatingStatsRepositoryImplTest {

    private lateinit var repository: HeatingStatsRepositoryImpl
    private val testDispatcher = StandardTestDispatcher()
    private val testScope = TestScope(testDispatcher)
    private val mockHeatingStatsApiService = MockHeatingStatsApiServiceImpl(
        mockHeatingStatsData
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

        repository = HeatingStatsRepositoryImpl(
            sessionManager = mockSessionManagerImpl,
            apiService = mockHeatingStatsApiService,
            repositoryScope = testScope,
            ioDispatcher = IoDispatcher(testDispatcher)
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkAll()
    }

    @Test
    fun `heatingStatsFlow emits Loading then Success when controller is selected`() = runTest {
        repository.heatingStatsFlow.test {
            // Initial state from stateIn
            assertEquals(DataFetchResult.Loading, awaitItem())

            // Act: Select a controller
            mockSessionManagerImpl.selectControllerById(0)

            testDispatcher.scheduler.advanceUntilIdle()

            // Assert
            val result = awaitItem()
            assertTrue(result is DataFetchResult.Success)
            assertEquals(mockHeatingStatsData, (result as DataFetchResult.Success).data)

            // Ensure no further emissions occur
            expectNoEvents()
        }
    }

    @Test
    fun `triggerRefresh causes a new fetch`() = runTest {
        mockSessionManagerImpl.selectControllerById(0)

        repository.heatingStatsFlow.test {
            // Initial states
            assertTrue(awaitItem() is DataFetchResult.Loading)
            testDispatcher.scheduler.advanceUntilIdle()
            assertTrue(awaitItem() is DataFetchResult.Success)

            // Act: Trigger refresh
            repository.triggerRefresh()
            testDispatcher.scheduler.advanceUntilIdle()

            // Assert: Should see Loading and then Success again
            assertTrue(awaitItem() is DataFetchResult.Loading)
            val result = awaitItem()
            assertTrue(result is DataFetchResult.Success)
            assertEquals(mockHeatingStatsData, (result as DataFetchResult.Success).data)
        }
    }

    @Test
    fun `heatingStatsFlow emits Error result on API failure`() = runTest {
        mockSessionManagerImpl.selectControllerById(0)
        
        // induce failure
        mockHeatingStatsApiService.setMockHeatingStats(null)

        repository.heatingStatsFlow.test {
             assertTrue(awaitItem() is DataFetchResult.Loading)
             testDispatcher.scheduler.advanceUntilIdle()
             
             val result = awaitItem()
             assertTrue(result is DataFetchResult.Error)
        }
    }
}
