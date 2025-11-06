package com.laimburggasse.aquariumcontrol.balling.presentation.edit

sealed interface BallingProfileEditUiEvent {
    data class ConfirmDosingVolume(val newDosingValue: Double): BallingProfileEditUiEvent
    data class ChangeDosingVolume(val newDosingValue: Double): BallingProfileEditUiEvent
}