package com.laimburggasse.aquariumcontrol.controller

import androidx.navigation.NavHostController
import com.laimburggasse.aquariumcontrol.ViewNavigationRoutes

fun NavHostController.handleControllerNavigation(event: ControllerNavigationEvent, profileId: Long?) {
    when (event) {
        ControllerNavigationEvent.ToControllerEdit -> {
            profileId.let {
                this.navigate(route = ViewNavigationRoutes.controllerEdit(controllerProfileId = profileId!!))
            }
        }
        ControllerNavigationEvent.ToControllerAdd -> this.navigate(route = ViewNavigationRoutes.controllerAdd)
        ControllerNavigationEvent.ToOverview -> this.navigate(route = ViewNavigationRoutes.overView)
        ControllerNavigationEvent.ToControllerMain -> this.navigate(route = ViewNavigationRoutes.controllerSelection)
    }
}