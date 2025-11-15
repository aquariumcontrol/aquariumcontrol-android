package com.laimburggasse.aquariumcontrol.ventilation.domain

import com.laimburggasse.aquariumcontrol.GlobalConstants
import com.laimburggasse.aquariumcontrol.lib_composables.domain.model.SetValsSanityCheckResult
import com.laimburggasse.aquariumcontrol.ventilation.domain.model.VentilationSetVals

fun ventilationSetValsSanityCheck(ventilationSetVals: VentilationSetVals):
        SetValsSanityCheckResult<Unit> {
    val ventilationSetValInterval: ClosedFloatingPointRange<Double> = 20.0..30.0

    if (ventilationSetVals.ventilationSwitchOffTemp !in ventilationSetValInterval ||
        ventilationSetVals.ventilationSwitchOnTemp !in ventilationSetValInterval
    ) {
        return SetValsSanityCheckResult.Error(
            errorMessage = GlobalConstants.ERR_MSG_RECEIVED_SET_VALS_OUT_OF_RANGE
        )
    }
    if (ventilationSetVals.ventilationSwitchOffTemp >= ventilationSetVals.ventilationSwitchOnTemp) {
        return SetValsSanityCheckResult.Error(
            errorMessage = GlobalConstants.ERR_MSG_VENTILATION_INCORRECT_SET_VALS
        )
    }
    return SetValsSanityCheckResult.Success(data = Unit)
}