package com.laimburggasse.aquariumcontrol.balling.presentation.navigation

import androidx.navigation.NavHostController
import com.laimburggasse.aquariumcontrol.ViewNavigationRoutes
import com.laimburggasse.aquariumcontrol.balling.presentation.navigation.BallingNavigationEvent

fun NavHostController.handleBallingNavigation(event: BallingNavigationEvent, pumpId: Int?) {
    when (event) {
        BallingNavigationEvent.ToBallingHistory24h -> this.navigate(route = ViewNavigationRoutes.ballingHistory24h)
        BallingNavigationEvent.ToBallingHistory7d -> this.navigate(route = ViewNavigationRoutes.ballingHistory7d)
        BallingNavigationEvent.ToBallingEdit -> {
            pumpId.let {
                this.navigate(route = ViewNavigationRoutes.ballingEdit(ballingProfileId = pumpId!!))
            }
        }
    }
}