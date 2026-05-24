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
import com.laimburggasse.aquariumcontrol.common.data.remote.DataFetchResult
import com.laimburggasse.aquariumcontrol.common.di.IoDispatcher
import com.laimburggasse.aquariumcontrol.common.domain.SessionManager
import com.laimburggasse.aquariumcontrol.common.testing.ControllerMockData.mockControllerProfileList
import com.laimburggasse.aquariumcontrol.common.testing.MockSessionManagerImpl
import com.laimburggasse.aquariumcontrol.common.util.Logger
import com.laimburggasse.aquariumcontrol.heating.data.repository.HeatingStatsRepositoryImpl
import com.laimburggasse.aquariumcontrol.heating.domain.model.HeatingStatsEntry
import com.laimburggasse.aquariumcontrol.heating.domain.repository.HeatingStatsRepository
import com.laimburggasse.aquariumcontrol.heating.testing.HeatingMockData
import com.laimburggasse.aquariumcontrol.heating.testing.MockHeatingStatsApiServiceImpl
import io.mockk.*
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
        HeatingMockData.mockHeatingStatsData
    )
    private val mockSessionManagerImpl = MockSessionManagerImpl(
        controllerProfiles = mockControllerProfileList
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = HeatingStatsRepositoryImpl(
            logger = mockk(relaxed = true),
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
    fun `heatingStats emits Loading then Success when controller is selected`() = runTest {

        repository.heatingStatsFlow.test {
            // Initial state from stateIn
            assertEquals(DataFetchResult.Loading, awaitItem())

            // Act: Select a controller
            mockSessionManagerImpl.selectControllerById(0)

            testDispatcher.scheduler.advanceUntilIdle()

            // Assert
            val result = awaitItem()

            assertTrue(result is DataFetchResult.Success)
            assertEquals(HeatingMockData.mockHeatingStatsData, (result as DataFetchResult.Success).data)

            // Ensure no further emissions (like a second refresh or an error) occur
            expectNoEvents()
        }
    }

    @Test
    fun `triggerRefresh from success state emits refreshTrigger`() = runTest {
        mockSessionManagerImpl.selectControllerById(0)

        repository.heatingStatsFlow.test {
            assertTrue(awaitItem() is DataFetchResult.Loading)
            assertTrue(awaitItem() is DataFetchResult.Success)

            // Act: Refresh
            repository.triggerRefresh()

            assertTrue(awaitItem() is DataFetchResult.Loading)
            assertTrue(awaitItem() is DataFetchResult.Success)

            // Ensure no further emissions (like a second refresh or an error) occur
            expectNoEvents()
        }
    }

    @Test
    fun `fetch failure emits Error result`() = runTest {
        mockSessionManagerImpl.selectControllerById(0)

        repository.heatingStatsFlow.test {
            assertTrue(awaitItem() is DataFetchResult.Loading)
            assertTrue(awaitItem() is DataFetchResult.Success)

            // Act: induce error with next API call
            mockHeatingStatsApiService.setMockHeatingStats(null)

            repository.triggerRefresh()

            testDispatcher.scheduler.advanceUntilIdle()

            assertTrue(awaitItem() is DataFetchResult.Loading)
            val result = awaitItem()
            assertTrue(result is DataFetchResult.Error)

            // Ensure no further emissions (like a second refresh or an error) occur
            expectNoEvents()
        }
    }
}
