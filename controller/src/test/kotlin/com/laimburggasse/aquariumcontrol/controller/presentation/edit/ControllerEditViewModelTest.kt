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

package com.laimburggasse.aquariumcontrol.controller.presentation.edit

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.laimburggasse.aquariumcontrol.common.data.local.ControllerProfileDao
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
        viewModel = ControllerEditViewModel(dao, sessionManager)

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
        viewModel = ControllerEditViewModel(dao, sessionManager)

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
        viewModel = ControllerEditViewModel(dao, sessionManager)

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

        viewModel = ControllerEditViewModel(dao, sessionManager)

        // Wait for the init block's LaunchedEffect to finish loading from DAO
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.state.test {
            val state = awaitItem()
            assertEquals(0, state.controllerProfileId)
            assertEquals("", state.controllerProfileName)
        }
    }
}