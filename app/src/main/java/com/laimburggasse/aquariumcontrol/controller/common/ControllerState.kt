package com.laimburggasse.aquariumcontrol.controller.common

data class ControllerState (
    // This class represents the state of the controller screen.
    var controllerProfileList: List<ControllerProfile> = emptyList(),
    val selectedControllerProfileId: Long? = null,
) {
    /**
     * A computed property that finds and returns the ControllerProfile
     * from the list whose ID matches the `selectedControllerProfileId` of this state.
     * It returns null if no matching profile is found.
     */
    val selectedProfile: ControllerProfile?
        get() = controllerProfileList.find { it.id == selectedControllerProfileId }
}