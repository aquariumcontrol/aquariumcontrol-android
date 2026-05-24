/*
 * Copyright 2026 Uwe Martin
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.laimburggasse.aquariumcontrol.balling.data

import app.cash.turbine.test



import com.laimburggasse.aquariumcontrol.balling.testing.BallingMockData.mockBallingProfiles
import com.laimburggasse.aquariumcontrol.balling.testing.MockBallingProfileApiServiceImpl
import com.laimburggasse.aquariumcontrol.common.data.remote.DataFetchResult
import com.laimburggasse.aquariumcontrol.common.testing.ControllerMockData.mockControllerProfileList
import com.laimburggasse.aquariumcontrol.common.testing.MockSessionManagerImpl
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
class BallingProfilesRepositoryImplTest {

    private lateinit var repository: BallingProfilesRepositoryImpl
    private val testDispatcher = StandardTestDispatcher()
    private val testScope = TestScope(testDispatcher)
    private val mockBallingProfileApiService = MockBallingProfileApiServiceImpl(
        mockBallingProfiles
    )
    private val mockSessionManagerImpl = MockSessionManagerImpl(
        controllerProfiles = mockControllerProfileList
    )
    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        // Mock Firebase Crashlytics statics
        
        
        
        

        repository = BallingProfilesRepositoryImpl(
            sessionManager = mockSessionManagerImpl,
            apiService = mockBallingProfileApiService,
            repositoryScope = testScope
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkAll()
    }

    @Test
    fun `ballingProfiles emits Loading then Success when controller is selected`() = runTest {

        repository.ballingProfiles.test {
            // Initial state from stateIn
            assertEquals(DataFetchResult.Loading, awaitItem())

            // Act: Select a controller
            mockSessionManagerImpl.selectControllerById(0)

            testDispatcher.scheduler.advanceUntilIdle()

            // Assert
            val result = awaitItem()

            assertTrue(result is DataFetchResult.Success)
            assertEquals(mockBallingProfiles, (result as DataFetchResult.Success<*>).data)

            // Ensure no further emissions (like a second refresh or an error) occur
            expectNoEvents()
        }
    }

    @Test
    fun `updateBallingProfile performs optimistic update then refreshes`() = runTest {
        mockSessionManagerImpl.selectControllerById(0)
        val updatedVolume = 2.0
        val updatedBallingProfile = mockBallingProfiles[0].copy(volume = updatedVolume)
        repository.ballingProfiles.test {
            assertTrue(awaitItem() is DataFetchResult.Loading)
            assertTrue(awaitItem() is DataFetchResult.Success)

            // Act
            mockBallingProfileApiService.updateMockBallingProfile(updatedBallingProfile)
            repository.updateBallingProfile(updatedBallingProfile)

            val optimisticResult1 = awaitItem()
            assertTrue(optimisticResult1 is DataFetchResult.Loading)

            val optimisticResult2 = awaitItem()
            assertTrue(optimisticResult2 is DataFetchResult.Success)

            val updatedBallingProfile = (optimisticResult2 as DataFetchResult.Success).data.first()
            assertEquals(updatedVolume, updatedBallingProfile.volume, 0.0)

            // Ensure no further emissions (like a second refresh or an error) occur
            expectNoEvents()

        }
    }

    @Test
    fun `updateBallingProfile failure emits Error result`() = runTest {
        mockSessionManagerImpl.selectControllerById(0)

        repository.ballingProfiles.test {
            skipItems(2) // Skip Loading and initial Success

            // induce error with next API call
            mockBallingProfileApiService.updateMockBallingProfile(null)

            repository.updateBallingProfile(mockBallingProfiles[0])

            testDispatcher.scheduler.advanceUntilIdle()

            val result = awaitItem()
            assertTrue(result is DataFetchResult.Error)

            // Ensure no further emissions (like a second refresh or an error) occur
            expectNoEvents()
        }
    }
}