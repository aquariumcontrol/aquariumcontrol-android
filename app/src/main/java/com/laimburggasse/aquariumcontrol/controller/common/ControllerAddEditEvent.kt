package com.laimburggasse.aquariumcontrol.controller.common

sealed interface ControllerAddEditEvent {
    // This interface defines all events that can be triggered from
    // - ControllerAddScreen
    // - ControllerEditScreen

    data object AddControllerProfile : ControllerAddEditEvent
    data class SetControllerAddress(val controllerAddress: String) : ControllerAddEditEvent
    data class SetControllerPort(val controllerPort: String) : ControllerAddEditEvent
    data class SetControllerUser(val controllerUser: String) : ControllerAddEditEvent
    data class SetControllerPassword(val controllerPassword: String) : ControllerAddEditEvent
    data class SetDefaultControllerId(val controllerProfileId: Long): ControllerAddEditEvent
    data object UnsetDefaultControllerId : ControllerAddEditEvent
    data object UpdateControllerProfile : ControllerAddEditEvent
    data class SetControllerProfileName(val controllerProfileName: String) : ControllerAddEditEvent

}