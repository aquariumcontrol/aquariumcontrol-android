package com.example.aquariumcontroller.refill

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.annotation.VisibleForTesting
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.json.JSONException
import org.json.JSONObject
import java.sql.Timestamp
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime

@Serializable
data class RefillEvent(
    @SerialName("Timestamp")
    val timestamp: String,
    @SerialName("Duration")
    val duration: Double,
    @SerialName("Volume")
    val volume: Double
) {
    @RequiresApi(Build.VERSION_CODES.O)
    fun toRefillEvent(): RefillEvent {
        val fixedDateTime = LocalDateTime.parse(timestamp.replace(" ", "T"))
        val zonedDateTime = ZonedDateTime.of(fixedDateTime, ZoneId.systemDefault())
        val fixedTimestamp = Timestamp.from(zonedDateTime.toInstant())
        return RefillEvent(fixedTimestamp.toString(), duration, volume)
    }

    constructor(timestamp: Timestamp, duration: Double, volume: Double) : this(
        timestamp.toString(),
        duration,
        volume
    )
}
