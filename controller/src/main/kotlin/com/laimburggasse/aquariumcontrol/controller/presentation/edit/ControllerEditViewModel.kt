/*
 * Copyright 2025 Uwe Martin
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

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.laimburggasse.aquariumcontrol.common.data.local.ControllerProfileDao
import com.laimburggasse.aquariumcontrol.common.domain.ControllerProfile
import com.laimburggasse.aquariumcontrol.common.domain.SessionManager
import com.laimburggasse.aquariumcontrol.controller.domain.isHostName
import com.laimburggasse.aquariumcontrol.controller.domain.isValidPortNumber
import com.laimburggasse.aquariumcontrol.controller.presentation.components.ControllerAddEditUiEvent
import com.laimburggasse.aquariumcontrol.controller.presentation.components.ControllerAddEditUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Inject

/**
 * ViewModel for the Controller profile editing screen.
 * Handles the retrieval, modification, and persistence of an existing controller profile.
 *
 * @property dao The Data Access Object for database operations on controller profiles.
 * @property sessionManager The manager for global session state and profile selection.
 */
class ControllerEditViewModel @Inject constructor(
    private val dao: ControllerProfileDao,
    private val sessionManager: SessionManager
) : ViewModel() {
    /** Internal mutable state flow for the editing screen. */
    private val _state = MutableStateFlow(value = ControllerAddEditUiState())

    /** Public read-only UI state observed by the view. */
    val state: StateFlow<ControllerAddEditUiState> = _state
        .stateIn(
            viewModelScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
            initialValue = ControllerAddEditUiState()
        )

    
    /**
     * Loads the controller profile data from the database for the given ID and updates the state.
     *
     * @param id The unique identifier of the controller profile to edit.
     */
    fun setControllerProfileId(id: Long) {
        if (id == -1L) return
        viewModelScope.launch {
            dao.getControllerProfileById(id = id)?.let { profile ->
                _state.update {
                    it.copy(
                        controllerProfileId = profile.id,
                        controllerProfileName = profile.name,
                        controllerProfileAddress = profile.address,
                        controllerProfilePort = profile.port.toString(),
                        controllerProfileUser = profile.user,
                        controllerProfilePassword = profile.password,
                        validControllerAddress = isHostName(profile.address)
                    )
                }
            }
        }
    }

    
    /**
     * Central event handler for all UI interactions on the controller edit screen.
     * Manages input updates, profile persistence, and default profile settings.
     *
     * @param event The specific [ControllerAddEditUiEvent] to process.
     */
    fun onEvent(event: ControllerAddEditUiEvent) {
        when (event) {
            is ControllerAddEditUiEvent.UpdateControllerProfile -> {
                viewModelScope.launch {
                    val profileToUpdate = ControllerProfile(
                        id = _state.value.controllerProfileId,
                        name = _state.value.controllerProfileName,
                        address = _state.value.controllerProfileAddress,
                        port = _state.value.controllerProfilePort.toIntOrNull() ?: 80,
                        user = _state.value.controllerProfileUser,
                        password = _state.value.controllerProfilePassword
                    )
                    dao.upsertControllerProfile(controllerProfile = profileToUpdate)

                    // Update the session manager if this is the currently selected profile
                    if (sessionManager.selectedControllerProfile.value?.id == profileToUpdate.id) {
                        sessionManager.updateSelectedControllerProfile(controllerProfile = profileToUpdate)
                    }
                }
            }
            is ControllerAddEditUiEvent.SetControllerProfileName -> {
                _state.update { it.copy(controllerProfileName = event.controllerProfileName) }
            }
            is ControllerAddEditUiEvent.SetControllerAddress -> {
                _state.update { it.copy(
                    controllerProfileAddress = event.controllerAddress,
                    validControllerAddress = isHostName(event.controllerAddress)
                )
                }
            }
            is ControllerAddEditUiEvent.SetControllerPassword -> {
                _state.update { it.copy(controllerProfilePassword = event.controllerPassword) }
            }
            is ControllerAddEditUiEvent.SetControllerPort -> {
                val port = event.controllerPort.toIntOrNull()
                if (event.controllerPort.isEmpty() || (port != null && isValidPortNumber(port))) {
                    _state.update { it.copy(controllerProfilePort = event.controllerPort) }
                }
            }
            is ControllerAddEditUiEvent.SetControllerUser -> {
                _state.update { it.copy(controllerProfileUser = event.controllerUser) }
            }
            is ControllerAddEditUiEvent.SetDefaultControllerId -> {
                sessionManager.saveDefaultControllerProfileId(event.controllerProfileId)
            }
            is ControllerAddEditUiEvent.UnsetDefaultControllerId -> {
                sessionManager.unsetDefaultControllerId()
            }
            else -> {
                // do nothing (interface is shared with ControllerAddScreen)
            }
        }
    }
}