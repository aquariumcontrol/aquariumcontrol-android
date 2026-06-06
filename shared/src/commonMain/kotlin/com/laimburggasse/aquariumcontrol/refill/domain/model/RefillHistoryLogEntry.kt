/*
 * Copyright 2025 Uwe Martin
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.laimburggasse.aquariumcontrol.refill.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.laimburggasse.aquariumcontrol.common.data.remote.WebApiParams
import com.laimburggasse.aquariumcontrol.common.domain.BaseHistoryLogEntry
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Room Entity and Domain model representing a single water refill event.
 *
 * @property timestamp The primary key, representing the time of the event in epoch milliseconds.
 * @property duration The duration of the refill event in seconds.
 * @property volume The volume of water added during the refill event in liters.
 */
@Entity(tableName = "REFILL_LOG")
@Serializable
data class RefillHistoryLogEntry(
    // This data class represents a refill event.
    @PrimaryKey
    @SerialName(value = WebApiParams.TIMESTAMP)
    override val timestamp: Long,
    @SerialName(value = WebApiParams.REFILL_DURATION)
    val duration: Double,
    @SerialName(value = WebApiParams.REFILL_VOLUME)
    val volume: Double
) : BaseHistoryLogEntry