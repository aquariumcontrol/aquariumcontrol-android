package com.example.aquariumcontroller

data class ControllerState (
    var controllerProfileList: List<ControllerProfile> = emptyList(),
    val controllerProfileName: String = "",
    val controllerProfileAddress: String = "",
    val controllerProfilePort: Int = 0,
    val controllerProfileUser: String = "",
    val controllerProfilePassword: String = "",
    val controllerProfileIsDefault: Boolean = false,

    val isAddingControllerProfile: Boolean = false
)