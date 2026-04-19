package com.laimburggasse.aquariumcontrol.controller.presentation.edit

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.laimburggasse.aquariumcontrol.common.data.ControllerProfileDao
import com.laimburggasse.aquariumcontrol.common.domain.SessionManager
import com.laimburggasse.aquariumcontrol.controller.presentation.components.ControllerAddEditUiEvent
import com.laimburggasse.aquariumcontrol.controller.testing.ControllerMockData.mockControllerProfile1
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
import org.junit.Assert.assertFalse
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ControllerEditViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var dao: ControllerProfileDao
    private lateinit var sessionManager: SessionManager
    private lateinit var savedStateHandle: SavedStateHandle
    private lateinit var viewModel: ControllerEditViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        dao = mockk(relaxed = true)
        sessionManager = mockk(relaxed = true)
        savedStateHandle = mockk()

        // Mock the navigation argument
        every { savedStateHandle.get<Long>("controllerProfileId") } returns 1L

        // Mock SessionManager flow to avoid null pointer issues
        every { sessionManager.selectedControllerProfile } returns MutableStateFlow(null)

        // Mock the DAO to return our profile
        coEvery { dao.getControllerProfileById(1) } returns mockControllerProfile1
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is populated from DAO`() = runTest {
        viewModel = ControllerEditViewModel(dao, sessionManager, savedStateHandle)

        // Wait for the init block's LaunchedEffect to finish loading from DAO
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.state.test {
            val state = awaitItem()
            assertEquals("", state.controllerProfileName)
            assertEquals("", state.controllerProfileAddress)
            assertEquals(0L, state.controllerProfileId)
            assertFalse(state.validControllerAddress)
        }
    }

    @Test
    fun `SetControllerProfileName updates state correctly`() = runTest {
        viewModel = ControllerEditViewModel(dao, sessionManager, savedStateHandle)

        // Wait for the init block's LaunchedEffect to finish loading from DAO
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.state.test {
            awaitItem() // Initial state from DAO

            viewModel.onEvent(ControllerAddEditUiEvent.SetControllerProfileName("New Name"))

            val updatedState = awaitItem()
            assertEquals("New Name", updatedState.controllerProfileName)
        }
    }

    @Test
    fun `updateControllerProfile triggers DAO upsert with correct ID`() = runTest {
        viewModel = ControllerEditViewModel(dao, sessionManager, savedStateHandle)

        // Wait for the init block's LaunchedEffect to finish loading from DAO
        testDispatcher.scheduler.advanceUntilIdle()

        // Act: Update name and trigger save
        viewModel.onEvent(ControllerAddEditUiEvent.SetControllerProfileName("Updated Tank"))

        viewModel.onEvent(ControllerAddEditUiEvent.UpdateControllerProfile)

        // Process viewModelScope.launch
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert: Ensure DAO was called with the existing ID (1) and updated name
        coVerify(exactly = 1) {
            dao.upsertControllerProfile(match {
                it.id == 1L && it.name == "Updated Tank"
            })
        }
    }

    @Test
    fun `missing ID in savedStateHandle results in default state`() = runTest {
        every { savedStateHandle.get<Int>("controllerProfileId") } returns null

        viewModel = ControllerEditViewModel(dao, sessionManager, savedStateHandle)

        // Wait for the init block's LaunchedEffect to finish loading from DAO
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.state.test {
            val state = awaitItem()
            assertEquals(0, state.controllerProfileId)
            assertEquals("", state.controllerProfileName)
        }
    }
}