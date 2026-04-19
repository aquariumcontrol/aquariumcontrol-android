package com.laimburggasse.aquariumcontrol.balling.presentation.edit

import androidx.lifecycle.SavedStateHandle
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
class BallingProfileEditViewModelTest {
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var repository: BallingProfilesRepository
    private lateinit var savedStateHandle: SavedStateHandle
    private lateinit var viewModel: BallingProfileEditViewModel

    private val repositoryFlow = MutableStateFlow<DataFetchResult<List<BallingProfile>>>(DataFetchResult.Loading)

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = mockk(relaxed = true)
        savedStateHandle = mockk()

        // Default behavior: return the repository flow
        every { repository.ballingProfiles } returns repositoryFlow
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is Loading`() = runTest {
        // savedStatHandle is used to transfer information about which Balling profile to edit.
        every { savedStateHandle.get<Int>("ballingProfileId") } returns 1

        viewModel = BallingProfileEditViewModel(savedStateHandle, repository)

        viewModel.ballingProfileEditUiState.test {
            assertEquals(BallingProfileEditUiState.Loading, awaitItem())
        }
    }

    @Test
    fun `successful repository fetch updates UI state to Success`() = runTest {
        every { savedStateHandle.get<Int>("ballingProfileId") } returns 1
        repositoryFlow.value = DataFetchResult.Success(mockBallingProfiles)
        val mockBallingProfile = mockBallingProfiles.first()

        viewModel = BallingProfileEditViewModel(savedStateHandle, repository)

        viewModel.ballingProfileEditUiState.test {
            // First item is always Loading
            assertTrue(awaitItem() is BallingProfileEditUiState.Loading)

            val item = awaitItem()
            assertTrue(item is BallingProfileEditUiState.Success)
            val actualBallingProfile = (item as BallingProfileEditUiState.Success).ballingProfile
            assertEquals(
                mockBallingProfile.pumpId,
                actualBallingProfile.pumpId,
            )
            assertEquals(
                mockBallingProfile.volume,
                actualBallingProfile.volume,
                0.0
            )
            assertEquals(
                mockBallingProfile.label,
                actualBallingProfile.label
            )
        }
    }

    @Test
    fun `onVolumeChange updates unconfirmed edits without affecting repository`() = runTest {
        every { savedStateHandle.get<Int>("ballingProfileId") } returns 1
        repositoryFlow.value = DataFetchResult.Success(mockBallingProfiles)

        viewModel = BallingProfileEditViewModel(savedStateHandle, repository)

        viewModel.ballingProfileEditUiState.test {
            // First item is always Loading
            assertTrue(awaitItem() is BallingProfileEditUiState.Loading)

            awaitItem() // Initial Success

            val newDosingValue = 5.5
            viewModel.onEvent(BallingProfileEditUiEvent.ChangeDosingVolume(newDosingValue))

            val updatedState = awaitItem()
            assertTrue(updatedState is BallingProfileEditUiState.Success)
            val actualBallingProfile = (updatedState as BallingProfileEditUiState.Success).ballingProfile
            assertEquals(
                newDosingValue,
                actualBallingProfile.volume,
                0.0
            )

            // Verify repository was NOT called yet
            coVerify(exactly = 0) { repository.updateBallingProfile(any()) }
        }
    }

    @Test
    fun `onConfirmDosing triggers repository update`() = runTest {
        every { savedStateHandle.get<Int>("ballingProfileId") } returns 1
        repositoryFlow.value = DataFetchResult.Success(mockBallingProfiles)

        viewModel = BallingProfileEditViewModel(savedStateHandle, repository)

        val newDosingValue = 5.5

        viewModel.ballingProfileEditUiState.test {
            // First item is always Loading
            assertTrue(awaitItem() is BallingProfileEditUiState.Loading)

            awaitItem() // Skip initial success

            viewModel.onEvent(BallingProfileEditUiEvent.ConfirmDosingVolume(newDosingValue))

            // Give the ViewModel coroutine a chance to execute
            testDispatcher.scheduler.advanceUntilIdle()

        }

        coVerify(exactly = 1) {
            repository.updateBallingProfile(
                match { it.pumpId == 1 && it.volume == newDosingValue }
            )
        }
    }

    @Test
    fun `missing navigation ID results in Error state`() = runTest {
        every { savedStateHandle.get<Int>("ballingProfileId") } returns null

        viewModel = BallingProfileEditViewModel(savedStateHandle, repository)

        viewModel.ballingProfileEditUiState.test {
            // First item is always Loading
            assertTrue(awaitItem() is BallingProfileEditUiState.Loading)

            val item = awaitItem()
            assertTrue(item is BallingProfileEditUiState.Error)
        }
    }

    @Test
    fun `profile not found in list results in NotFound state`() = runTest {
        every { savedStateHandle.get<Int>("ballingProfileId") } returns 999 // Non-existent ID
        repositoryFlow.value = DataFetchResult.Success(mockBallingProfiles)

        viewModel = BallingProfileEditViewModel(savedStateHandle, repository)

        viewModel.ballingProfileEditUiState.test {
            // First item is always Loading
            assertTrue(awaitItem() is BallingProfileEditUiState.Loading)

            assertTrue(awaitItem() is BallingProfileEditUiState.NotFound)
        }
    }
}