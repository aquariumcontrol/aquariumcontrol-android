package com.laimburggasse.aquariumcontrol.schedule.presentation

import app.cash.turbine.test
import com.laimburggasse.aquariumcontrol.common.data.remote.DataFetchResult
import com.laimburggasse.aquariumcontrol.schedule.domain.model.ScheduleEntries
import com.laimburggasse.aquariumcontrol.schedule.domain.repository.ScheduleRepository
import com.laimburggasse.aquariumcontrol.schedule.testing.ScheduleMockData.mockScheduleEntries
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
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

@OptIn(ExperimentalCoroutinesApi::class)
class ScheduleViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var repository: ScheduleRepository
    private lateinit var viewModel: ScheduleViewModel

    // Controlled flow to simulate repository updates
    private val repositoryFlow = MutableStateFlow<DataFetchResult<ScheduleEntries>>(DataFetchResult.Loading)

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = mockk(relaxed = true)

        // Stub the repository to return our controlled flow
        every { repository.scheduleFlow } returns repositoryFlow

        viewModel = ScheduleViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is Loading`() = runTest {
        viewModel.scheduleUiState.test {
            assertEquals(ScheduleUiState.Loading, awaitItem())
        }
    }

    @Test
    fun `successful fetch updates state to Success`() = runTest {
        viewModel.scheduleUiState.test {
            assertEquals(ScheduleUiState.Loading, awaitItem())

            // Act: Push success data into the repository flow
            repositoryFlow.value = DataFetchResult.Success(mockScheduleEntries)

            val state = awaitItem()
            assertTrue(state is ScheduleUiState.Success)
            assertEquals(
                mockScheduleEntries.ballingStartTime,
                (state as ScheduleUiState.Success).scheduleEntries.ballingStartTime
            )
        }
    }

    @Test
    fun `failed fetch updates state to Error`() = runTest {
        val errorMsg = "Network Error"
        viewModel.scheduleUiState.test {
            awaitItem() // Skip initial loading

            // Act: Push error into the repository flow
            repositoryFlow.value = DataFetchResult.Error(exception = Exception(), errorMessage = errorMsg)

            val state = awaitItem()
            assertTrue(state is ScheduleUiState.Error)
            assertEquals(errorMsg, (state as ScheduleUiState.Error).errorMessage)
        }
    }

    @Test
    fun `ChangeTime event triggers repository update with copied values`() = runTest {
        // Arrange: Start with success state
        repositoryFlow.value = DataFetchResult.Success(mockScheduleEntries)
        testDispatcher.scheduler.advanceUntilIdle()

        // Act: User changes Balling Start Time to 09:30 (9.5f)
        viewModel.onScheduleEvent(
            ScheduleUiEvent.ChangeTime(
                scheduleType = ScheduleType.BALLING,
                timeType = TimeType.START,
                newTime = 9.5f
            )
        )

        // Assert: Verify repository received an object with the updated value
        verify(exactly = 1) {
            repository.updateLocalSchedule(match { it.ballingStartTime == 9.5f })
        }
    }

    @Test
    fun `Update event triggers remote repository update`() = runTest {
        // Act
        viewModel.onScheduleEvent(ScheduleUiEvent.Update)
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        coVerify(exactly = 1) { repository.updateSchedule() }
    }

    @Test
    fun `Reset event triggers repository reset`() = runTest {
        // Act
        viewModel.onScheduleEvent(ScheduleUiEvent.Reset)
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        coVerify(exactly = 1) { repository.reset() }
    }

    @Test
    fun `Refresh event triggers repository refresh`() = runTest {
        // Act
        viewModel.onScheduleEvent(ScheduleUiEvent.Refresh)
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        coVerify(exactly = 1) { repository.triggerRefresh() }
    }
}