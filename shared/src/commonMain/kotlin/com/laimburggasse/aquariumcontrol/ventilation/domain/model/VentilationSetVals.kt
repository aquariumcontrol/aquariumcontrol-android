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

package com.laimburggasse.aquariumcontrol.ventilation.domain.model

import com.laimburggasse.aquariumcontrol.common.data.remote.WebApiParams
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Domain model representing the temperature thresholds for surface ventilation.
 *
 * @property ventilationSwitchOffTemp The temperature at which ventilation should turn OFF.
 * @property ventilationSwitchOnTemp The temperature at which ventilation should turn ON.
 */
@Serializable
data class VentilationSetVals(
    @SerialName(WebApiParams.VENTILATION_SWITCH_OFF_TEMPERATURE)
    var ventilationSwitchOffTemp: Double,

    @SerialName(WebApiParams.VENTILATION_SWITCH_ON_TEMPERATURE)
    var ventilationSwitchOnTemp: Double
)