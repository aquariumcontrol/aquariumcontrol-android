package com.laimburggasse.aquariumcontrol.refill.presentation.entry

sealed interface RefillUiEvent {
    object StartControl: RefillUiEvent
    object StopControl: RefillUiEvent
    object ResetErrors: RefillUiEvent
}