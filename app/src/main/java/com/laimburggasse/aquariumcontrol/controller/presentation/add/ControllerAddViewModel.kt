package com.laimburggasse.aquariumcontrol.controller.presentation.add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.laimburggasse.aquariumcontrol.controller.presentation.components.ControllerAddEditUiEvent
import com.laimburggasse.aquariumcontrol.controller.presentation.components.ControllerAddEditUiState
import com.laimburggasse.aquariumcontrol.controller.domain.ControllerProfile
import com.laimburggasse.aquariumcontrol.controller.data.ControllerProfileDao
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
                        port = state.value.controllerProfilePort,
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
                if (event.controllerPort.isNotEmpty()) {
                    if (isValidPortNumber(port = event.controllerPort.toInt())) {
                        _state.update { it.copy(controllerProfilePort = event.controllerPort.toInt()) }
                    }
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