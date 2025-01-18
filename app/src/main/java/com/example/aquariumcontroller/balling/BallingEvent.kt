package com.example.aquariumcontroller.balling

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BallingEvent(
    @SerialName("Timestamp")
    val timestamp: String,
    @SerialName("pumpid")
    val pumpId: Int,
    @SerialName("dosingvolume")
    val volume: Double,
    @SerialName("label")
    val label: String
)
