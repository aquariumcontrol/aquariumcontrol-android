package com.laimburggasse.aquariumcontrol.common.util

import kotlinx.datetime.*

object DateTimeUtil {
    fun parseApiTimestamp(timestamp: String): Long {
        return try {
            // yyyy-MM-dd HH:mm:ss
            val iso = timestamp.replace(" ", "T")
            LocalDateTime.parse(iso).toInstant(TimeZone.currentSystemDefault()).toEpochMilliseconds()
        } catch (e: Exception) {
            0L
        }
    }

    fun formatToApiTimestamp(epochMillis: Long): String {
        val instant = Instant.fromEpochMilliseconds(epochMillis)
        val localDateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())
        return localDateTime.toString().replace("T", " ").substringBefore(".")
    }
}
