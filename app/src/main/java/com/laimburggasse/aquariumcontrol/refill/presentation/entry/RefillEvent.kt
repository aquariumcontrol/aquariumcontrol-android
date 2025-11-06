package com.laimburggasse.aquariumcontrol.refill.presentation.entry

sealed interface RefillEvent {
    object StartControl: RefillEvent
    object StopControl: RefillEvent
    object ResetErrors: RefillEvent
}