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

class ControllerViewModel(private val dao: ControllerProfileDao) : ViewModel() {
    // This class represents the view model for the controller screen.
    private var _controllerProfiles = dao.getAllControllerProfiles()
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

                viewModelScope.launch {
                    val controllerProfile = ControllerProfile(
                        name = controllerProfileName,
                        address = controllerProfileAddress,
                        port = controllerProfilePort,
                        user = controllerProfileUser,
                        password = controllerProfilePassword,
                    )

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

                viewModelScope.launch {
                    val controllerProfile = ControllerProfile(
                        id = controllerProfileId,
                        name = controllerProfileName,
                        address = controllerProfileAddress,
                        port = controllerProfilePort,
                        user = controllerProfileUser,
                        password = controllerProfilePassword
                    )
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

            is ControllerEvent.SetControllerPassword -> {
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
                            this::class.simpleName,
                            "Setting controller port: ${event.controllerPort}"
                        )
                        _state.update {
                            it.copy(
                                controllerProfilePort = event.controllerPort.toInt()
                            )
                        }
                    }
                }
            }

            is ControllerEvent.SetControllerUser -> {
                _state.update {
                    it.copy(
                        controllerProfileUser = event.controllerUser
                    )
                }
            }

            is ControllerEvent.SelectControllerProfile -> {
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

            is ControllerEvent.LoadControllerProfiles -> {
                viewModelScope.launch {
                    // Fetch the controller profiles from the database
                    _controllerProfiles = dao.getAllControllerProfiles()
                }
            }
        }
    }

    /**
     * Checks if the provided string is not empty and compatible to
     * IPv4 address, IPv6 address or DNS hostname.
     *
     * @param address The string to check
     * - IPv4 Address:
     * Four decimal numbers (0-255) separated by dots (.).
     * Example: 192.168.1.100
     * - IPv6 Address:
     * Eight groups of hexadecimal numbers (0-FFFF) separated by colons (:).
     * Can contain leading zeros and double colons (::) to represent consecutive groups of zeros.
     * Example: 2001:0db8:85a3:0000:0000:8a2e:0370:7334, 2001:db8::1
     * - DNS Hostname:
     * Example: www.example.com, my-server.example.org, localhost
     */
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

    /**
     * Checks if the provided port number is valid.
     *
     * @param port The port number to check
     */
    private fun isValidPortNumber(port: Int): Boolean {
        return port in 0..65535
    }
}
