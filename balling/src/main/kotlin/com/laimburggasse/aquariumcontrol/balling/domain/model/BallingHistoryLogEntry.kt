package com.laimburggasse.aquariumcontrol.balling.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BallingHistoryLogEntry(
    // This data class represents a Balling dosing event.
    @SerialName(value = "Timestamp")
    val timestamp: String,
    @SerialName(value = "pumpid")
    val pumpId: Int,
    @SerialName(value = "dosingvolume")
    val volume: Double,
    @SerialName(value = "label")
    val label: String
)