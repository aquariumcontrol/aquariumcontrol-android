package com.laimburggasse.aquariumcontrol.heating.presentation

sealed interface HeatingSetValsUiEvent {
    data class ChangeRange(val lowerValue: Float, val upperValue: Float): HeatingSetValsUiEvent
    data class ChangeLowerValue(val lowerValue: Float): HeatingSetValsUiEvent
    data class ChangeUpperValue(val upperValue: Float): HeatingSetValsUiEvent
    object Update: HeatingSetValsUiEvent
    object Reset: HeatingSetValsUiEvent
    object Refresh: HeatingSetValsUiEvent
}