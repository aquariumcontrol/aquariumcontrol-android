package com.laimburggasse.aquariumcontrol.refill.common

sealed class RefillNavigationEvent {
    object ToRefillHistory24h: RefillNavigationEvent()
    object ToRefillHistory7d: RefillNavigationEvent()
}