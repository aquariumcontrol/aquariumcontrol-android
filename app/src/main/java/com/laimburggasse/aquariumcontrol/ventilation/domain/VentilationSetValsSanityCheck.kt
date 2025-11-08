package com.laimburggasse.aquariumcontrol.ventilation.domain

import com.laimburggasse.aquariumcontrol.ventilation.domain.model.VentilationSetVals
import com.laimburggasse.aquariumcontrol.ventilation.presentation.VentilationSetValsUiState

fun ventilationSetValsSanityCheck(ventilationSetVals: VentilationSetVals): VentilationSetValsUiState {
    val ventilationSetValInterval: ClosedFloatingPointRange<Double> = 20.0..30.0

    if (ventilationSetVals.ventilationSwitchOffTemp !in ventilationSetValInterval ||
        ventilationSetVals.ventilationSwitchOnTemp !in ventilationSetValInterval
    ) {
        VentilationSetValsUiState.Error(
            errorMessage = "Incorrect data received from server: values are out of range"
        )
    }
    if (ventilationSetVals.ventilationSwitchOffTemp >= ventilationSetVals.ventilationSwitchOnTemp) {
        VentilationSetValsUiState.Error(
            errorMessage = "Incorrect data received from server: switch off value must be smaller than switch on value"
        )
    }
    return VentilationSetValsUiState.Success(ventilationSetVals)
}