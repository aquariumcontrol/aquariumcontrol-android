package com.laimburggasse.aquariumcontrol.balling.presentation.edit

sealed interface BallingProfileEditEvent {
    data class ConfirmDosingVolume(val newDosingValue: Double): BallingProfileEditEvent
    data class ChangeDosingVolume(val newDosingValue: Double): BallingProfileEditEvent
}