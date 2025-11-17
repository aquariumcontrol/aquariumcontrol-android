package com.laimburggasse.aquariumcontrol.controller.presentation.components

sealed interface ControllerAddEditUiEvent {
    // This interface defines all events that can be triggered from
    // - ControllerAddScreen
    // - ControllerEditScreen

    data object AddControllerProfile : ControllerAddEditUiEvent
    data class SetControllerAddress(val controllerAddress: String) : ControllerAddEditUiEvent
    data class SetControllerPort(val controllerPort: String) : ControllerAddEditUiEvent
    data class SetControllerUser(val controllerUser: String) : ControllerAddEditUiEvent
    data class SetControllerPassword(val controllerPassword: String) : ControllerAddEditUiEvent
    data class SetDefaultControllerId(val controllerProfileId: Long): ControllerAddEditUiEvent
    data object UnsetDefaultControllerId : ControllerAddEditUiEvent
    data object UpdateControllerProfile : ControllerAddEditUiEvent
    data class SetControllerProfileName(val controllerProfileName: String) : ControllerAddEditUiEvent

}