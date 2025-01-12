package com.example.aquariumcontroller.controller

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.net.InetAddress
import kotlin.text.matches

class ControllerViewModel(private val dao: ControllerProfileDao) : ViewModel() {

    private val _controllerProfiles = dao.getAllControllerProfiles()
    private val _state = MutableStateFlow(ControllerState())

    val state = combine(_state, _controllerProfiles) { state, controllerProfileList ->
        state.copy(
            controllerProfileList = controllerProfileList
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ControllerState())

    fun onEvent(event: ControllerEvent) {
        when (event) {

            is ControllerEvent.AddControllerProfile -> {
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
                    password = controllerProfilePassword
                )
                controllerProfile.isDefault = controllerProfileIsDefault

                Log.d("ControllerViewModel", "Adding controller profile: $controllerProfile")
                viewModelScope.launch {
                    dao.upsertControllerProfile(controllerProfile)
                }
            }

            is ControllerEvent.UpdateControllerProfile -> {
                val controllerProfileId = state.value.controllerProfileId
                val controllerProfileName = state.value.controllerProfileName
                val controllerProfileAddress = state.value.controllerProfileAddress
                val controllerProfilePort = state.value.controllerProfilePort
                val controllerProfileUser = state.value.controllerProfileUser
                val controllerProfilePassword = state.value.controllerProfilePassword
                val controllerProfileIsDefault = state.value.controllerProfileIsDefault
                val controllerProfile = ControllerProfile(
                    id = controllerProfileId,
                    name = controllerProfileName,
                    address = controllerProfileAddress,
                    port = controllerProfilePort,
                    user = controllerProfileUser,
                    password = controllerProfilePassword
                )
                controllerProfile.isDefault = controllerProfileIsDefault

                Log.d("ControllerViewModel", "Updating controller profile: $controllerProfile")
                viewModelScope.launch {
                    dao.upsertControllerProfile(controllerProfile)
                }
            }

            is ControllerEvent.DeleteControllerProfile -> {
                viewModelScope.launch {
                    dao.deleteControllerProfile(event.controllerProfile)
                }
            }

            is ControllerEvent.SetControllerProfileName -> {
                _state.update {
                    it.copy(
                        controllerProfileName = event.controllerProfileName
                    )
                }
            }

            is ControllerEvent.SetControllerAddress -> {
                Log.d(
                    "ControllerViewModel",
                    "Setting controller address: ${event.controllerAddress}"
                )
                _state.update {
                    it.copy(
                        controllerProfileAddress = event.controllerAddress
                    )
                }
                if (isHostName(event.controllerAddress)) {
                    Log.d(
                        "ControllerViewModel",
                        "Invalid controller address: ${event.controllerAddress}"
                    )
                }
                _state.update {
                    it.copy(
                        validControllerAddress = isHostName(event.controllerAddress)
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
                Log.d(
                    "ControllerViewModel",
                    "Setting controller password: ${event.controllerPassword}"
                )
                _state.update {
                    it.copy(
                        controllerProfilePassword = event.controllerPassword
                    )
                }
            }

            is ControllerEvent.SetControllerPort -> {
                if (event.controllerPort.isNotEmpty()) {
                    if (isValidPortNumber(event.controllerPort.toInt())) {
                        Log.d(
                            "ControllerViewModel",
                            "Setting controller port: ${event.controllerPort}"
                        )
                        _state.update {
                            it.copy(
                                controllerProfilePort = event.controllerPort.toInt()
                            )
                        }
                    } else {
                        Log.d(
                            "ControllerViewModel",
                            "Invalid controller port: ${event.controllerPort}"
                        )
                    }
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

            is ControllerEvent.SelectControllerProfile -> {
                Log.d(
                    "ControllerViewModel",
                    "Selecting controller profile: ${event.controllerProfile}"
                )
                _state.update {
                    it.copy(
                        controllerProfileId = event.controllerProfile.id,
                        controllerProfileName = event.controllerProfile.name,
                        controllerProfileAddress = event.controllerProfile.address,
                        controllerProfilePort = event.controllerProfile.port,
                        controllerProfileUser = event.controllerProfile.user,
                        controllerProfilePassword = event.controllerProfile.password,
                    )
                }
            }
        }
    }

    private fun isHostName(address: String): Boolean {
        if (address.isBlank()) {
            return false
        } else {
            val ipv4Regex =
                "^(([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\\.){3}([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\$".toRegex()
            val ipv6Regex = "^([0-9a-fA-F]{1,4}:){7}[0-9a-fA-F]{1,4}\$".toRegex()
            val ipv6CompressedRegex =
                "^(([0-9a-fA-F]{1,4}:){0,1}(([0-9a-fA-F]{1,4}:){0,6}[0-9a-fA-F]{1,4})?::([0-9a-fA-F]{1,4}:){0,6}[0-9a-fA-F]{1,4})?\$".toRegex()
            val dnsRegex =
                "^([a-zA-Z0-9]([a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?\\.)+[a-zA-Z]{2,}\$".toRegex()

            return address.matches(ipv4Regex) ||
                    address.matches(ipv6Regex) ||
                    address.matches(ipv6CompressedRegex) ||
                    address.matches(dnsRegex)
        }
    }

    private fun isValidPortNumber(port: Int): Boolean {
        return port in 0..65535
    }
}