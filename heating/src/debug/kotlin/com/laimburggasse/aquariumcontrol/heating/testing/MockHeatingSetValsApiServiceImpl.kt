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
import okhttp3.ResponseBody
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.Response

class MockHeatingSetValsApiServiceImpl(
    private var mockHeatingSetVals: HeatingSetVals?
): HeatingSetValsApiService
{
    override suspend fun getHeatingSetVals(
        url: String,
        authHeader: String
    ): Response<List<HeatingSetVals>> {
        return if (mockHeatingSetVals == null) {
            Response.error(500, "API error".toResponseBody())
        }
        else {
            val outputList = listOf(mockHeatingSetVals!!.copy())
            Response.success(outputList)
        }
    }

    fun updateMockHeatingSetVals(newHeatingSetVals: HeatingSetVals) {
        mockHeatingSetVals = newHeatingSetVals
    }

    override suspend fun updateHeatingSetVals(
        url: String,
        authHeader: String,
        params: Map<String, String>
    ): Response<ResponseBody> {
        return Response.success("OK".toResponseBody())
    }
}