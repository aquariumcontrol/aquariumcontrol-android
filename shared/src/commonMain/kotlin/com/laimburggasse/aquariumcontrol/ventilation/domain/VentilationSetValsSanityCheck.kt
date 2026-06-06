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

package com.laimburggasse.aquariumcontrol.ventilation.domain

import com.laimburggasse.aquariumcontrol.common.domain.GlobalConstants
import com.laimburggasse.aquariumcontrol.common.domain.model.SetValsSanityCheckResult
import com.laimburggasse.aquariumcontrol.ventilation.domain.model.VentilationSetVals

/**
 * Performs a sanity check on ventilation setpoint values.
 * Verifies that temperatures are within the allowed range (20-30°C) and that
 * the switch-on temperature is strictly greater than the switch-off temperature.
 *
 * @param ventilationSetVals The setpoints to validate.
 * @return A [SetValsSanityCheckResult] indicating success or the specific error type.
 */
fun ventilationSetValsSanityCheck(ventilationSetVals: VentilationSetVals):
        SetValsSanityCheckResult<Unit> {
    val ventilationSetValInterval: ClosedFloatingPointRange<Double> = 20.0..30.0

    if (ventilationSetVals.ventilationSwitchOffTemp !in ventilationSetValInterval ||
        ventilationSetVals.ventilationSwitchOnTemp !in ventilationSetValInterval
    ) {
        return SetValsSanityCheckResult.Error(
            errorMessage = GlobalConstants.ERR_MSG_RECEIVED_SET_VALS_OUT_OF_RANGE
        )
    }
    if (ventilationSetVals.ventilationSwitchOffTemp >= ventilationSetVals.ventilationSwitchOnTemp) {
        return SetValsSanityCheckResult.Error(
            errorMessage = GlobalConstants.ERR_MSG_VENTILATION_INCORRECT_SET_VALS
        )
    }
    return SetValsSanityCheckResult.Success(data = Unit)
}