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

package com.laimburggasse.aquariumcontrol.feed.domain.model

import android.annotation.SuppressLint
import com.laimburggasse.aquariumcontrol.common.data.remote.WebApiParams

data class FeedProfilePhase(
    var duration: Double = 0.0,
    var skimmer: Boolean = false,
    var mainPump1: Boolean = false,
    var mainPump2: Boolean = false,
    var auxPump1: Boolean = false,
    var auxPump2: Boolean = false,
    var isPause: Boolean = false,
    var idx: Int = 0
) {
    private fun getPostParameterNameBase(idx: Int): String {
        @SuppressLint("DefaultLocale") val formattedIdx: String = String.format("%02d", idx + 1)
        return if (isPause) {
            "pause$formattedIdx"
        } else {
            "feed$formattedIdx"
        }
    }

    fun getFeedProfilePhaseApiCallParameters(idx: Int): Map<String, String> {
        val durationMillis: Int = (duration * 1000.0).toInt()
        return mapOf(
            getPostParameterNameBase(idx)+ WebApiParams.DURATION to (durationMillis).toString(),
            getPostParameterNameBase(idx)+ WebApiParams.SKIMMER to (if (skimmer) 1 else 0).toString(),
            getPostParameterNameBase(idx)+ WebApiParams.MAIN_PUMP1 to (if (mainPump1) 1 else 0).toString(),
            getPostParameterNameBase(idx)+ WebApiParams.MAIN_PUMP2 to (if (mainPump2) 1 else 0).toString(),
            getPostParameterNameBase(idx)+ WebApiParams.AUX_PUMP1 to (if (auxPump1) 1 else 0).toString(),
            getPostParameterNameBase(idx)+ WebApiParams.AUX_PUMP2 to (if (auxPump2) 1 else 0).toString(),
        )
    }

}