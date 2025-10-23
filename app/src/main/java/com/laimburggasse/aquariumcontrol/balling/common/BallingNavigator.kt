package com.laimburggasse.aquariumcontrol.balling.common

import androidx.navigation.NavHostController
import com.laimburggasse.aquariumcontrol.ViewNavigationRoutes

fun NavHostController.handleBallingNavigation(event: BallingNavigationEvent, pumpId: Int?) {
    when (event) {
        BallingNavigationEvent.ToBallingHistory24h -> this.navigate(route = ViewNavigationRoutes.ballingHistory7d)
        BallingNavigationEvent.ToBallingHistory7d -> this.navigate(route = ViewNavigationRoutes.ballingHistory7d)
        BallingNavigationEvent.ToBallingEdit -> {
            pumpId.let {
                this.navigate(route = ViewNavigationRoutes.ballingEdit(ballingProfileId = pumpId!!))
            }
        }
    }
}