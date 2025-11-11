package com.laimburggasse.aquariumcontrol.ventilation.presentation

sealed interface VentilationSetValsUiEvent {
    data class ChangeRange(val lowerValue: Float, val upperValue: Float): VentilationSetValsUiEvent
    data class ChangeLowerValue(val lowerValue: Float): VentilationSetValsUiEvent
    data class ChangeUpperValue(val upperValue: Float): VentilationSetValsUiEvent
    object Update: VentilationSetValsUiEvent
    object Reset: VentilationSetValsUiEvent
    object Refresh: VentilationSetValsUiEvent
}