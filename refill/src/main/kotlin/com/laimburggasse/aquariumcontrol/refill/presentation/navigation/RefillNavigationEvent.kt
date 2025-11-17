package com.laimburggasse.aquariumcontrol.refill.presentation.navigation

sealed class RefillNavigationEvent {
    object ToRefillHistory24h: RefillNavigationEvent()
    object ToRefillHistory7d: RefillNavigationEvent()
}