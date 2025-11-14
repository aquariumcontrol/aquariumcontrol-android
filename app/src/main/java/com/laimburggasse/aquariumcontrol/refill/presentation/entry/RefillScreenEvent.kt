package com.laimburggasse.aquariumcontrol.refill.presentation.entry

interface RefillScreenEvent {
    data class ShowToast(val message: String) : RefillScreenEvent
}