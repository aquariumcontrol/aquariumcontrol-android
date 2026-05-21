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

import com.google.firebase.Firebase
import com.google.firebase.crashlytics.crashlytics
import com.laimburggasse.aquariumcontrol.common.data.remote.WebApiParams
import com.laimburggasse.aquariumcontrol.feed.data.remote.dto.FeedProfileImport

object FeedProfileMapper {
    fun fromFeedProfileImportUpdateIdName(
        feedProfileImport: FeedProfileImport,
        profileId: Int,
        profileName: String
    ): FeedProfile {
        feedProfileImport.profileId = profileId
        feedProfileImport.profileName = profileName
        return fromFeedProfileImport(feedProfileImport)
    }

    fun fromFeedProfileImport(feedProfileImport: FeedProfileImport): FeedProfile {
        feedProfileImport.logContent()
        val phases: List<FeedProfilePhase> = List(size = 20) { index ->
            val isPause: Boolean = index % 2 == 0
            val phaseNum: Int = (index / 2) + 1 // e.g., 0,1 -> 1; 2,3 -> 2
            val importPrefix: String = if (isPause) "pause%02d".format(phaseNum) else "feed%02d".format(phaseNum)

            fun getImportPropertyInt(name: String, default: Int): Int {
                return try {
                    feedProfileImport::class.java.getDeclaredField(name).let { field ->
                        field.isAccessible = true
                        (field.get(feedProfileImport) as? Int) ?: default
                    }
                } catch (e: Exception) { // recording exception in crashlytics
                    Firebase.crashlytics.recordException(e)
                    default
                }
            }

            fun getImportBoolProperty(name: String): Boolean {
                return try {
                    feedProfileImport::class.java.getDeclaredField(name).let { field ->
                        field.isAccessible = true
                        (field.get(feedProfileImport) as Boolean)
                    }
                } catch (_: Exception) { // not recording exception in crashlytics
                    false
                }
            }

            FeedProfilePhase(
                duration = getImportPropertyInt(name = "${importPrefix}${WebApiParams.DURATION}", default = 0).toDouble() / 1000.0,
                skimmer = getImportBoolProperty(name = "${importPrefix}${WebApiParams.SKIMMER}"),
                mainPump1 = getImportBoolProperty(name = "${importPrefix}${WebApiParams.MAIN_PUMP1}"),
                mainPump2 = getImportBoolProperty(name = "${importPrefix}${WebApiParams.MAIN_PUMP2}"),
                auxPump1 = getImportBoolProperty(name = "${importPrefix}${WebApiParams.AUX_PUMP1}"),
                auxPump2 = getImportBoolProperty(name = "${importPrefix}${WebApiParams.AUX_PUMP2}"),
                isPause = isPause,
                idx = index
            )
        }

        return FeedProfile(
            profileID = feedProfileImport.profileId,
            profileName = feedProfileImport.profileName,
            feedProfilePhases = phases
        )
    }
}
