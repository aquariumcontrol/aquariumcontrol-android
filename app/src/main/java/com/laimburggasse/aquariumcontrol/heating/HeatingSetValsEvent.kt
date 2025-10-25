package com.laimburggasse.aquariumcontrol.heating

sealed interface HeatingSetValsEvent {
    data class ChangeRange(val lowerValue: Float, val upperValue: Float): HeatingSetValsEvent
    data class ChangeLowerValue(val lowerValue: Float): HeatingSetValsEvent
    data class ChangeUpperValue(val upperValue: Float): HeatingSetValsEvent
    object Update: HeatingSetValsEvent
    object Reset: HeatingSetValsEvent
}