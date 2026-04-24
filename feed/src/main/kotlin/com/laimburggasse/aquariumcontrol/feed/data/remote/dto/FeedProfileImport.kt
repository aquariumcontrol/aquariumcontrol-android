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

package com.laimburggasse.aquariumcontrol.feed.data.remote.dto

import com.laimburggasse.aquariumcontrol.common.data.remote.WebApiParams
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class FeedProfileImport {
    @SerialName(value = WebApiParams.PROFILE_ID)
    var profileId: Int = 0

    @SerialName(value = WebApiParams.PROFILE_NAME)
    var profileName: String = ""

    // Phase #1
    @SerialName(value = WebApiParams.PAUSE + "01" + WebApiParams.DURATION)
    var pause01Duration: Int? = 0

    @SerialName(value = WebApiParams.PAUSE + "01" + WebApiParams.SKIMMER)
    var pause01Skimmer: Int = 1
    @SerialName(value = WebApiParams.PAUSE + "01" + WebApiParams.MAIN_PUMP1)
    var pause01MainPump1: Int = 0
    @SerialName(value = WebApiParams.PAUSE + "01" + WebApiParams.MAIN_PUMP2)
    var pause01MainPump2: Int = 0
    @SerialName(value = WebApiParams.PAUSE + "01" + WebApiParams.AUX_PUMP1)
    var pause01AuxPump1: Int = 0
    @SerialName(value = WebApiParams.PAUSE + "01" + WebApiParams.AUX_PUMP2)
    var pause01AuxPump2: Int = 0

    @SerialName(value = WebApiParams.FEED + "01" + WebApiParams.DURATION)
    var feed01Duration: Int? = 0

    @SerialName(value = WebApiParams.FEED + "01" + WebApiParams.SKIMMER)
    var feed01Skimmer: Int = 1
    @SerialName(value = WebApiParams.FEED + "01" + WebApiParams.MAIN_PUMP1)
    var feed01MainPump1: Int = 0
    @SerialName(value = WebApiParams.FEED + "01" + WebApiParams.MAIN_PUMP2)
    var feed01MainPump2: Int = 0
    @SerialName(value = WebApiParams.FEED + "01" + WebApiParams.AUX_PUMP1)
    var feed01AuxPump1: Int = 0
    @SerialName(value = WebApiParams.FEED + "01" + WebApiParams.AUX_PUMP2)
    var feed01AuxPump2: Int = 0

    // Phase #2
    @SerialName(value = WebApiParams.PAUSE + "02" + WebApiParams.DURATION)
    var pause02Duration: Int? = 0

    @SerialName(value = WebApiParams.PAUSE + "02" + WebApiParams.SKIMMER)
    var pause02Skimmer: Int = 1
    @SerialName(value = WebApiParams.PAUSE + "02" + WebApiParams.MAIN_PUMP1)
    var pause02MainPump1: Int = 0
    @SerialName(value = WebApiParams.PAUSE + "02" + WebApiParams.MAIN_PUMP2)
    var pause02MainPump2: Int = 0
    @SerialName(value = WebApiParams.PAUSE + "02" + WebApiParams.AUX_PUMP1)
    var pause02AuxPump1: Int = 0
    @SerialName(value = WebApiParams.PAUSE + "02" + WebApiParams.AUX_PUMP2)
    var pause02AuxPump2: Int = 0

    @SerialName(value = WebApiParams.FEED + "02" + WebApiParams.DURATION)
    var feed02Duration: Int? = 0

    @SerialName(value = WebApiParams.FEED + "02" + WebApiParams.SKIMMER)
    var feed02Skimmer: Int = 1
    @SerialName(value = WebApiParams.FEED + "02" + WebApiParams.MAIN_PUMP1)
    var feed02MainPump1: Int = 0
    @SerialName(value = WebApiParams.FEED + "02" + WebApiParams.MAIN_PUMP2)
    var feed02MainPump2: Int = 0
    @SerialName(value = WebApiParams.FEED + "02" + WebApiParams.AUX_PUMP1)
    var feed02AuxPump1: Int = 0
    @SerialName(value = WebApiParams.FEED + "02" + WebApiParams.AUX_PUMP2)
    var feed02AuxPump2: Int = 0

    // Phase #3
    @SerialName(value = WebApiParams.PAUSE + "03" + WebApiParams.DURATION)
    var pause03Duration: Int? = 0

    @SerialName(value = WebApiParams.PAUSE + "03" + WebApiParams.SKIMMER)
    var pause03Skimmer: Int = 1
    @SerialName(value = WebApiParams.PAUSE + "03" + WebApiParams.MAIN_PUMP1)
    var pause03MainPump1: Int = 0
    @SerialName(value = WebApiParams.PAUSE + "03" + WebApiParams.MAIN_PUMP2)
    var pause03MainPump2: Int = 0
    @SerialName(value = WebApiParams.PAUSE + "03" + WebApiParams.AUX_PUMP1)
    var pause03AuxPump1: Int = 0
    @SerialName(value = WebApiParams.PAUSE + "03" + WebApiParams.AUX_PUMP2)
    var pause03AuxPump2: Int = 0

    @SerialName(value = WebApiParams.FEED + "03" + WebApiParams.DURATION)
    var feed03Duration: Int? = 0

    @SerialName(value = WebApiParams.FEED + "03" + WebApiParams.SKIMMER)
    var feed03Skimmer: Int = 1
    @SerialName(value = WebApiParams.FEED + "03" + WebApiParams.MAIN_PUMP1)
    var feed03MainPump1: Int = 0
    @SerialName(value = WebApiParams.FEED + "03" + WebApiParams.MAIN_PUMP2)
    var feed03MainPump2: Int = 0
    @SerialName(value = WebApiParams.FEED + "03" + WebApiParams.AUX_PUMP1)
    var feed03AuxPump1: Int = 0
    @SerialName(value = WebApiParams.FEED + "03" + WebApiParams.AUX_PUMP2)
    var feed03AuxPump2: Int = 0

    // Phase #4
    @SerialName(value = WebApiParams.PAUSE + "04" + WebApiParams.DURATION)
    var pause04Duration: Int? = 0

    @SerialName(value = WebApiParams.PAUSE + "04" + WebApiParams.SKIMMER)
    var pause04Skimmer: Int = 1
    @SerialName(value = WebApiParams.PAUSE + "04" + WebApiParams.MAIN_PUMP1)
    var pause04MainPump1: Int = 0
    @SerialName(value = WebApiParams.PAUSE + "04" + WebApiParams.MAIN_PUMP2)
    var pause04MainPump2: Int = 0
    @SerialName(value = WebApiParams.PAUSE + "04" + WebApiParams.AUX_PUMP1)
    var pause04AuxPump1: Int = 0
    @SerialName(value = WebApiParams.PAUSE + "04" + WebApiParams.AUX_PUMP2)
    var pause04AuxPump2: Int = 0

    @SerialName(value = WebApiParams.FEED + "04" + WebApiParams.DURATION)
    var feed04Duration: Int? = 0

    @SerialName(value = WebApiParams.FEED + "04" + WebApiParams.SKIMMER)
    var feed04Skimmer: Int = 1
    @SerialName(value = WebApiParams.FEED + "04" + WebApiParams.MAIN_PUMP1)
    var feed04MainPump1: Int = 0
    @SerialName(value = WebApiParams.FEED + "04" + WebApiParams.MAIN_PUMP2)
    var feed04MainPump2: Int = 0
    @SerialName(value = WebApiParams.FEED + "04" + WebApiParams.AUX_PUMP1)
    var feed04AuxPump1: Int = 0
    @SerialName(value = WebApiParams.FEED + "04" + WebApiParams.AUX_PUMP2)
    var feed04AuxPump2: Int = 0

    // Phase #5
    @SerialName(value = WebApiParams.PAUSE + "05" + WebApiParams.DURATION)
    var pause05Duration: Int? = 0

    @SerialName(value = WebApiParams.PAUSE + "05" + WebApiParams.SKIMMER)
    var pause05Skimmer: Int = 1
    @SerialName(value = WebApiParams.PAUSE + "05" + WebApiParams.MAIN_PUMP1)
    var pause05MainPump1: Int = 0
    @SerialName(value = WebApiParams.PAUSE + "05" + WebApiParams.MAIN_PUMP2)
    var pause05MainPump2: Int = 0
    @SerialName(value = WebApiParams.PAUSE + "05" + WebApiParams.AUX_PUMP1)
    var pause05AuxPump1: Int = 0
    @SerialName(value = WebApiParams.PAUSE + "05" + WebApiParams.AUX_PUMP2)
    var pause05AuxPump2: Int = 0

    @SerialName(value = WebApiParams.FEED + "05" + WebApiParams.DURATION)
    var feed05Duration: Int? = 0

    @SerialName(value = WebApiParams.FEED + "05" + WebApiParams.SKIMMER)
    var feed05Skimmer: Int = 1
    @SerialName(value = WebApiParams.FEED + "05" + WebApiParams.MAIN_PUMP1)
    var feed05MainPump1: Int = 0
    @SerialName(value = WebApiParams.FEED + "05" + WebApiParams.MAIN_PUMP2)
    var feed05MainPump2: Int = 0
    @SerialName(value = WebApiParams.FEED + "05" + WebApiParams.AUX_PUMP1)
    var feed05AuxPump1: Int = 0
    @SerialName(value = WebApiParams.FEED + "05" + WebApiParams.AUX_PUMP2)
    var feed05AuxPump2: Int = 0

    // Phase #6
    @SerialName(value = WebApiParams.PAUSE + "06" + WebApiParams.DURATION)
    var pause06Duration: Int? = 0

    @SerialName(value = WebApiParams.PAUSE + "06" + WebApiParams.SKIMMER)
    var pause06Skimmer: Int = 1
    @SerialName(value = WebApiParams.PAUSE + "06" + WebApiParams.MAIN_PUMP1)
    var pause06MainPump1: Int = 0
    @SerialName(value = WebApiParams.PAUSE + "06" + WebApiParams.MAIN_PUMP2)
    var pause06MainPump2: Int = 0
    @SerialName(value = WebApiParams.PAUSE + "06" + WebApiParams.AUX_PUMP1)
    var pause06AuxPump1: Int = 0
    @SerialName(value = WebApiParams.PAUSE + "06" + WebApiParams.AUX_PUMP2)
    var pause06AuxPump2: Int = 0

    @SerialName(value = WebApiParams.FEED + "06" + WebApiParams.DURATION)
    var feed06Duration: Int? = 0

    @SerialName(value = WebApiParams.FEED + "06" + WebApiParams.SKIMMER)
    var feed06Skimmer: Int = 1
    @SerialName(value = WebApiParams.FEED + "06" + WebApiParams.MAIN_PUMP1)
    var feed06MainPump1: Int = 0
    @SerialName(value = WebApiParams.FEED + "06" + WebApiParams.MAIN_PUMP2)
    var feed06MainPump2: Int = 0
    @SerialName(value = WebApiParams.FEED + "06" + WebApiParams.AUX_PUMP1)
    var feed06AuxPump1: Int = 0
    @SerialName(value = WebApiParams.FEED + "06" + WebApiParams.AUX_PUMP2)
    var feed06AuxPump2: Int = 0

    // Phase #7
    @SerialName(value = WebApiParams.PAUSE + "07" + WebApiParams.DURATION)
    var pause07Duration: Int? = 0

    @SerialName(value = WebApiParams.PAUSE + "07" + WebApiParams.SKIMMER)
    var pause07Skimmer: Int = 1
    @SerialName(value = WebApiParams.PAUSE + "07" + WebApiParams.MAIN_PUMP1)
    var pause07MainPump1: Int = 0
    @SerialName(value = WebApiParams.PAUSE + "07" + WebApiParams.MAIN_PUMP2)
    var pause07MainPump2: Int = 0
    @SerialName(value = WebApiParams.PAUSE + "07" + WebApiParams.AUX_PUMP1)
    var pause07AuxPump1: Int = 0
    @SerialName(value = WebApiParams.PAUSE + "07" + WebApiParams.AUX_PUMP2)
    var pause07AuxPump2: Int = 0

    @SerialName(value = WebApiParams.FEED + "07" + WebApiParams.DURATION)
    var feed07Duration: Int? = 0

    @SerialName(value = WebApiParams.FEED + "07" + WebApiParams.SKIMMER)
    var feed07Skimmer: Int = 1
    @SerialName(value = WebApiParams.FEED + "07" + WebApiParams.MAIN_PUMP1)
    var feed07MainPump1: Int = 0
    @SerialName(value = WebApiParams.FEED + "07" + WebApiParams.MAIN_PUMP2)
    var feed07MainPump2: Int = 0
    @SerialName(value = WebApiParams.FEED + "07" + WebApiParams.AUX_PUMP1)
    var feed07AuxPump1: Int = 0
    @SerialName(value = WebApiParams.FEED + "07" + WebApiParams.AUX_PUMP2)
    var feed07AuxPump2: Int = 0

    // Phase #8
    @SerialName(value = WebApiParams.PAUSE + "08" + WebApiParams.DURATION)
    var pause08Duration: Int? = 0

    @SerialName(value = WebApiParams.PAUSE + "08" + WebApiParams.SKIMMER)
    var pause08Skimmer: Int = 1
    @SerialName(value = WebApiParams.PAUSE + "08" + WebApiParams.MAIN_PUMP1)
    var pause08MainPump1: Int = 0
    @SerialName(value = WebApiParams.PAUSE + "08" + WebApiParams.MAIN_PUMP2)
    var pause08MainPump2: Int = 0
    @SerialName(value = WebApiParams.PAUSE + "08" + WebApiParams.AUX_PUMP1)
    var pause08AuxPump1: Int = 0
    @SerialName(value = WebApiParams.PAUSE + "08" + WebApiParams.AUX_PUMP2)
    var pause08AuxPump2: Int = 0

    @SerialName(value = WebApiParams.FEED + "08" + WebApiParams.DURATION)
    var feed08Duration: Int? = 0

    @SerialName(value = WebApiParams.FEED + "08" + WebApiParams.SKIMMER)
    var feed08Skimmer: Int = 1
    @SerialName(value = WebApiParams.FEED + "08" + WebApiParams.MAIN_PUMP1)
    var feed08MainPump1: Int = 0
    @SerialName(value = WebApiParams.FEED + "08" + WebApiParams.MAIN_PUMP2)
    var feed08MainPump2: Int = 0
    @SerialName(value = WebApiParams.FEED + "08" + WebApiParams.AUX_PUMP1)
    var feed08AuxPump1: Int = 0
    @SerialName(value = WebApiParams.FEED + "08" + WebApiParams.AUX_PUMP2)
    var feed08AuxPump2: Int = 0

    // Phase #9
    @SerialName(value = WebApiParams.PAUSE + "09" + WebApiParams.DURATION)
    var pause09Duration: Int? = 0

    @SerialName(value = WebApiParams.PAUSE + "09" + WebApiParams.SKIMMER)
    var pause09Skimmer: Int = 1
    @SerialName(value = WebApiParams.PAUSE + "09" + WebApiParams.MAIN_PUMP1)
    var pause09MainPump1: Int = 0
    @SerialName(value = WebApiParams.PAUSE + "09" + WebApiParams.MAIN_PUMP2)
    var pause09MainPump2: Int = 0
    @SerialName(value = WebApiParams.PAUSE + "09" + WebApiParams.AUX_PUMP1)
    var pause09AuxPump1: Int = 0
    @SerialName(value = WebApiParams.PAUSE + "09" + WebApiParams.AUX_PUMP2)
    var pause09AuxPump2: Int = 0

    @SerialName(value = WebApiParams.FEED + "09" + WebApiParams.DURATION)
    var feed09Duration: Int? = 0

    @SerialName(value = WebApiParams.FEED + "09" + WebApiParams.SKIMMER)
    var feed09Skimmer: Int = 1
    @SerialName(value = WebApiParams.FEED + "09" + WebApiParams.MAIN_PUMP1)
    var feed09MainPump1: Int = 0
    @SerialName(value = WebApiParams.FEED + "09" + WebApiParams.MAIN_PUMP2)
    var feed09MainPump2: Int = 0
    @SerialName(value = WebApiParams.FEED + "09" + WebApiParams.AUX_PUMP1)
    var feed09AuxPump1: Int = 0
    @SerialName(value = WebApiParams.FEED + "09" + WebApiParams.AUX_PUMP2)
    var feed09AuxPump2: Int = 0

    // Phase #10
    @SerialName(value = WebApiParams.PAUSE + "10" + WebApiParams.DURATION)
    var pause10Duration: Int? = 0

    @SerialName(value = WebApiParams.PAUSE + "10" + WebApiParams.SKIMMER)
    var pause10Skimmer: Int = 1
    @SerialName(value = WebApiParams.PAUSE + "10" + WebApiParams.MAIN_PUMP1)
    var pause10MainPump1: Int = 0
    @SerialName(value = WebApiParams.PAUSE + "10" + WebApiParams.MAIN_PUMP2)
    var pause10MainPump2: Int = 0
    @SerialName(value = WebApiParams.PAUSE + "10" + WebApiParams.AUX_PUMP1)
    var pause10AuxPump1: Int = 0
    @SerialName(value = WebApiParams.PAUSE + "10" + WebApiParams.AUX_PUMP2)
    var pause10AuxPump2: Int = 0

    @SerialName(value = WebApiParams.FEED + "10" + WebApiParams.DURATION)
    var feed10Duration: Int? = 0

    @SerialName(value = WebApiParams.FEED + "10" + WebApiParams.SKIMMER)
    var feed10Skimmer: Int = 1
    @SerialName(value = WebApiParams.FEED + "10" + WebApiParams.MAIN_PUMP1)
    var feed10MainPump1: Int = 0
    @SerialName(value = WebApiParams.FEED + "10" + WebApiParams.MAIN_PUMP2)
    var feed10MainPump2: Int = 0
    @SerialName(value = WebApiParams.FEED + "10" + WebApiParams.AUX_PUMP1)
    var feed10AuxPump1: Int = 0
    @SerialName(value = WebApiParams.FEED + "10" + WebApiParams.AUX_PUMP2)
    var feed10AuxPump2: Int = 0

    fun logContent(tag: String = "FeedProfileImport") {
        val sb = StringBuilder()
        sb.append("--- $tag Content Start ---\n")
        sb.append("  Profile ID: $profileId\n")
        sb.append("  Profile Name: $profileName\n")

        fun getDurationString(duration: Int?): String {
            return when (duration) {
                null -> "N/A"
                0 -> "0s (Inactive)" // Assuming 0 Duration means this phase is inactive
                else -> "${duration}s"
            }
        }

        fun getStatusString(status: Int): String {
            return if (status == 1) "ON" else "OFF"
        }

        // Helper function to append details for a specific phase
        fun appendPhaseDetails(
            phaseNum: Int,
            pauseDuration: Int?,
            pauseSkimmer: Int, pauseMP1: Int, pauseMP2: Int, pauseAP1: Int, pauseAP2: Int,
            feedDuration: Int?,
            feedSkimmer: Int, feedMP1: Int, feedMP2: Int, feedAP1: Int, feedAP2: Int
        ) {
            sb.append("  -- Phase #$phaseNum --\n")
            sb.append("    Pause Duration: ${getDurationString(pauseDuration)}\n")
            sb.append("      Pause Skimmer: ${getStatusString(pauseSkimmer)}\n")
            sb.append("      Pause Main Pump 1: ${getStatusString(pauseMP1)}\n")
            sb.append("      Pause Main Pump 2: ${getStatusString(pauseMP2)}\n")
            sb.append("      Pause Aux Pump 1: ${getStatusString(pauseAP1)}\n")
            sb.append("      Pause Aux Pump 2: ${getStatusString(pauseAP2)}\n")
            sb.append("    Feed Duration: ${getDurationString(feedDuration)}\n")
            sb.append("      Feed Skimmer: ${getStatusString(feedSkimmer)}\n")
            sb.append("      Feed Main Pump 1: ${getStatusString(feedMP1)}\n")
            sb.append("      Feed Main Pump 2: ${getStatusString(feedMP2)}\n")
            sb.append("      Feed Aux Pump 1: ${getStatusString(feedAP1)}\n")
            sb.append("      Feed Aux Pump 2: ${getStatusString(feedAP2)}\n")
        }

        // Call the helper function for each of the 10 phases
        appendPhaseDetails(
            1,
            pause01Duration,
            pause01Skimmer,
            pause01MainPump1,
            pause01MainPump2,
            pause01AuxPump1,
            pause01AuxPump2,
            feed01Duration,
            feed01Skimmer,
            feed01MainPump1,
            feed01MainPump2,
            feed01AuxPump1,
            feed01AuxPump2
        )
        appendPhaseDetails(
            2,
            pause02Duration,
            pause02Skimmer,
            pause02MainPump1,
            pause02MainPump2,
            pause02AuxPump1,
            pause02AuxPump2,
            feed02Duration,
            feed02Skimmer,
            feed02MainPump1,
            feed02MainPump2,
            feed02AuxPump1,
            feed02AuxPump2
        )
        appendPhaseDetails(
            3,
            pause03Duration,
            pause03Skimmer,
            pause03MainPump1,
            pause03MainPump2,
            pause03AuxPump1,
            pause03AuxPump2,
            feed03Duration,
            feed03Skimmer,
            feed03MainPump1,
            feed03MainPump2,
            feed03AuxPump1,
            feed03AuxPump2
        )
        appendPhaseDetails(
            4,
            pause04Duration,
            pause04Skimmer,
            pause04MainPump1,
            pause04MainPump2,
            pause04AuxPump1,
            pause04AuxPump2,
            feed04Duration,
            feed04Skimmer,
            feed04MainPump1,
            feed04MainPump2,
            feed04AuxPump1,
            feed04AuxPump2
        )
        appendPhaseDetails(
            5,
            pause05Duration,
            pause05Skimmer,
            pause05MainPump1,
            pause05MainPump2,
            pause05AuxPump1,
            pause05AuxPump2,
            feed05Duration,
            feed05Skimmer,
            feed05MainPump1,
            feed05MainPump2,
            feed05AuxPump1,
            feed05AuxPump2
        )
        appendPhaseDetails(
            6,
            pause06Duration,
            pause06Skimmer,
            pause06MainPump1,
            pause06MainPump2,
            pause06AuxPump1,
            pause06AuxPump2,
            feed06Duration,
            feed06Skimmer,
            feed06MainPump1,
            feed06MainPump2,
            feed06AuxPump1,
            feed06AuxPump2
        )
        appendPhaseDetails(
            7,
            pause07Duration,
            pause07Skimmer,
            pause07MainPump1,
            pause07MainPump2,
            pause07AuxPump1,
            pause07AuxPump2,
            feed07Duration,
            feed07Skimmer,
            feed07MainPump1,
            feed07MainPump2,
            feed07AuxPump1,
            feed07AuxPump2
        )
        appendPhaseDetails(
            8,
            pause08Duration,
            pause08Skimmer,
            pause08MainPump1,
            pause08MainPump2,
            pause08AuxPump1,
            pause08AuxPump2,
            feed08Duration,
            feed08Skimmer,
            feed08MainPump1,
            feed08MainPump2,
            feed08AuxPump1,
            feed08AuxPump2
        )
        appendPhaseDetails(
            9,
            pause09Duration,
            pause09Skimmer,
            pause09MainPump1,
            pause09MainPump2,
            pause09AuxPump1,
            pause09AuxPump2,
            feed09Duration,
            feed09Skimmer,
            feed09MainPump1,
            feed09MainPump2,
            feed09AuxPump1,
            feed09AuxPump2
        )
        appendPhaseDetails(
            10,
            pause10Duration,
            pause10Skimmer,
            pause10MainPump1,
            pause10MainPump2,
            pause10AuxPump1,
            pause10AuxPump2,
            feed10Duration,
            feed10Skimmer,
            feed10MainPump1,
            feed10MainPump2,
            feed10AuxPump1,
            feed10AuxPump2
        )

        sb.append("--- $tag Content End ---")
        //Log.d(tag, sb.toString())
    }
}