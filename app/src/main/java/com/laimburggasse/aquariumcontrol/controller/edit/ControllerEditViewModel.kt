package com.laimburggasse.aquariumcontrol.controller.edit

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.laimburggasse.aquariumcontrol.SessionManager
import com.laimburggasse.aquariumcontrol.controller.common.ControllerAddEditEvent
import com.laimburggasse.aquariumcontrol.controller.common.ControllerAddEditState
import com.laimburggasse.aquariumcontrol.controller.common.ControllerProfile
import com.laimburggasse.aquariumcontrol.controller.common.ControllerProfileDao
import com.laimburggasse.aquariumcontrol.controller.common.isHostName
import com.laimburggasse.aquariumcontrol.controller.common.isValidPortNumber
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ControllerEditViewModel @Inject constructor(
    private val dao: ControllerProfileDao,
    private val sessionManager: SessionManager,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    // This class represents the view model for the controller edit screen.

    // Get the ID directly from the navigation arguments
    private val controllerProfileId: Long? = savedStateHandle.get<Long>("controllerProfileId")?.let {
        if (it == -1L) null else it
    }
    private val _state = MutableStateFlow(value = ControllerAddEditState())
    val state: StateFlow<ControllerAddEditState> = _state
        .stateIn(
            viewModelScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
            initialValue = ControllerAddEditState()
        )

    init {
        // In the init block, load the profile from the database using the provided ID.
        // This populates the ViewModel's state as soon as it's created.
        if (controllerProfileId != -1L) {
            viewModelScope.launch {
                dao.getControllerProfileById(id = controllerProfileId!!)?.let { profile ->
                    _state.update {
                        it.copy(
                            controllerProfileId = profile.id,
                            controllerProfileName = profile.name,
                            controllerProfileAddress = profile.address,
                            controllerProfilePort = profile.port,
                            controllerProfileUser = profile.user,
                            controllerProfilePassword = profile.password
                        )
                    }
                }
            }
        }
    }

    fun onEvent(event: ControllerAddEditEvent) {
        when (event) {
            is ControllerAddEditEvent.UpdateControllerProfile -> {
                viewModelScope.launch {
                    val profileToUpdate = ControllerProfile(
                        id = _state.value.controllerProfileId,
                        name = _state.value.controllerProfileName,
                        address = _state.value.controllerProfileAddress,
                        port = _state.value.controllerProfilePort,
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
            is ControllerAddEditEvent.SetControllerProfileName -> {
                _state.update { it.copy(controllerProfileName = event.controllerProfileName) }
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
            is ControllerAddEditEvent.SetDefaultControllerId -> {
                sessionManager.saveDefaultControllerProfileId(event.controllerProfileId)
            }
            is ControllerAddEditEvent.UnsetDefaultControllerId -> {
                sessionManager.unsetDefaultControllerId()
            }
            else -> {
                // do nothing (interface is shared with ControllerAddScreen)
            }
        }
    }
}