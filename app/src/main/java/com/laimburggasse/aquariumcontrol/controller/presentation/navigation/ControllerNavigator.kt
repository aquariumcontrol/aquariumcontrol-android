package com.laimburggasse.aquariumcontrol.controller.presentation.navigation

import android.util.Log
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
                Log.d("handleControllerNavigation", "profileId: $profileId")
                this.navigate(route = ViewNavigationRoutes.overView(controllerProfileId = profileId))
            }
            else {
                Log.d("handleControllerNavigation", "profileId is null")
            }
        }
        ControllerNavigationEvent.ToControllerMain -> {
            this.navigate(route = ViewNavigationRoutes.controllerSelection(controllerProfileId = -1))
        }
    }
}