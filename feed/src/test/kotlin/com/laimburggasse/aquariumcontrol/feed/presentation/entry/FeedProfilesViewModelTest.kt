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

package com.laimburggasse.aquariumcontrol.feed.presentation.entry

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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import com.google.firebase.crashlytics.FirebaseCrashlytics

@OptIn(ExperimentalCoroutinesApi::class)
class FeedProfilesViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var repository: FeedProfilesRepository
    private lateinit var viewModel: FeedProfilesViewModel

    // Controlled flow to simulate repository emissions
    private val repositoryFlow = MutableStateFlow<DataFetchResult<List<FeedProfile>>>(DataFetchResult.Loading)

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = mockk(relaxed = true)

        // Mock the repository flow
        every { repository.profilesFlow } returns repositoryFlow

        viewModel = FeedProfilesViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is Loading`() = runTest {
        viewModel.feedProfilesUiState.test {
            assertEquals(FeedProfilesUiState.Loading, awaitItem())
        }
    }

    @Test
    fun `repository success with data updates state to Success`() = runTest {
        val mockProfiles = listOf(mockk<FeedProfile>())

        viewModel.feedProfilesUiState.test {
            assertEquals(FeedProfilesUiState.Loading, awaitItem())

            // Act: Emit data
            repositoryFlow.value = DataFetchResult.Success(mockProfiles)

            val state = awaitItem()
            assertTrue(state is FeedProfilesUiState.Success)
            assertEquals(mockProfiles, (state as FeedProfilesUiState.Success).feedProfiles)
        }
    }

    @Test
    fun `repository success with empty list updates state to Empty`() = runTest {
        viewModel.feedProfilesUiState.test {
            assertEquals(FeedProfilesUiState.Loading, awaitItem())

            // Act: Emit empty list
            repositoryFlow.value = DataFetchResult.Success(emptyList())

            assertEquals(FeedProfilesUiState.Empty, awaitItem())
        }
    }

    @Test
    fun `repository error updates state to Error`() = runTest {
        val errorMsg = "API 404"

        viewModel.feedProfilesUiState.test {
            assertEquals(FeedProfilesUiState.Loading, awaitItem())

            // Act: Emit error
            repositoryFlow.value = DataFetchResult.Error(Exception(), errorMsg)

            val state = awaitItem()
            assertTrue(state is FeedProfilesUiState.Error)
            assertEquals(errorMsg, (state as FeedProfilesUiState.Error).errorMessage)
        }
    }

    @Test
    fun `Refresh event calls triggerRefresh on repository`() = runTest {
        // Act
        viewModel.onFeedProfilesUiEvent(FeedProfilesUiEvent.Refresh)

        // Advance scheduler to allow viewModelScope.launch to execute
        testDispatcher.scheduler.advanceUntilIdle()

        // Verify: repository.triggerRefresh() is called (once in init, once for the event)
        coVerify(exactly = 2) { repository.triggerRefresh() }
    }

    @Test
    fun `DeleteFeedProfile event calls deleteFeedProfile on repository`() = runTest {
        val profileToDelete = mockk<FeedProfile>()

        // Act
        viewModel.onFeedProfilesUiEvent(FeedProfilesUiEvent.DeleteFeedProfile(profileToDelete))
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        coVerify(exactly = 1) { repository.deleteFeedProfile(profileToDelete) }
    }
}