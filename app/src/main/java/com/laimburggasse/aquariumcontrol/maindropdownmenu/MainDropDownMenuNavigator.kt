package com.laimburggasse.aquariumcontrol.maindropdownmenu

import androidx.navigation.NavHostController
import com.laimburggasse.aquariumcontrol.ViewNavigationRoutes

fun NavHostController.handleMainDropDownMenuNavigation(event: MainDropDownMenuNavigationEvent) {
    when (event) {
        MainDropDownMenuNavigationEvent.ToOverview -> this.navigate(route = ViewNavigationRoutes.overView)
        MainDropDownMenuNavigationEvent.ToSchedule -> this.navigate(route = ViewNavigationRoutes.schedule)
        MainDropDownMenuNavigationEvent.ToControllerSelection -> this.navigate(route = ViewNavigationRoutes.controllerSelection)
        MainDropDownMenuNavigationEvent.ToDisplayData -> this.navigate(route = ViewNavigationRoutes.timeData)
        MainDropDownMenuNavigationEvent.ToBalling -> this.navigate(route = ViewNavigationRoutes.balling)
        MainDropDownMenuNavigationEvent.ToRefill -> this.navigate(route = ViewNavigationRoutes.refill)
        MainDropDownMenuNavigationEvent.ToFeed -> this.navigate(route = ViewNavigationRoutes.feed)
        MainDropDownMenuNavigationEvent.ToHeating -> this.navigate(route = ViewNavigationRoutes.heating)
        MainDropDownMenuNavigationEvent.ToVentilation -> this.navigate(route = ViewNavigationRoutes.ventilation)
        MainDropDownMenuNavigationEvent.ToInfo -> this.navigate(route = ViewNavigationRoutes.info)
    }
}