package com.laimburggasse.aquariumcontrol.controller.selection

import com.laimburggasse.aquariumcontrol.controller.common.ControllerProfile

sealed interface ControllerSelectionEvent {
    // This interface defines all events that can be triggered from
    // - ControllerEditScreen
    // - ControllerSelectionScreen

    //data object LoadControllerSelectionProfiles : ControllerSelectionEvent

    data class DeleteControllerSelectionProfile(val controllerProfile: ControllerProfile): ControllerSelectionEvent

    data class SelectControllerProfile(val controllerProfile: ControllerProfile): ControllerSelectionEvent

    data object AutoNavigationFinished: ControllerSelectionEvent
}