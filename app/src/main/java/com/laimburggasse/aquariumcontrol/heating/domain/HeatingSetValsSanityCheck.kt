package com.laimburggasse.aquariumcontrol.heating.domain

import com.laimburggasse.aquariumcontrol.GlobalConstants
import com.laimburggasse.aquariumcontrol.lib_model.SetValsSanityCheckResult
import com.laimburggasse.aquariumcontrol.heating.domain.model.HeatingSetVals

fun heatingSetValsSanityCheck(heatingSetVals: HeatingSetVals):
        SetValsSanityCheckResult<Unit> {
    val heatingSetValInterval: ClosedFloatingPointRange<Double> = 20.0..30.0

    if (heatingSetVals.heatingSwitchOffTemp !in heatingSetValInterval ||
        heatingSetVals.heatingSwitchOnTemp !in heatingSetValInterval
    ) {
        return SetValsSanityCheckResult.Error(
            errorMessage = GlobalConstants.ERR_MSG_RECEIVED_SET_VALS_OUT_OF_RANGE
        )
    }
    if (heatingSetVals.heatingSwitchOffTemp <= heatingSetVals.heatingSwitchOnTemp) {
        return SetValsSanityCheckResult.Error(
            errorMessage = GlobalConstants.ERR_MSG_HEATING_INCORRECT_SET_VALS
        )
    }
    return SetValsSanityCheckResult.Success(data = Unit)
}