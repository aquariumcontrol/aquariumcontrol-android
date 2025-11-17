package com.laimburggasse.aquariumcontrol.heating.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class HeatingSetVals(
    @SerialName(value = "heatingSwitchOnTemp")
    var heatingSwitchOnTemp: Double,

    @SerialName(value = "heatingSwitchOffTemp")
    var heatingSwitchOffTemp: Double
)