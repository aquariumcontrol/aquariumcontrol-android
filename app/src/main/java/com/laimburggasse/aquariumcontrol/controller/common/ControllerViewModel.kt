package com.laimburggasse.aquariumcontrol.controller.common

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.laimburggasse.aquariumcontrol.MainViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.transformLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ControllerViewModel(
    private val dao: ControllerProfileDao,
    private val mainViewModel: MainViewModel
) : ViewModel() {
    // This class represents the view model for the controller screen.
    // Initialize with an empty list or a loading state if preferred
    private val _controllerProfiles = MutableStateFlow<List<ControllerProfile>>(value = emptyList())
    private val _state = MutableStateFlow(value = ControllerState())

    val state = combine(_state, _controllerProfiles) { state, controllerProfileList ->
        state.copy(
            controllerProfileList = controllerProfileList
        )
    }.stateIn(
        viewModelScope,
        started = SharingStarted.Companion.WhileSubscribed(stopTimeoutMillis = 5000),
        initialValue = ControllerState()
    )

    /**
     * A reactive state flow that is `true` only when the conditions for
     * automatically navigating to the default profile are met.
     * The UI can observe this state to trigger side effects.
     */
    val canStartWaitForDefaultNavigation: StateFlow<Boolean> = combine(
        flow = _controllerProfiles,
        flow2 = mainViewModel.isFirstLoad,
        flow3 = mainViewModel.defaultControllerProfileId
    ) { profiles, isFirstLoad, defaultId ->
        profiles.isNotEmpty() && (defaultId != null) && isFirstLoad
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
        initialValue = false
    )

    /**
     * A state flow that emits progress values from 0.0f to 1.0f over 3 seconds.
     * The countdown starts automatically when `canNavigateToDefaultProfile` becomes true.
     * It uses `transformLatest` to ensure that if the condition becomes false and then
     * true again, a new countdown is started, cancelling the old one.
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    val progressToDefaultNavigation: StateFlow<Float> =
        canStartWaitForDefaultNavigation.transformLatest { canNavigate ->
        if (canNavigate) {
            val countdownDurationMillis = 3000L
            val intervalMillis = 50L // Update ~20 times per second
            var elapsedMillis = 0L

            while (elapsedMillis < countdownDurationMillis) {
                // Emit the current progress as a float between 0.0 and 1.0
                emit(value = elapsedMillis.toFloat() / countdownDurationMillis)
                delay(timeMillis = intervalMillis)
                elapsedMillis += intervalMillis
            }
            // Ensure it emits the final 1.0f value
            emit(value = 1.0f)
        } else {
            // If the condition is not met, emit 0.0f to reset any progress indicator.
            emit(value = 0.0f)
        }

    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
        initialValue = 0.0f // The countdown starts at 0% progress
    )

    init {
        loadInitialControllerProfiles()
    }

    private fun loadInitialControllerProfiles() {
        viewModelScope.launch {
            try {
                // Fetch the initial list - suspend function call
                val profiles: List<ControllerProfile> = dao.getAllControllerProfiles()
                _controllerProfiles.value = profiles
            } catch (_: Exception) {
                // @todo: Handle any errors during fetching, e.g., log them or update UI state
            }
        }
    }

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
                val controllerProfile = state.value.selectedProfile
                controllerProfile.let {
                    mainViewModel.updateSelectedControllerProfile(controllerProfile = controllerProfile!!)
                }
                val controllerProfileId: Long = state.value.controllerProfileId
                val controllerProfileName: String = state.value.controllerProfileName
                val controllerProfileAddress: String = state.value.controllerProfileAddress
                val controllerProfilePort: Int = state.value.controllerProfilePort
                val controllerProfileUser: String = state.value.controllerProfileUser
                val controllerProfilePassword: String = state.value.controllerProfilePassword

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
                // @todo: implement
                //viewModelScope.launch {
                //    dao.deleteControllerProfile(event.controllerProfile)
                //}
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

                val selectedProfile = state.value.selectedProfile
                selectedProfile.let {
                    mainViewModel.updateSelectedControllerProfile(controllerProfile = selectedProfile!!)
                }
            }

            is ControllerEvent.LoadControllerProfiles -> {
                viewModelScope.launch {
                    try {
                        // Fetch the controller profiles from the database
                        val profiles = dao.getAllControllerProfiles() // This is your suspend function call
                        _controllerProfiles.value = profiles // Update the value of the StateFlow
                    } catch (e: Exception) {
                        Log.e("ControllerViewModel", "Error loading controller profiles on event", e)
                        // Optionally, update the UI or _controllerProfiles with an error state or empty list
                    }
                }
            }

            is ControllerEvent.SetDefaultControllerId -> {
                Log.d("ControllerViewModel", "Setting default controller profile ID: ${event.controllerProfileId}")
                mainViewModel.saveDefaultControllerProfileId(event.controllerProfileId)
            }
            ControllerEvent.UnsetDefaultControllerId -> {
                Log.d("ControllerViewModel", "Unsetting default controller profile ID")
                mainViewModel.unsetDefaultControllerId()
            }

            ControllerEvent.AutoNavigationFinished -> {
                // do nothing
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

    fun getSelectedControllerProfileById(id: Long): ControllerProfile? {
        return state.value.controllerProfileList.find { it.id == id }
    }
}