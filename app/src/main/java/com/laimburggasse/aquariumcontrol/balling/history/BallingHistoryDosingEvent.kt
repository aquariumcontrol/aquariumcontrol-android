package com.laimburggasse.aquariumcontrol.balling.history

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BallingHistoryDosingEvent(
    // This data class represents a Balling dosing event.
    @SerialName("Timestamp")
    val timestamp: String,
    @SerialName("pumpid")
    val pumpId: Int,
    @SerialName("dosingvolume")
    val volume: Double,
    @SerialName("label")
    val label: String
)
