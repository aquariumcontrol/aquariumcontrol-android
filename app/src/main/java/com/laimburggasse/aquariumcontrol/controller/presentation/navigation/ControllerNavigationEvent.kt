package com.laimburggasse.aquariumcontrol.controller.presentation.navigation

sealed class ControllerNavigationEvent {
    object ToControllerAdd : ControllerNavigationEvent()
    object ToControllerEdit: ControllerNavigationEvent()
    object ToOverview: ControllerNavigationEvent()
    object ToControllerMain: ControllerNavigationEvent()
}