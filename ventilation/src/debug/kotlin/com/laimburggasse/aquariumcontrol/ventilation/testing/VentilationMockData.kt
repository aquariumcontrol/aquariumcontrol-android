package com.laimburggasse.aquariumcontrol.ventilation.testing

import com.laimburggasse.aquariumcontrol.ventilation.domain.model.VentilationSetVals
import com.laimburggasse.aquariumcontrol.ventilation.presentation.VentilationSetValsUiState

object VentilationMockData {
    val mockVentilationSetValsUiStateSuccess = VentilationSetValsUiState.Success(
        ventilationSetVals = VentilationSetVals(
            ventilationSwitchOffTemp = 23.5,
            ventilationSwitchOnTemp = 27.5
        )
    )
    val mockVentilationSetValsUiStateError = VentilationSetValsUiState.Error(
        errorMessage = "Mock Error loading data"
    )
}