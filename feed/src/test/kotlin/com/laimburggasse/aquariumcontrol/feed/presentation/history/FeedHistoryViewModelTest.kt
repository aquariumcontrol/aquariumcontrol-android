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

package com.laimburggasse.aquariumcontrol.feed.presentation.history

import androidx.paging.PagingData
import app.cash.turbine.test
import com.laimburggasse.aquariumcontrol.feed.domain.model.FeedHistoryLogEntry
import com.laimburggasse.aquariumcontrol.feed.domain.repository.FeedHistoryRepository
import com.laimburggasse.aquariumcontrol.feed.testing.FeedMockData.mockFeedHistoryLogEntries
import io.mockk.every
import io.mockk.mockk
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
class FeedHistoryViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var repository: FeedHistoryRepository
    private lateinit var viewModel: FeedHistoryViewModel

    // Flow used to push PagingData updates to the ViewModel
    private val repositoryFlow = MutableStateFlow<PagingData<FeedHistoryLogEntry>>(PagingData.empty())

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = mockk(relaxed = true)

        // Stub the repository flow
        every { repository.feedHistoryEventFlow } returns repositoryFlow

        viewModel = FeedHistoryViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `viewModel correctly observes repository flow`() = runTest {
        viewModel.feedHistoryFlow.test {
            // Initial item from the stubbed flow
            awaitItem()

            // Act: Push new PagingData
            val newData = PagingData.from(mockFeedHistoryLogEntries)
            repositoryFlow.value = newData

            // Verify the ViewModel flow receives the new item
            // Note: PagingData doesn't implement equals, so we just verify receipt
            awaitItem()
        }
    }
}