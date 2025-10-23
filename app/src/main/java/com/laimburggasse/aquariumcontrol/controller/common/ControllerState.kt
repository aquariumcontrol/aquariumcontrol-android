package com.laimburggasse.aquariumcontrol.controller.common

import com.laimburggasse.aquariumcontrol.controller.common.ControllerProfile

data class ControllerState (
    // This class represents the state of the controller screen.
    var controllerProfileList: List<ControllerProfile> = emptyList(),
    val controllerProfileId: Long = 0,
    val controllerProfileName: String = "",
    val controllerProfileAddress: String = "",
    val controllerProfilePort: Int = 0,
    val controllerProfileUser: String = "",
    val controllerProfilePassword: String = "",
    val validControllerAddress: Boolean = true
) {
    /**
     * A computed property that finds and returns the ControllerProfile
     * from the list whose ID matches the `controllerProfileId` of this state.
     * It returns null if no matching profile is found.
     */
    val selectedProfile: ControllerProfile?
        get() = controllerProfileList.find { it.id == controllerProfileId }
}