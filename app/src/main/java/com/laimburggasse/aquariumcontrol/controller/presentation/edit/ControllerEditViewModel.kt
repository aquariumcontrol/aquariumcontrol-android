package com.laimburggasse.aquariumcontrol.controller.presentation.edit

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.laimburggasse.aquariumcontrol.common.domain.SessionManager
import com.laimburggasse.aquariumcontrol.controller.presentation.components.ControllerAddEditUiEvent
import com.laimburggasse.aquariumcontrol.controller.presentation.components.ControllerAddEditUiState
import com.laimburggasse.aquariumcontrol.common.domain.ControllerProfile
import com.laimburggasse.aquariumcontrol.common.data.ControllerProfileDao
import com.laimburggasse.aquariumcontrol.controller.domain.isHostName
import com.laimburggasse.aquariumcontrol.controller.domain.isValidPortNumber
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
    private val _state = MutableStateFlow(value = ControllerAddEditUiState())
    val state: StateFlow<ControllerAddEditUiState> = _state
        .stateIn(
            viewModelScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
            initialValue = ControllerAddEditUiState()
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

    fun onEvent(event: ControllerAddEditUiEvent) {
        when (event) {
            is ControllerAddEditUiEvent.UpdateControllerProfile -> {
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