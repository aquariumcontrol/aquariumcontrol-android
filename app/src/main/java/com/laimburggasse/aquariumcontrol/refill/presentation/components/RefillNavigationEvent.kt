package com.laimburggasse.aquariumcontrol.refill.presentation.components

sealed class RefillNavigationEvent {
    object ToRefillHistory24h: RefillNavigationEvent()
    object ToRefillHistory7d: RefillNavigationEvent()
}