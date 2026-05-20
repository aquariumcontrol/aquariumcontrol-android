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

package com.laimburggasse.aquariumcontrol.ventilation.data

import app.cash.turbine.test
import com.google.firebase.Firebase
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.crashlytics.crashlytics
import com.laimburggasse.aquariumcontrol.common.data.remote.DataFetchResult
import com.laimburggasse.aquariumcontrol.common.di.IoDispatcher
import com.laimburggasse.aquariumcontrol.common.testing.ControllerMockData.mockControllerProfileList
import com.laimburggasse.aquariumcontrol.common.testing.MockSessionManagerImpl
import com.laimburggasse.aquariumcontrol.ventilation.testing.VentilationMockData.mockVentilationSetVals
import com.laimburggasse.aquariumcontrol.ventilation.testing.MockVentilationSetValsApiServiceImpl
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

class VentilationSetValsRepositoryImplTest {
    private lateinit var repository: VentilationSetValsRepositoryImpl
    private val testDispatcher = StandardTestDispatcher()
    private val testScope = TestScope(testDispatcher)
    private val mockVentilationSetValsApiService = MockVentilationSetValsApiServiceImpl(
        mockVentilationSetVals,
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

        repository = VentilationSetValsRepositoryImpl(
            sessionManager = mockSessionManagerImpl,
            apiService = mockVentilationSetValsApiService,
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
    fun `ventilationSetValsFlow emits Loading then Success when controller is selected`() = runTest {

        repository.ventilationSetValsFlow.test {
            // Initial state from stateIn
            assertEquals(DataFetchResult.Loading, awaitItem())

            // Act: Select a controller
            mockSessionManagerImpl.selectControllerById(0)

            testDispatcher.scheduler.advanceUntilIdle()

            // Assert
            val result = awaitItem()

            assertTrue(result is DataFetchResult.Success)
            assertEquals(
                mockVentilationSetVals, (result as DataFetchResult.Success).data
            )

            // Ensure no further emissions occur
            expectNoEvents()
        }
    }

    @Test
    fun `updateLocalValues performs optimistic update`() = runTest {
        mockSessionManagerImpl.selectControllerById(0)
        val updatedSetVals = mockVentilationSetVals.copy(ventilationSwitchOffTemp = 28.0)

        repository.ventilationSetValsFlow.test {
            // Initial Loading and Success
            assertTrue(awaitItem() is DataFetchResult.Loading)
            testDispatcher.scheduler.advanceUntilIdle()
            assertTrue(awaitItem() is DataFetchResult.Success)

            // Act: Update local values
            repository.updateLocalValues(updatedSetVals)

            // Assert: Optimistic update should be emitted immediately
            val result = awaitItem()
            assertTrue(result is DataFetchResult.Success)
            assertEquals(updatedSetVals, (result as DataFetchResult.Success).data)

            expectNoEvents()
        }
    }

    @Test
    fun `updateValues commits values then refreshes`() = runTest {
        mockSessionManagerImpl.selectControllerById(0)
        val updatedSetVals = mockVentilationSetVals.copy(ventilationSwitchOffTemp = 22.0)

        repository.ventilationSetValsFlow.test {
            // Initial Loading and Success
            assertTrue(awaitItem() is DataFetchResult.Loading)
            testDispatcher.scheduler.advanceUntilIdle()
            assertTrue(awaitItem() is DataFetchResult.Success)

            // Act: Update local values and then commit
            repository.updateLocalValues(updatedSetVals)
            assertTrue(awaitItem() is DataFetchResult.Success) // Consume optimistic update

            mockVentilationSetValsApiService.updateMockVentilationSetVals(updatedSetVals)
            repository.updateValues()

            // Consuming intermediate states if they appear (StateFlow conflation makes this non-deterministic)
            testDispatcher.scheduler.advanceUntilIdle()

            // We want to see the final updated value
            var lastItem = awaitItem()
            while ((lastItem !is DataFetchResult.Success) || (lastItem.data != updatedSetVals)) {
                lastItem = awaitItem()
            }

            assertEquals(updatedSetVals, lastItem.data)
        }
    }

    @Test
    fun `updateValues failure emits Error result`() = runTest {
        mockSessionManagerImpl.selectControllerById(0)
        val updatedSetVals = mockVentilationSetVals.copy(ventilationSwitchOffTemp = 28.0)

        repository.ventilationSetValsFlow.test {
            // Initial Loading and Success
            assertTrue(awaitItem() is DataFetchResult.Loading)
            testDispatcher.scheduler.advanceUntilIdle()
            assertTrue(awaitItem() is DataFetchResult.Success)

            // Induce failure in next API call
            mockVentilationSetValsApiService.setForceError(force = true)

            repository.updateLocalValues(updatedSetVals)
            assertTrue(awaitItem() is DataFetchResult.Success) // Consume optimistic update

            repository.updateValues()

            testDispatcher.scheduler.advanceUntilIdle()

            // The public flow combines stable network data with local edits.
            // When updateValues fails, the repository sets failureResult.
            // networkDataFlow will emit Error.
            // ventilationSetValsFlow combines networkDataFlow and pendingUpdate.
            // In case of failure, updateValues sets pendingUpdate.value = null

            val result = awaitItem()
            // This could be the old Success or the Error
            if (result is DataFetchResult.Success) {
                val errorResult = awaitItem()
                assertTrue(errorResult is DataFetchResult.Error)
            } else {
                assertTrue(result is DataFetchResult.Error)
            }
        }
    }

    @Test
    fun `reset clears pending update`() = runTest {
        mockSessionManagerImpl.selectControllerById(0)
        val updatedSetVals = mockVentilationSetVals.copy(ventilationSwitchOffTemp = 28.0)

        repository.ventilationSetValsFlow.test {
            // Initial Loading and Success
            assertTrue(awaitItem() is DataFetchResult.Loading)
            testDispatcher.scheduler.advanceUntilIdle()
            assertTrue(awaitItem() is DataFetchResult.Success)

            // Act: Update local values
            repository.updateLocalValues(updatedSetVals)
            assertTrue(awaitItem() is DataFetchResult.Success) // Consume optimistic update

            // Reset
            repository.reset()

            // Assert: Should revert to original data
            val result = awaitItem()
            assertTrue(result is DataFetchResult.Success)
            assertEquals(
                mockVentilationSetVals, (result as DataFetchResult.Success).data
            )
        }
    }

    @Test
    fun `triggerRefresh re-fetches data`() = runTest {
        mockSessionManagerImpl.selectControllerById(0)

        repository.ventilationSetValsFlow.test {
            // Initial Loading and Success
            assertTrue(awaitItem() is DataFetchResult.Loading)
            testDispatcher.scheduler.advanceUntilIdle()
            assertTrue(awaitItem() is DataFetchResult.Success)

            // Act: Trigger refresh
            repository.triggerRefresh()

            // Assert: Should see Loading and then Success
            testDispatcher.scheduler.advanceUntilIdle()

            val refreshLoading = awaitItem()
            assertTrue(refreshLoading is DataFetchResult.Loading)

            val refreshSuccess = awaitItem()
            assertTrue(refreshSuccess is DataFetchResult.Success)
            assertEquals(
                mockVentilationSetVals,
                (refreshSuccess as DataFetchResult.Success).data
            )
        }
    }
}