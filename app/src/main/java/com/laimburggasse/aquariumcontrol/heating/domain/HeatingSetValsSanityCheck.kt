package com.laimburggasse.aquariumcontrol.heating.domain

import com.laimburggasse.aquariumcontrol.lib_model.SetValsSanityCheckResult
import com.laimburggasse.aquariumcontrol.heating.domain.model.HeatingSetVals

fun heatingSetValsSanityCheck(heatingSetVals: HeatingSetVals):
        SetValsSanityCheckResult<Unit> {
    val heatingSetValInterval: ClosedFloatingPointRange<Double> = 20.0..30.0

    if (heatingSetVals.heatingSwitchOffTemp !in heatingSetValInterval ||
        heatingSetVals.heatingSwitchOnTemp !in heatingSetValInterval
    ) {
        return SetValsSanityCheckResult.Error(
            errorMessage = "Incorrect data received from server: values are out of range"
        )
    }
    if (heatingSetVals.heatingSwitchOffTemp <= heatingSetVals.heatingSwitchOnTemp) {
        return SetValsSanityCheckResult.Error(
            errorMessage = "Incorrect data received from server: switch off value must be smaller than switch on value"
        )
    }
    return SetValsSanityCheckResult.Success(data = Unit)
}