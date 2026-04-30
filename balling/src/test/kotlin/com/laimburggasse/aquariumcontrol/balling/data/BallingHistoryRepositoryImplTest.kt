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

import androidx.paging.Pager
import androidx.paging.PagingData
import app.cash.turbine.test
import com.google.firebase.Firebase
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.crashlytics.crashlytics
import com.laimburggasse.aquariumcontrol.balling.data.remote.BallingHistoryRemoteMediator
import com.laimburggasse.aquariumcontrol.balling.domain.model.BallingHistoryLogEntry
import com.laimburggasse.aquariumcontrol.common.testing.ControllerMockData.mockControllerProfileList
import com.laimburggasse.aquariumcontrol.common.testing.MockSessionManagerImpl
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class BallingHistoryRepositoryImplTest {

    private lateinit var repository: BallingHistoryRepositoryImpl
    private val testDispatcher = StandardTestDispatcher()
    
    private val mockSessionManagerImpl = MockSessionManagerImpl(
        controllerProfiles = mockControllerProfileList
    )
    private val mockPager: Pager<Int, BallingHistoryLogEntry> = mockk(relaxed = true)
    private val mockRemoteMediator: BallingHistoryRemoteMediator = mockk(relaxed = true)
    
    private val pagerFlow = MutableStateFlow<PagingData<BallingHistoryLogEntry>>(PagingData.empty())

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        // Mock Firebase Crashlytics statics
        mockkStatic(FirebaseCrashlytics::class)
        val mockCrashlytics = mockk<FirebaseCrashlytics>(relaxed = true)
        every { FirebaseCrashlytics.getInstance() } returns mockCrashlytics
        every { Firebase.crashlytics } returns mockCrashlytics

        every { mockPager.flow } returns pagerFlow

        repository = BallingHistoryRepositoryImpl(
            sessionManager = mockSessionManagerImpl,
            pager = mockPager,
            remoteMediator = mockRemoteMediator
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkAll()
    }

    @Test
    fun `ballingHistoryDosingEventFlow updates mediator config and returns pager flow when controller is selected`() = runTest {

        repository.ballingHistoryDosingEventFlow.test {
            // Initial state: nothing emitted yet because no controller is selected (filtered out)
            
            // Act: Select a controller
            mockSessionManagerImpl.selectControllerById(mockControllerProfileList[0].id)
            testDispatcher.scheduler.runCurrent()

            // Assert: Mediator is updated
            verify { mockRemoteMediator.updateConfig(any(), any()) }
            
            // Assert: We received the initial item from pagerFlow
            awaitItem()
        }
    }
}