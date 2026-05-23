package com.laimburggasse.aquariumcontrol.refill.data.remote.dto

import com.laimburggasse.aquariumcontrol.common.data.remote.WebApiParams
import com.laimburggasse.aquariumcontrol.refill.domain.model.RefillHistoryLogEntry
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.text.SimpleDateFormat
import java.util.Locale

@Serializable
data class RefillHistoryLogEntryDto(
    @SerialName(value = WebApiParams.TIMESTAMP)
    val timestamp: String,
    @SerialName(value = WebApiParams.REFILL_DURATION)
    val duration: Double,
    @SerialName(value = WebApiParams.REFILL_VOLUME)
    val volume: Double
) {
    fun toRefillHistoryLogEntry(): RefillHistoryLogEntry {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)
        val timestampMillis = try {
            dateFormat.parse(timestamp)?.time ?: 0L
        } catch (e: Exception) {
            0L
        }
        return RefillHistoryLogEntry(
            timestamp = timestampMillis,
            duration = duration,
            volume = volume
        )
    }
}
