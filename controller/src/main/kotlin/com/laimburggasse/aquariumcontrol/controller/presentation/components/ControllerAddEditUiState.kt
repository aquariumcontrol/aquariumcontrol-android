package com.laimburggasse.aquariumcontrol.controller.presentation.components

data class ControllerAddEditUiState (
    // This class represents the state of the controller add or edit screen.
    val controllerProfileId: Long = 0,
    val controllerProfileName: String = "",
    val controllerProfileAddress: String = "",
    val controllerProfilePort: Int = 80,
    val controllerProfileUser: String = "",
    val controllerProfilePassword: String = "",
    val validControllerAddress: Boolean = true
)