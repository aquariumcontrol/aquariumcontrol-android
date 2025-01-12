package com.example.aquariumcontroller.controller

data class ControllerState (
    var controllerProfileList: List<ControllerProfile> = emptyList(),
    val controllerProfileId: Long = 0,
    val controllerProfileName: String = "",
    val controllerProfileAddress: String = "",
    val controllerProfilePort: Int = 0,
    val controllerProfileUser: String = "",
    val controllerProfilePassword: String = "",
    val controllerProfileIsDefault: Boolean = false,
    val validControllerAddress: Boolean = true
)