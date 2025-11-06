package com.laimburggasse.aquariumcontrol.ventilation.presentation

sealed interface VentilationSetValsEvent {
    data class ChangeRange(val lowerValue: Float, val upperValue: Float): VentilationSetValsEvent
    data class ChangeLowerValue(val lowerValue: Float): VentilationSetValsEvent
    data class ChangeUpperValue(val upperValue: Float): VentilationSetValsEvent
    object Update: VentilationSetValsEvent
    object Reset: VentilationSetValsEvent
}