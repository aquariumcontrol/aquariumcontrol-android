package com.laimburggasse.aquariumcontrol.common.util

import kotlinx.datetime.*
import kotlin.time.Instant

/**
 * Common utility for parsing and formatting timestamps used by the Aquarium controller.
 */
object DateTimeUtil {
    /** Cached system timezone for performance on iOS. */
    private val systemTz = TimeZone.currentSystemDefault()

    /**
     * Parses a string timestamp from the API into epoch milliseconds.
     * Supports "yyyy-MM-dd HH:mm:ss" and "yyyy-MM-dd" formats.
     *
     * @param timestamp The raw string from the server.
     * @return The epoch milliseconds, or 0L if parsing fails.
     */
    fun parseApiTimestamp(timestamp: String): Long {
        if (timestamp.isEmpty()) return 0L
        return try {
            val iso = timestamp.replace(" ", "T")
            if (iso.contains("T")) {
                LocalDateTime.parse(iso).toInstant(systemTz).toEpochMilliseconds()
            } else {
                // Try as LocalDate (yyyy-MM-dd or yyyy-M-d)
                val parts = iso.split("-")
                if (parts.size == 3) {
                    val year = parts[0].toInt()
                    val month = parts[1].toInt()
                    val day = parts[2].toInt()
                    LocalDate(year, month, day).atStartOfDayIn(systemTz).toEpochMilliseconds()
                } else {
                    0L
                }
            }
        } catch (e: Exception) {
            0L
        }
    }

    /**
     * Formats epoch milliseconds into the string format expected by the API.
     *
     * @param epochMillis The timestamp in milliseconds.
     * @return A string formatted as "yyyy-MM-dd HH:mm:ss".
     */
    fun formatToApiTimestamp(epochMillis: Long): String {
        val instant = Instant.fromEpochMilliseconds(epochMillis)
        val localDateTime = instant.toLocalDateTime(systemTz)
        return localDateTime.toString().replace("T", " ").substringBefore(".")
    }
}
