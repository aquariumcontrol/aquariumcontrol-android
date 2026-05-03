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

package com.laimburggasse.aquariumcontrol.controller.presentation.add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.laimburggasse.aquariumcontrol.controller.presentation.components.ControllerAddEditUiEvent
import com.laimburggasse.aquariumcontrol.controller.presentation.components.ControllerAddEditUiState
import com.laimburggasse.aquariumcontrol.common.domain.ControllerProfile
import com.laimburggasse.aquariumcontrol.common.data.local.ControllerProfileDao
import com.laimburggasse.aquariumcontrol.controller.domain.isHostName
import com.laimburggasse.aquariumcontrol.controller.domain.isValidPortNumber
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ControllerAddViewModel @Inject constructor(
    private val dao: ControllerProfileDao,
): ViewModel() {
    private val _state = MutableStateFlow(value = ControllerAddEditUiState())

    val state: StateFlow<ControllerAddEditUiState> = _state.asStateFlow()

    fun addControllerProfile(event: ControllerAddEditUiEvent, onAddComplete: () -> Unit) {
        when (event) {
            is ControllerAddEditUiEvent.AddControllerProfile -> {
                viewModelScope.launch {
                    val controllerProfile = ControllerProfile(
                        name = state.value.controllerProfileName,
                        address = state.value.controllerProfileAddress,
                        port = state.value.controllerProfilePort.toIntOrNull() ?: 80,
                        user = state.value.controllerProfileUser,
                        password = state.value.controllerProfilePassword,
                    )

                    dao.upsertControllerProfile(controllerProfile)
                    onAddComplete()
                }
            }

            else -> {
            }
        }
    }

    fun onEvent(event: ControllerAddEditUiEvent) {
        when (event) {
            is ControllerAddEditUiEvent.SetControllerProfileName -> {
                _state.update { it.copy( controllerProfileName = event.controllerProfileName) }
            }

            is ControllerAddEditUiEvent.SetControllerAddress -> {
                _state.update { it.copy(
                        controllerProfileAddress = event.controllerAddress,
                        validControllerAddress = isHostName(event.controllerAddress)
                    )
                }
            }

            is ControllerAddEditUiEvent.SetControllerPassword -> {
                _state.update { it.copy( controllerProfilePassword = event.controllerPassword) }
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

            else -> {
                // do nothing: interface is shared with ControllerEditScreen
            }
        }
    }
}