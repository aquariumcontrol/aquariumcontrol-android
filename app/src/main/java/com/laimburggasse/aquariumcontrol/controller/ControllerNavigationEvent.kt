package com.laimburggasse.aquariumcontrol.controller

sealed class ControllerNavigationEvent {
    object ToControllerAdd : ControllerNavigationEvent()
    object ToControllerEdit: ControllerNavigationEvent()
    object ToOverview: ControllerNavigationEvent()
    object ToControllerMain: ControllerNavigationEvent()
}