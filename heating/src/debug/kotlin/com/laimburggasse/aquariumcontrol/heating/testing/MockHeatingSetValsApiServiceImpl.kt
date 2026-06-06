/*
 *  Copyright 2026 Uwe Martin
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.laimburggasse.aquariumcontrol.heating.testing

import com.laimburggasse.aquariumcontrol.heating.data.remote.HeatingSetValsApiService
import com.laimburggasse.aquariumcontrol.heating.domain.model.HeatingSetVals
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

class MockHeatingSetValsApiServiceImpl(
    private var mockHeatingSetVals: HeatingSetVals?
): HeatingSetValsApiService
{
    private var forceError: Boolean = false

    fun setForceError(force: Boolean) {
        this.forceError = force
    }

    override suspend fun getHeatingSetVals(
        url: String,
        authHeader: String
    ): List<HeatingSetVals> {
        delay(10.milliseconds)
        if (mockHeatingSetVals == null || forceError) {
            throw Exception("API error")
        }
        else {
            return listOf(mockHeatingSetVals!!.copy())
        }
    }

    fun updateMockHeatingSetVals(newHeatingSetVals: HeatingSetVals) {
        mockHeatingSetVals = newHeatingSetVals
    }

    override suspend fun updateHeatingSetVals(
        url: String,
        authHeader: String,
        params: Map<String, String>
    ): Boolean {
        return !forceError
    }
}