package com.laimburggasse.aquariumcontrol.controller.add

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.laimburggasse.aquariumcontrol.controller.common.ControllerAddEditEvent
import com.laimburggasse.aquariumcontrol.controller.common.ControllerProfile
import com.laimburggasse.aquariumcontrol.controller.common.ControllerProfileDao
import com.laimburggasse.aquariumcontrol.controller.common.ControllerState
import com.laimburggasse.aquariumcontrol.controller.common.isHostName
import com.laimburggasse.aquariumcontrol.controller.common.isValidPortNumber
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ControllerAddViewModel(
    private val dao: ControllerProfileDao,
): ViewModel() {
    private val _state = MutableStateFlow(value = ControllerState())

    val state: StateFlow<ControllerState> = _state.asStateFlow()

    fun addControllerProfile(event: ControllerAddEditEvent, onAddComplete: () -> Unit) {
        when (event) {
            is ControllerAddEditEvent.AddControllerProfile -> {
                val controllerProfileName: String = state.value.controllerProfileName
                val controllerProfileAddress: String = state.value.controllerProfileAddress
                val controllerProfilePort: Int = state.value.controllerProfilePort
                val controllerProfileUser: String = state.value.controllerProfileUser
                val controllerProfilePassword: String = state.value.controllerProfilePassword

                Log.d(
                    this::class.simpleName,
                    "Adding controller profile: $controllerProfileName"
                )
                viewModelScope.launch {
                    val controllerProfile = ControllerProfile(
                        name = controllerProfileName,
                        address = controllerProfileAddress,
                        port = controllerProfilePort,
                        user = controllerProfileUser,
                        password = controllerProfilePassword,
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
                Log.d(
                    this::class.simpleName,
                    "Setting controller profile name: ${event.controllerProfileName}"
                )
                _state.update {
                    it.copy(
                        controllerProfileName = event.controllerProfileName
                    )
                }
            }

            is ControllerAddEditEvent.SetControllerAddress -> {
                _state.update {
                    it.copy(
                        controllerProfileAddress = event.controllerAddress
                    )
                }
                if (isHostName(event.controllerAddress)) {
                    Log.d(
                        this::class.simpleName,
                        "Invalid controller address: ${event.controllerAddress}"
                    )
                }
                _state.update {
                    it.copy(
                        validControllerAddress = isHostName(event.controllerAddress)
                    )
                }
            }

            is ControllerAddEditEvent.SetControllerPassword -> {
                _state.update {
                    it.copy(
                        controllerProfilePassword = event.controllerPassword
                    )
                }
            }

            is ControllerAddEditEvent.SetControllerPort -> {
                if (event.controllerPort.isNotEmpty()) {
                    if (isValidPortNumber(port = event.controllerPort.toInt())) {
                        _state.update {
                            it.copy(
                                controllerProfilePort = event.controllerPort.toInt()
                            )
                        }
                    }
                }
            }

            is ControllerAddEditEvent.SetControllerUser -> {
                _state.update {
                    it.copy(
                        controllerProfileUser = event.controllerUser
                    )
                }
            }

            else -> {
                // do nothing: interface is shared with ControllerEditScreen
            }
        }
    }
}