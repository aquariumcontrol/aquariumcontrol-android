package com.laimburggasse.aquariumcontrol.controller.presentation.components

sealed class ControllerNavigationEvent {
    object ToControllerAdd : ControllerNavigationEvent()
    object ToControllerEdit: ControllerNavigationEvent()
    object ToOverview: ControllerNavigationEvent()
    object ToControllerMain: ControllerNavigationEvent()
}