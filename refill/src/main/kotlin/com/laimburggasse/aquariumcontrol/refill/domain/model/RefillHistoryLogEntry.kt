package com.laimburggasse.aquariumcontrol.refill.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.sql.Timestamp

@Serializable
data class RefillHistoryLogEntry(
    @SerialName(value = "Timestamp")
    val timestamp: String,
    @SerialName(value = "Duration")
    val duration: Double,
    @SerialName(value = "Volume")
    val volume: Double
) {
    // This class is used to represent a refill event.
    constructor(timestamp: Timestamp, duration: Double, volume: Double) : this(
        timestamp.toString(),
        duration,
        volume
    )
}