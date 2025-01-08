package com.example.aquariumcontroller

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ControllerViewModel(private val dao: ControllerProfileDao): ViewModel() {

    private val _controllerProfiles = dao.getAllControllerProfiles()
    private val _state = MutableStateFlow(ControllerState())

    val state = combine(_state, _controllerProfiles) { state, controllerProfileList ->
        state.copy(
            controllerProfileList = controllerProfileList
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ControllerState())

    fun onEvent(event: ControllerEvent) {
        when (event) {
            is ControllerEvent.SetControllerProfileName -> {
                _state.update {
                    it.copy(
                        controllerProfileName = event.controllerProfileName
                    )
                }
            }

            ControllerEvent.AddControllerProfile -> {
                val controllerProfileName = state.value.controllerProfileName
                val controllerProfileAddress = state.value.controllerProfileAddress
                val controllerProfilePort = state.value.controllerProfilePort
                val controllerProfileUser = state.value.controllerProfileUser
                val controllerProfilePassword = state.value.controllerProfilePassword
                val controllerProfileIsDefault = state.value.controllerProfileIsDefault
                val controllerProfile = ControllerProfile(
                    name = controllerProfileName,
                    address = controllerProfileAddress,
                    port = controllerProfilePort,
                    user = controllerProfileUser,
                    password = controllerProfilePassword)
                controllerProfile.isDefault = controllerProfileIsDefault

                Log.d("ControllerViewModel", "Adding controller profile: $controllerProfile")
                viewModelScope.launch {
                    dao.upsertControllerProfile(controllerProfile)
                }
            }

            is ControllerEvent.DeleteControllerProfile -> {
                viewModelScope.launch {
                    dao.deleteControllerProfile(event.controllerProfile)
                }
            }

            is ControllerEvent.SetControllerAddress -> {
                Log.d("ControllerViewModel", "Setting controller address: ${event.controllerAddress}")
                _state.update {
                    it.copy(
                        controllerProfileAddress = event.controllerAddress
                    )
                }
            }

            is ControllerEvent.SetControllerIsDefault -> {
                _state.update {
                    it.copy(
                        controllerProfileIsDefault = event.controllerIsDefault
                    )
                }
            }

            is ControllerEvent.SetControllerPassword -> {
                Log.d("ControllerViewModel", "Setting controller password: ${event.controllerPassword}")
                _state.update {
                    it.copy(
                        controllerProfilePassword = event.controllerPassword
                    )
                }
            }

            is ControllerEvent.SetControllerPort -> {
                Log.d("ControllerViewModel", "Setting controller port: ${event.controllerPort}")
                _state.update {
                    it.copy(
                        controllerProfilePort = event.controllerPort.toInt()
                    )
                }
            }

            is ControllerEvent.SetControllerUser -> {
                Log.d("ControllerViewModel", "Setting controller user: ${event.controllerUser}")
                _state.update {
                    it.copy(
                        controllerProfileUser = event.controllerUser
                    )
                }
            }
        }
    }
}