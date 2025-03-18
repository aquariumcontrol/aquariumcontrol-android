package com.laimburggasse.aquariumcontrol.heating

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class HeatingSetVals(
    @SerialName("heatingSwitchOffTemp")
    var heatingSwitchOffTemp: Double,

    @SerialName("heatingSwitchOnTemp")
    var heatingSwitchOnTemp: Double
)