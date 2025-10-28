package com.laimburggasse.aquariumcontrol.controller.add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.laimburggasse.aquariumcontrol.controller.common.ControllerAddEditEvent
import com.laimburggasse.aquariumcontrol.controller.common.ControllerAddEditState
import com.laimburggasse.aquariumcontrol.controller.common.ControllerProfile
import com.laimburggasse.aquariumcontrol.controller.common.ControllerProfileDao
import com.laimburggasse.aquariumcontrol.controller.common.isHostName
import com.laimburggasse.aquariumcontrol.controller.common.isValidPortNumber
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
    private val _state = MutableStateFlow(value = ControllerAddEditState())

    val state: StateFlow<ControllerAddEditState> = _state.asStateFlow()

    fun addControllerProfile(event: ControllerAddEditEvent, onAddComplete: () -> Unit) {
        when (event) {
            is ControllerAddEditEvent.AddControllerProfile -> {
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

    fun onEvent(event: ControllerAddEditEvent) {
        when (event) {
            is ControllerAddEditEvent.SetControllerProfileName -> {
                _state.update { it.copy( controllerProfileName = event.controllerProfileName) }
            }

            is ControllerAddEditEvent.SetControllerAddress -> {
                _state.update { it.copy(
                        controllerProfileAddress = event.controllerAddress,
                        validControllerAddress = isHostName(event.controllerAddress)
                    )
                }
            }

            is ControllerAddEditEvent.SetControllerPassword -> {
                _state.update { it.copy( controllerProfilePassword = event.controllerPassword) }
            }

            is ControllerAddEditEvent.SetControllerPort -> {
                if (event.controllerPort.isNotEmpty()) {
                    if (isValidPortNumber(port = event.controllerPort.toInt())) {
                        _state.update { it.copy(controllerProfilePort = event.controllerPort.toInt()) }
                    }
                }
            }

            is ControllerAddEditEvent.SetControllerUser -> {
                _state.update { it.copy(controllerProfileUser = event.controllerUser) }
            }

            else -> {
                // do nothing: interface is shared with ControllerEditScreen
            }
        }
    }
}