package com.example.aquariumcontroller.refill

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.sql.Timestamp

@Serializable
data class RefillEvent(
    @SerialName("Timestamp")
    val timestamp: String,
    @SerialName("Duration")
    val duration: Double,
    @SerialName("Volume")
    val volume: Double
) {

    constructor(timestamp: Timestamp, duration: Double, volume: Double) : this(
        timestamp.toString(),
        duration,
        volume
    )
}
