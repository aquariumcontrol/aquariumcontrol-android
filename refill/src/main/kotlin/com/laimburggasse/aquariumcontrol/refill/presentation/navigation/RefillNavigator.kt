package com.laimburggasse.aquariumcontrol.refill.presentation.navigation

import androidx.navigation.NavHostController
import com.laimburggasse.aquariumcontrol.common.presentation.ViewNavigationRoutes

fun NavHostController.handleRefillNavigation(event: RefillNavigationEvent) {
    when (event) {
        RefillNavigationEvent.ToRefillHistory7d -> this.navigate(route = ViewNavigationRoutes.refillHistory7d)
        RefillNavigationEvent.ToRefillHistory24h -> this.navigate(route = ViewNavigationRoutes.refillHistory24h)
    }
}