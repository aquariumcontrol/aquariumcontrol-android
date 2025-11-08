package com.laimburggasse.aquariumcontrol.heating.domain

import com.laimburggasse.aquariumcontrol.heating.domain.model.HeatingSetVals
import com.laimburggasse.aquariumcontrol.heating.presentation.HeatingSetValsUiState

fun heatingSetValsSanityCheck(heatingSetVals: HeatingSetVals): HeatingSetValsUiState {
    val heatingSetValInterval: ClosedFloatingPointRange<Double> = 20.0..30.0

    if (heatingSetVals.heatingSwitchOffTemp !in heatingSetValInterval ||
        heatingSetVals.heatingSwitchOnTemp !in heatingSetValInterval
    ) {
        HeatingSetValsUiState.Error(
            errorMessage = "Incorrect data received from server: values are out of range"
        )
    }
    if (heatingSetVals.heatingSwitchOffTemp <= heatingSetVals.heatingSwitchOnTemp) {
        HeatingSetValsUiState.Error(
            errorMessage = "Incorrect data received from server: switch off value must be greater than switch on value"
        )
    }
    return HeatingSetValsUiState.Success(heatingSetVals)
}