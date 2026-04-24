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

package com.laimburggasse.aquariumcontrol.schedule.testing

import com.laimburggasse.aquariumcontrol.schedule.data.remote.ScheduleApiService
import com.laimburggasse.aquariumcontrol.schedule.data.remote.dto.ScheduleEntry
import com.laimburggasse.aquariumcontrol.schedule.data.remote.dto.ScheduleStringEntry
import okhttp3.ResponseBody
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.Response
import kotlin.collections.indexOfFirst

class MockScheduleApiServiceImpl(
    private var mockScheduleStringEntries: List<ScheduleStringEntry>?,
    private var mockScheduleEntries: List<ScheduleEntry>? = mockScheduleStringEntries?.map {
        it.transformToSetVal()
    }
): ScheduleApiService {
    override suspend fun getScheduleData(
        url: String,
        authHeader: String
    ): Response<List<ScheduleStringEntry>> {
        return if (mockScheduleStringEntries == null) {
            Response.error(500, "API ERROR".toResponseBody())
        }
        else {
            Response.success(mockScheduleStringEntries)
        }
    }

    fun updateMockScheduleData(mockScheduleEntry: ScheduleEntry) {
        mockScheduleEntries = mockScheduleEntries?.toMutableList()?.apply {
            val index = indexOfFirst { it.scheduleType == mockScheduleEntry.scheduleType }
            if (index != -1) {
                set(index, mockScheduleEntry)
            }
        }
        mockScheduleStringEntries = mockScheduleEntries?.map {
            it.toScheduleStringEntry()
        }
    }

    override suspend fun updateScheduleData(
        url: String,
        authHeader: String,
        params: Map<String, String>
    ): Response<ResponseBody> {
        return if (mockScheduleEntries == null) {
            Response.error(500, "API ERROR".toResponseBody())
        }
        else {
            Response.success("OK".toResponseBody())
        }
    }
}