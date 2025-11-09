package com.laimburggasse.aquariumcontrol.ventilation.domain

import com.laimburggasse.aquariumcontrol.lib_model.SetValsSanityCheckResult
import com.laimburggasse.aquariumcontrol.ventilation.domain.model.VentilationSetVals

fun ventilationSetValsSanityCheck(ventilationSetVals: VentilationSetVals):
        SetValsSanityCheckResult<Unit> {
    val ventilationSetValInterval: ClosedFloatingPointRange<Double> = 20.0..30.0

    if (ventilationSetVals.ventilationSwitchOffTemp !in ventilationSetValInterval ||
        ventilationSetVals.ventilationSwitchOnTemp !in ventilationSetValInterval
    ) {
        return SetValsSanityCheckResult.Error(
            errorMessage = "Incorrect data received from server: values are out of range"
        )
    }
    if (ventilationSetVals.ventilationSwitchOffTemp >= ventilationSetVals.ventilationSwitchOnTemp) {
        return SetValsSanityCheckResult.Error(
            errorMessage = "Incorrect data received from server: switch off value must be smaller than switch on value"
        )
    }
    return SetValsSanityCheckResult.Success(data = Unit)
}