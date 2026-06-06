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

package com.laimburggasse.aquariumcontrol.heating.domain

import com.laimburggasse.aquariumcontrol.common.domain.GlobalConstants
import com.laimburggasse.aquariumcontrol.common.domain.model.SetValsSanityCheckResult
import com.laimburggasse.aquariumcontrol.heating.domain.model.HeatingSetVals

/**
 * Performs a sanity check on heating setpoint values.
 * Verifies that temperatures are within the allowed range (20-30°C) and that
 * the switch-off temperature is strictly greater than the switch-on temperature.
 *
 * @param heatingSetVals The setpoints to validate.
 * @return A [SetValsSanityCheckResult] indicating success or the specific error type.
 */
fun heatingSetValsSanityCheck(heatingSetVals: HeatingSetVals):
        SetValsSanityCheckResult<Unit> {
    val heatingSetValInterval: ClosedFloatingPointRange<Double> = 20.0..30.0

    if (heatingSetVals.heatingSwitchOffTemp !in heatingSetValInterval ||
        heatingSetVals.heatingSwitchOnTemp !in heatingSetValInterval
    ) {
        return SetValsSanityCheckResult.Error(
            errorMessage = GlobalConstants.ERR_MSG_RECEIVED_SET_VALS_OUT_OF_RANGE
        )
    }
    if (heatingSetVals.heatingSwitchOffTemp <= heatingSetVals.heatingSwitchOnTemp) {
        return SetValsSanityCheckResult.Error(
            errorMessage = GlobalConstants.ERR_MSG_HEATING_INCORRECT_SET_VALS
        )
    }
    return SetValsSanityCheckResult.Success(data = Unit)
}