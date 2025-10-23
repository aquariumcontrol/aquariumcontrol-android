package com.laimburggasse.aquariumcontrol.balling.common

sealed class BallingNavigationEvent {
    object ToBallingHistory24h: BallingNavigationEvent()
    object ToBallingHistory7d: BallingNavigationEvent()
    object ToBallingEdit: BallingNavigationEvent()
}