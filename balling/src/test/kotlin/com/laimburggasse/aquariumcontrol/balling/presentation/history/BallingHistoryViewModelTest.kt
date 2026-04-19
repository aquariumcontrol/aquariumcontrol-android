package com.laimburggasse.aquariumcontrol.balling.presentation.history

import app.cash.turbine.test
import com.laimburggasse.aquariumcontrol.balling.domain.model.BallingHistoryLogEntry
import com.laimburggasse.aquariumcontrol.balling.domain.repository.BallingHistoryRepository
import com.laimburggasse.aquariumcontrol.balling.testing.BallingMockData.mockBallingHistoryLogEntries
import com.laimburggasse.aquariumcontrol.common.data.remote.DataFetchResult
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

@OptIn(ExperimentalCoroutinesApi::class)
class BallingHistoryViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var repository: BallingHistoryRepository<BallingHistoryLogEntry>
    private lateinit var viewModel: BallingHistoryViewModel

    // Flow used to push updates to the ViewModel
    private val repositoryFlow = MutableStateFlow<DataFetchResult<List<BallingHistoryLogEntry>>>(DataFetchResult.Loading)

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = mockk(relaxed = true)

        // Stub the repository flow
        every { repository.ballingHistoryDosingEventFlow } returns repositoryFlow

        viewModel = BallingHistoryViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is Loading`() = runTest {
        viewModel.ballingHistoryUiState.test {
            assertEquals(BallingHistoryUiState.Loading, awaitItem())
        }
    }

    @Test
    fun `successful fetch with data updates state to Success`() = runTest {
        viewModel.ballingHistoryUiState.test {
            assertEquals(BallingHistoryUiState.Loading, awaitItem())

            // Act: Push success data
            repositoryFlow.value = DataFetchResult.Success(mockBallingHistoryLogEntries)

            val item = awaitItem()
            assertTrue(item is BallingHistoryUiState.Success)
            assertEquals(2, (item as BallingHistoryUiState.Success).ballingHistoryLogEntries.size)
        }
    }

    @Test
    fun `successful fetch with empty list updates state to Empty`() = runTest {
        viewModel.ballingHistoryUiState.test {
            awaitItem() // Skip Loading

            // Act: Push empty list
            repositoryFlow.value = DataFetchResult.Success(emptyList())

            assertEquals(BallingHistoryUiState.Empty, awaitItem())
        }
    }

    @Test
    fun `failed fetch updates state to Error`() = runTest {
        val errorMsg = "Network Timeout"
        viewModel.ballingHistoryUiState.test {
            awaitItem() // Skip Loading

            // Act: Push error
            repositoryFlow.value = DataFetchResult.Error(
                errorMessage = errorMsg,
                exception = Exception()
            )

            val item = awaitItem()
            assertTrue(item is BallingHistoryUiState.Error)
            assertEquals(errorMsg, (item as BallingHistoryUiState.Error).errorMessage)
        }
    }

    @Test
    fun `Refresh event triggers triggerRefresh in repository`() = runTest {
        // Act
        viewModel.onEvent(BallingHistoryUiEvent.Refresh)

        // Wait for the viewModelScope.launch to execute
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        coVerify(exactly = 1) { repository.triggerRefresh() }
    }

    @Test
    fun `SetRange event triggers setHistoryTimeRange in repository`() = runTest {
        // Act: Request full week
        viewModel.onEvent(BallingHistoryUiEvent.SetRange(showFullWeek = true))

        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        coVerify(exactly = 1) { repository.setHistoryTimeRange(fetchFullWeek = true) }
    }
}