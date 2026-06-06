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

/**
 * Data Transfer Object (DTO) used for importing feed profiles from the remote API.
 * Contains flat properties mapping to the multiphase structure used by the controller hardware.
 */
@Serializable
class FeedProfileImport {
    @SerialName(value = WebApiParams.PROFILE_ID)
        /** The unique identifier of the feed profile. */
    var profileId: Int = 0

    @SerialName(value = WebApiParams.PROFILE_NAME)
        /** The descriptive name of the feed profile. */
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

}