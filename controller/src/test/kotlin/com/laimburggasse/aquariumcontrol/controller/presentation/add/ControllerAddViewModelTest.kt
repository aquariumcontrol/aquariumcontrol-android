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

package com.laimburggasse.aquariumcontrol.controller.presentation.add

import app.cash.turbine.test
import com.laimburggasse.aquariumcontrol.common.data.local.ControllerProfileDao
import com.laimburggasse.aquariumcontrol.controller.presentation.components.ControllerAddEditUiEvent
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ControllerAddViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var dao: ControllerProfileDao
    private lateinit var viewModel: ControllerAddViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        dao = mockk(relaxed = true)
        viewModel = ControllerAddViewModel(dao)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state has default values`() = runTest {
        viewModel.state.test {
            val initialState = awaitItem()
            assertEquals("", initialState.controllerProfileName)
            assertEquals("", initialState.controllerProfileAddress)
            assertFalse(initialState.validControllerAddress)
        }
    }

    @Test
    fun `SetControllerProfileName updates state`() = runTest {
        viewModel.state.test {
            awaitItem() // Skip initial

            viewModel.onEvent(ControllerAddEditUiEvent.SetControllerProfileName("My Aquarium"))

            val updatedState = awaitItem()
            assertEquals("My Aquarium", updatedState.controllerProfileName)
        }
    }

    @Test
    fun `SetControllerAddress updates state and validates address`() = runTest {
        viewModel.state.test {
            awaitItem() // Skip initial

            // Act: Set a valid-looking hostname
            viewModel.onEvent(ControllerAddEditUiEvent.SetControllerAddress("aquarium.local"))

            val updatedState = awaitItem()
            assertEquals("aquarium.local", updatedState.controllerProfileAddress)
            // Note: This relies on isHostName returning true for this string
            assertTrue(updatedState.validControllerAddress)
        }
    }

    @Test
    fun `SetControllerPort updates state only for valid ports`() = runTest {
        viewModel.state.test {
            awaitItem() // Skip initial (default port 80)

            // Act: Set a valid port
            viewModel.onEvent(ControllerAddEditUiEvent.SetControllerPort("8080"))
            assertEquals("8080", awaitItem().controllerProfilePort)

            // Act: Set an invalid port (assuming isValidPortNumber checks 1-65535)
            viewModel.onEvent(ControllerAddEditUiEvent.SetControllerPort("999999"))
            // State should not have updated from 8080
            expectNoEvents()
        }
    }

    @Test
    fun `addControllerProfile triggers DAO upsert and callback`() = runTest {
        var callbackCalled = false

        // Arrange: Populate state via events
        viewModel.onEvent(ControllerAddEditUiEvent.SetControllerProfileName("Test Tank"))
        viewModel.onEvent(ControllerAddEditUiEvent.SetControllerAddress("192.168.1.50"))

        // Act
        viewModel.addControllerProfile(ControllerAddEditUiEvent.AddControllerProfile) {
            callbackCalled = true
        }

        // Process the viewModelScope.launch
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        coVerify(exactly = 1) {
            dao.upsertControllerProfile(match {
                it.name == "Test Tank" && it.address == "192.168.1.50"
            })
        }
        assertTrue(callbackCalled)
    }

    @Test
    fun `other events do not change state`() = runTest {
        viewModel.state.test {
            val initial = awaitItem()

            // This event is ignored in AddViewModel as per the "else" branch
            viewModel.onEvent(ControllerAddEditUiEvent.AddControllerProfile)

            expectNoEvents()
            assertEquals(initial, viewModel.state.value)
        }
    }
}