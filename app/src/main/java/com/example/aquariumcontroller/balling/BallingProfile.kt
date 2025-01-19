package com.example.aquariumcontroller.balling

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BallingProfile(
    @SerialName("pumpid")
    val pumpId: Int,
    @SerialName("dosingvolume")
    var volume: Double,
    @SerialName("label")
    val label: String
)
