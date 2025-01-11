package com.example.aquariumcontroller

sealed interface ControllerEvent {
    data object AddControllerProfile : ControllerEvent
    data object UpdateControllerProfile : ControllerEvent
    data class DeleteControllerProfile(val controllerProfile: ControllerProfile): ControllerEvent
    data class SetControllerProfileName(val controllerProfileName: String) : ControllerEvent
    data class SetControllerAddress(val controllerAddress: String) : ControllerEvent
    data class SetControllerPort(val controllerPort: String) : ControllerEvent
    data class SetControllerUser(val controllerUser: String) : ControllerEvent
    data class SetControllerPassword(val controllerPassword: String) : ControllerEvent
    data class SetControllerIsDefault(val controllerIsDefault: Boolean) : ControllerEvent

    data class SelectControllerProfile(val controllerProfile: ControllerProfile): ControllerEvent
}