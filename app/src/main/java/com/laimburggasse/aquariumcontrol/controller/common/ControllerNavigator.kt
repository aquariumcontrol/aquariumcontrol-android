package com.laimburggasse.aquariumcontrol.controller.common

import androidx.navigation.NavHostController
import com.laimburggasse.aquariumcontrol.ViewNavigationRoutes

fun NavHostController.handleControllerNavigation(event: ControllerNavigationEvent, profileId: Long?) {
    when (event) {
        ControllerNavigationEvent.ToControllerEdit -> {
            if (profileId != null) {
                this.navigate(route = ViewNavigationRoutes.controllerEdit(controllerProfileId = profileId))
            }
        }
        ControllerNavigationEvent.ToControllerAdd -> this.navigate(route = ViewNavigationRoutes.controllerAdd)
        ControllerNavigationEvent.ToOverview -> {
            if (profileId != null) {
                this.navigate(route = ViewNavigationRoutes.overView(controllerProfileId = profileId))
            }
        }
        ControllerNavigationEvent.ToControllerMain -> {
            if (profileId != null) {
                this.navigate(route = ViewNavigationRoutes.controllerSelection(controllerProfileId = profileId))
            }
        }
    }
}