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

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.laimburggasse.aquariumcontrol.common.data.remote.DataFetchResult
import com.laimburggasse.aquariumcontrol.common.presentation.ViewNavigationRoutes
import com.laimburggasse.aquariumcontrol.feed.domain.model.FeedScheduleEntry
import com.laimburggasse.aquariumcontrol.feed.domain.repository.FeedProfilesRepository
import com.laimburggasse.aquariumcontrol.feed.domain.repository.FeedScheduleRepository
import com.laimburggasse.aquariumcontrol.feed.presentation.schedule.FeedScheduleEntryUiEvent
import com.laimburggasse.aquariumcontrol.feed.testing.FeedMockData.mockFeedProfile
import io.mockk.coEvery
import io.mockk.coVerify
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
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.util.Calendar

@OptIn(ExperimentalCoroutinesApi::class)
class FeedScheduleViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var scheduleRepository: FeedScheduleRepository
    private lateinit var profilesRepository: FeedProfilesRepository
    private lateinit var savedStateHandle: SavedStateHandle
    private lateinit var viewModel: FeedScheduleViewModel

    private val scheduleFlow = MutableStateFlow<DataFetchResult<List<FeedScheduleEntry>>>(DataFetchResult.Loading)

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        scheduleRepository = mockk(relaxed = true)
        profilesRepository = mockk(relaxed = true)

        // Mock the schedule flow from repository
        every { scheduleRepository.scheduleFlow } returns scheduleFlow

        // Default SavedStateHandle behavior
        savedStateHandle = SavedStateHandle(mapOf(
            ViewNavigationRoutes.FEED_ARG_IS_NEW_SCHEDULE_ENTRY to true,
            ViewNavigationRoutes.FEED_ARG_MILLIS_SINCE_EPOCH to 123456L,
            ViewNavigationRoutes.FEED_ARG_PROFILE_ID to 5
        ))

        coEvery { profilesRepository.findProfileById(5) } returns mockFeedProfile

        viewModel = FeedScheduleViewModel(
            savedStateHandle,
            scheduleRepository,
            profilesRepository
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `init block correctly sets state from SavedStateHandle`() = runTest {
        // Assert initial values from init
        assertEquals(true, viewModel.isNewScheduleEntry.value)
        assertEquals(123456L, viewModel.millisSinceEpoch.value)

        // Wait for loadDataForProfile(5) to finish
        testDispatcher.scheduler.advanceUntilIdle()
        assertEquals(mockFeedProfile, viewModel.feedProfile.value)
    }

    @Test
    fun `UI state transitions from Loading to Success when repository emits data`() = runTest {
        viewModel.feedScheduleSelectionUiState.test {
            // Initial state
            assertEquals(FeedScheduleSelectionUiState.Loading, awaitItem())

            // Act: Emit data
            val mockEntries = listOf(mockk<FeedScheduleEntry>())
            scheduleFlow.value = DataFetchResult.Success(mockEntries)

            val state = awaitItem()
            assertTrue(state is FeedScheduleSelectionUiState.Success)
            assertEquals(mockEntries, (state as FeedScheduleSelectionUiState.Success).feedScheduleEntries)
        }
    }

    @Test
    fun `UI state transitions to Empty when repository emits empty list`() = runTest {
        viewModel.feedScheduleSelectionUiState.test {
            awaitItem() // Skip loading

            // Act: Emit empty
            scheduleFlow.value = DataFetchResult.Success(emptyList())

            assertEquals(FeedScheduleSelectionUiState.Empty, awaitItem())
        }
    }

    @Test
    fun `DeleteFeedScheduleEntry event calls repository`() = runTest {
        val entry = mockk<FeedScheduleEntry>()

        // Act
        viewModel.onFeedSchedulesUiEvent(FeedScheduleUiEvent.DeleteFeedScheduleEntry(entry))
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        coVerify(exactly = 1) { scheduleRepository.deleteFeedScheduleEntry(entry) }
    }

    @Test
    fun `UpdateFeedScheduleEntry event with isNewEntry true calls addFeedScheduleEntry`() = runTest {
        val calendar = Calendar.getInstance()
        val event = FeedScheduleEntryUiEvent.UpdateFeedScheduleEntry(
            feedProfile = mockFeedProfile,
            feedScheduleEntry = mockk { every { isDaily } returns true },
            selectedCalendar = calendar,
            millisSinceEpoch = 0L,
            isNewEntry = true
        )

        // Act
        viewModel.onFeedScheduleEntryUiEvent(event)
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        coVerify(exactly = 1) {
            scheduleRepository.addFeedScheduleEntry(
                mockFeedProfile.profileID,
                mockFeedProfile.profileName,
                true,
                calendar
            )
        }
    }

    @Test
    fun `UpdateFeedScheduleEntry event with isNewEntry false calls updateFeedScheduleEntry`() = runTest {
        val calendar = Calendar.getInstance()
        val event = FeedScheduleEntryUiEvent.UpdateFeedScheduleEntry(
            feedProfile = mockFeedProfile,
            feedScheduleEntry = mockk { every { isDaily } returns false },
            selectedCalendar = calendar,
            millisSinceEpoch = 999L,
            isNewEntry = false
        )

        // Act
        viewModel.onFeedScheduleEntryUiEvent(event)
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        coVerify(exactly = 1) {
            scheduleRepository.updateFeedScheduleEntry(
                mockFeedProfile.profileID,
                mockFeedProfile.profileName,
                false,
                calendar,
                999L
            )
        }
    }

    @Test
    fun `GetFeedProfileIdByName returns correct ID from profiles repository`() {
        // Arrange
        val name = "Afternoon"
        every { profilesRepository.findProfileByName(name) } returns mockFeedProfile

        // Act
        val result = viewModel.onFeedSchedulesUiEvent(
            FeedScheduleUiEvent.GetFeedProfileIdByName(name)
        )

        // Assert
        assertEquals(mockFeedProfile.profileID, result)
    }
}