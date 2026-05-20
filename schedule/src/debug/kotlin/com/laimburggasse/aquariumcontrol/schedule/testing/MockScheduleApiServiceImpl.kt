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
import kotlinx.coroutines.delay

class MockScheduleApiServiceImpl(
    private var mockScheduleStringEntries: List<ScheduleStringEntry>?,
    private var mockScheduleEntries: List<ScheduleEntry>? = mockScheduleStringEntries?.map {
        it.transformToSetVal()
    }
): ScheduleApiService {
    private var forceError: Boolean = false

    fun setMockScheduleStringEntries(entries: List<ScheduleStringEntry>?) {
        this.mockScheduleStringEntries = entries
    }

    override suspend fun getScheduleData(
        url: String,
        authHeader: String
    ): List<ScheduleStringEntry> {
        delay(10) // Small virtual delay to avoid conflation in flow
        return if (mockScheduleStringEntries == null || forceError) {
            throw Exception("API ERROR")
        }
        else {
            mockScheduleStringEntries!!
        }
    }

    override suspend fun updateScheduleData(
        url: String,
        authHeader: String,
        params: Map<String, String>
    ): Boolean {
        return mockScheduleEntries != null
    }
}