package com.laimburggasse.aquariumcontrol.controller.presentation.selection

import com.laimburggasse.aquariumcontrol.common.domain.ControllerProfile

sealed interface ControllerSelectionUiEvent {
    // This interface defines all events that can be triggered from
    // - ControllerEditScreen
    // - ControllerSelectionScreen

    //data object LoadControllerSelectionProfiles : ControllerSelectionEvent

    data class DeleteControllerSelectionProfile(val controllerProfile: ControllerProfile): ControllerSelectionUiEvent

    data class SelectControllerProfile(val controllerProfile: ControllerProfile): ControllerSelectionUiEvent

    data object AutoNavigationFinished: ControllerSelectionUiEvent
}