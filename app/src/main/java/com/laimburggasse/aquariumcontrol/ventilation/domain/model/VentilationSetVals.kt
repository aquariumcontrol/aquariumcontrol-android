package com.laimburggasse.aquariumcontrol.ventilation.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class VentilationSetVals(
    @SerialName("ventilationSwitchOffTemp")
    var ventilationSwitchOffTemp: Double,

    @SerialName("ventilationSwitchOnTemp")
    var ventilationSwitchOnTemp: Double
)