package com.laimburggasse.aquariumcontrol.balling.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BallingProfile(
    // This class represents a Balling dosing profile.
    @SerialName(value = "pumpid")
    val pumpId: Int,
    @SerialName(value = "dosingvolume")
    var volume: Double,
    @SerialName(value = "label")
    val label: String
)