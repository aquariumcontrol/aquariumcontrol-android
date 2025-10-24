package com.laimburggasse.aquariumcontrol.feed.common

import android.annotation.SuppressLint
import com.laimburggasse.aquariumcontrol.webapi.WebApiParams

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
        @SuppressLint("DefaultLocale") val formattedIdx = String.format("%02d", idx + 1)
        return if (isPause) {
            "pause$formattedIdx"
        } else {
            "feed$formattedIdx"
        }
    }

    fun getFeedProfilePhaseApiCallParameters(idx: Int): Map<String, String> {
        return mapOf(
            // convert duration to milliseconds
            getPostParameterNameBase(idx)+ WebApiParams.DURATION to (duration * 1000.0).toInt().toString(),
            getPostParameterNameBase(idx)+ WebApiParams.SKIMMER to (if (skimmer) 1 else 0).toString(),
            getPostParameterNameBase(idx)+ WebApiParams.MAINPUMP1 to (if (mainPump1) 1 else 0).toString(),
            getPostParameterNameBase(idx)+ WebApiParams.MAINPUMP2 to (if (mainPump2) 1 else 0).toString(),
            getPostParameterNameBase(idx)+ WebApiParams.AUXPUMP1 to (if (auxPump1) 1 else 0).toString(),
            getPostParameterNameBase(idx)+ WebApiParams.AUXPUMP2 to (if (auxPump2) 1 else 0).toString(),
        )
    }

}