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

package com.laimburggasse.aquariumcontrol.ventilation.testing

import com.laimburggasse.aquariumcontrol.ventilation.data.remote.VentilationSetValsApiService
import com.laimburggasse.aquariumcontrol.ventilation.domain.model.VentilationSetVals
import okhttp3.ResponseBody
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.Response

class MockVentilationSetValsApiServiceImpl(
    private var mockVentilationSetVals: VentilationSetVals?
): VentilationSetValsApiService
{
    override suspend fun getVentilationSetVals(
        url: String,
        authHeader: String
    ): Response<List<VentilationSetVals>> {
        return if (mockVentilationSetVals == null) {
            Response.error(500, "API error".toResponseBody())
        }
        else {
            val outputList = listOf(mockVentilationSetVals!!.copy())
            Response.success(outputList)
        }
    }

    fun updateMockVentilationSetVals(newVentilationSetVals: VentilationSetVals) {
        mockVentilationSetVals = newVentilationSetVals
    }

    override suspend fun updateVentilationSetVals(
        url: String,
        authHeader: String,
        params: Map<String, String>
    ): Response<ResponseBody> {
        return Response.success("OK".toResponseBody())
    }
}