package com.laimburggasse.aquariumcontrol.balling.presentation.entry

import app.cash.turbine.test
import com.laimburggasse.aquariumcontrol.balling.domain.model.BallingProfile
import com.laimburggasse.aquariumcontrol.balling.domain.repository.BallingProfilesRepository
import com.laimburggasse.aquariumcontrol.balling.testing.BallingMockData.mockBallingProfiles
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
class BallingProfilesViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var repository: BallingProfilesRepository
    private lateinit var viewModel: BallingProfilesViewModel

    // Controlled flow to simulate repository updates
    private val repositoryFlow = MutableStateFlow<DataFetchResult<List<BallingProfile>>>(DataFetchResult.Loading)

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = mockk(relaxed = true)

        // Stub the repository to return our controlled flow
        every { repository.ballingProfiles } returns repositoryFlow

        viewModel = BallingProfilesViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is Loading`() = runTest {
        viewModel.ballingProfilesUiState.test {
            // StateFlow immediately emits its initial value
            assertEquals(BallingProfilesUiState.Loading, awaitItem())
        }
    }

    @Test
    fun `successful fetch with data updates state to Success`() = runTest {
        viewModel.ballingProfilesUiState.test {
            assertEquals(BallingProfilesUiState.Loading, awaitItem())

            // Act: Simulate repository returning data
            repositoryFlow.value = DataFetchResult.Success(mockBallingProfiles)

            val item = awaitItem()
            assertTrue(item is BallingProfilesUiState.Success)
            assertEquals(2, (item as BallingProfilesUiState.Success).ballingProfiles.size)
        }
    }

    @Test
    fun `successful fetch with empty data updates state to Empty`() = runTest {
        viewModel.ballingProfilesUiState.test {
            awaitItem() // Skip Loading

            // Act: Simulate repository returning an empty list
            repositoryFlow.value = DataFetchResult.Success(emptyList())

            assertEquals(BallingProfilesUiState.Empty, awaitItem())
        }
    }

    @Test
    fun `failed fetch updates state to Error`() = runTest {
        val errorMsg = "Server Unreachable"
        viewModel.ballingProfilesUiState.test {
            awaitItem() // Skip Loading

            // Act: Simulate repository returning an error
            repositoryFlow.value = DataFetchResult.Error(
                errorMessage = errorMsg,
                exception = Exception()
            )

            val item = awaitItem()
            assertTrue(item is BallingProfilesUiState.Error)
            assertEquals(errorMsg, (item as BallingProfilesUiState.Error).errorMessage)
        }
    }

    @Test
    fun `onRefresh event calls triggerRefresh on repository`() = runTest {
        // Act
        viewModel.onRefresh()

        // Ensure the viewModelScope.launch has finished executing
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        coVerify(exactly = 1) { repository.triggerRefresh() }
    }
}