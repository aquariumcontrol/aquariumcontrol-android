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

package com.laimburggasse.aquariumcontrol.feed.testing

import com.google.firebase.Firebase
import com.google.firebase.crashlytics.crashlytics
import com.laimburggasse.aquariumcontrol.common.data.remote.WebApiParams
import com.laimburggasse.aquariumcontrol.feed.data.remote.dto.FeedProfileImport
import com.laimburggasse.aquariumcontrol.feed.domain.model.FeedProfile


fun FeedProfile.toFeedProfileImport(): FeedProfileImport {
    val import = FeedProfileImport()
    import.profileId = this.profileID
    import.profileName = this.profileName

    this.feedProfilePhases.forEachIndexed { index, phase ->
        val isPause: Boolean = index % 2 == 0
        val phaseNum: Int = (index / 2) + 1
        val prefix: String = if (isPause) "pause%02d".format(phaseNum) else "feed%02d".format(phaseNum)

        fun setField(suffix: String, value: Any?) {
            try {
                val fieldName = "$prefix$suffix"
                FeedProfileImport::class.java.getDeclaredField(fieldName).apply {
                    isAccessible = true
                    set(import, value)
                }
            } catch (e: Exception) {
                Firebase.crashlytics.recordException(e)
            }
        }

        setField(suffix = WebApiParams.DURATION, value = (phase.duration * 1000).toInt())
        setField(suffix = WebApiParams.SKIMMER, value = if (phase.skimmer) 1 else 0)
        setField(suffix = WebApiParams.MAIN_PUMP1, value = if (phase.mainPump1) 1 else 0)
        setField(suffix = WebApiParams.MAIN_PUMP2, value = if (phase.mainPump2) 1 else 0)
        setField(suffix = WebApiParams.AUX_PUMP1, value = if (phase.auxPump1) 1 else 0)
        setField(suffix = WebApiParams.AUX_PUMP2, value = if (phase.auxPump2) 1 else 0)
    }
    return import
}
