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

import com.laimburggasse.aquariumcontrol.common.data.remote.WebApiParams

/**
 * Domain model representing a complete feed profile configuration.
 * A profile consists of multiple phases defining actuator states over time.
 *
 * @property profileID The unique identifier for the feed profile.
 * @property profileName The descriptive name of the feed profile.
 * @property feedProfilePhases The ordered list of [FeedProfilePhase]s that make up the sequence.
 */
data class FeedProfile(
    val profileID: Int,
    val profileName: String,
    val feedProfilePhases: List<FeedProfilePhase>,
) {
    constructor() : this(
        profileID = 0,
        profileName = "",
        // Creates a list of 20 default phases (10 pause, 10 feed)
        feedProfilePhases = List(size = 20) { index ->
            FeedProfilePhase(
                duration = 0.0,
                skimmer = false,
                mainPump1 = false,
                mainPump2 = false,
                auxPump1 = false,
                auxPump2 = false,
                isPause = index % 2 == 0, // Even indices are pause phases, uneven are feed phases
                idx = index
            )
        }
    )

        /**
     * Flattens the profile and its phases into a map of parameters suitable for a remote API call.
     *
     * @return A map of query parameters representing the full profile state.
     */
    fun getFeedProfileApiCallParameters(): Map<String, String> {
        // This function iterates over the feed profile phases and adds all the parameters to the
        // map which is then used to form the GET part of the API call
        val myMap: MutableMap<String, String> = mutableMapOf()
        for ((idx: Int, feedProfilePhase: FeedProfilePhase) in feedProfilePhases.withIndex()) {
            myMap += feedProfilePhase.getFeedProfilePhaseApiCallParameters(
                idx = (idx.toFloat() / 2.0).toInt()
            )
        }
        myMap += mapOf(
            WebApiParams.PROFILE_NAME to this.profileName,
            WebApiParams.PROFILE_ID to this.profileID.toString()
        )
        return myMap
    }
}