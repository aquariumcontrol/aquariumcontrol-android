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

package com.laimburggasse.aquariumcontrol.balling.domain.model

import androidx.room.Entity
import com.laimburggasse.aquariumcontrol.common.data.remote.WebApiParams
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import androidx.room.PrimaryKey
import com.laimburggasse.aquariumcontrol.common.domain.BaseHistoryLogEntry

/**
 * Room Entity and Domain model representing a single Balling dosing event.
 * Stored in the "BALLING_LOG" table.
 *
 * @property timestamp The primary key, representing the time of the event in epoch milliseconds.
 * @property pumpId The identifier of the pump that performed the dosing.
 * @property volume The volume of fluid dosed, in milliliters.
 * @property label A descriptive label for the fluid (e.g., "Calcium").
 */
@Entity(tableName = "BALLING_LOG")
@Serializable
data class BallingHistoryLogEntry(
    @PrimaryKey
    @SerialName(value = WebApiParams.TIMESTAMP)
    override val timestamp: Long,
    @SerialName(value = WebApiParams.PUMP_ID)
    val pumpId: Int,
    @SerialName(value = WebApiParams.DOSING_VOLUME)
    val volume: Double,
    @SerialName(value = WebApiParams.LABEL)
    val label: String
) : BaseHistoryLogEntry