package com.laimburggasse.aquariumcontrol.common.util

import kotlinx.datetime.*

object DateTimeUtil {
    fun parseApiTimestamp(timestamp: String): Long {
        if (timestamp.isEmpty()) return 0L
        return try {
            val iso = timestamp.replace(" ", "T")
            if (iso.contains("T")) {
                LocalDateTime.parse(iso).toInstant(TimeZone.currentSystemDefault()).toEpochMilliseconds()
            } else {
                // Try as LocalDate (yyyy-MM-dd or yyyy-M-d)
                val parts = iso.split("-")
                if (parts.size == 3) {
                    val year = parts[0].toInt()
                    val month = parts[1].toInt()
                    val day = parts[2].toInt()
                    LocalDate(year, month, day).atStartOfDayIn(TimeZone.currentSystemDefault()).toEpochMilliseconds()
                } else {
                    0L
                }
            }
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
