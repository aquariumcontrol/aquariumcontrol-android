package com.laimburggasse.aquariumcontrol.maindropdownmenu

sealed class MainDropDownMenuNavigationEvent {
    object ToOverview : MainDropDownMenuNavigationEvent()
    object ToDisplayData : MainDropDownMenuNavigationEvent()
    object ToBalling : MainDropDownMenuNavigationEvent()
    object ToRefill : MainDropDownMenuNavigationEvent()
    object ToFeed : MainDropDownMenuNavigationEvent()
    object ToHeating : MainDropDownMenuNavigationEvent()
    object ToVentilation : MainDropDownMenuNavigationEvent()
    object ToSchedule : MainDropDownMenuNavigationEvent()
    object ToControllerSelection : MainDropDownMenuNavigationEvent()
    object ToInfo : MainDropDownMenuNavigationEvent()
}