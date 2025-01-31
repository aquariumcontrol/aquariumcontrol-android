package com.example.aquariumcontroller.feed

import android.annotation.SuppressLint
import com.example.aquariumcontroller.ApiParams

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
    constructor(
        duration: Int,
        skimmer: Int,
        mainPump1: Int,
        mainPump2: Int,
        auxPump1: Int,
        auxPump2: Int,
        isPause: Boolean,
        idx: Int
    ) : this() {
        this.duration = duration.toDouble() / 1000.0
        this.skimmer = skimmer > 0
        this.mainPump1 = mainPump1 > 0
        this.mainPump2 = mainPump2 > 0
        this.auxPump1 = auxPump1 > 0
        this.auxPump2 = auxPump2 > 0
        this.isPause = isPause
        this.idx = idx
    }

    fun overwriteWith(otherPhase: FeedProfilePhase) {
        this.duration = otherPhase.duration
        this.skimmer = otherPhase.skimmer
        this.mainPump1 = otherPhase.mainPump1
        this.mainPump2 = otherPhase.mainPump2
        this.auxPump1 = otherPhase.auxPump1
        this.auxPump2 = otherPhase.auxPump2
        this.isPause = otherPhase.isPause
        this.idx = otherPhase.idx
    }

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
            getPostParameterNameBase(idx)+ ApiParams.DURATION to (duration * 1000.0).toInt().toString(),
            getPostParameterNameBase(idx)+ ApiParams.SKIMMER to (if (skimmer) 1 else 0).toString(),
            getPostParameterNameBase(idx)+ ApiParams.MAINPUMP1 to (if (mainPump1) 1 else 0).toString(),
            getPostParameterNameBase(idx)+ ApiParams.MAINPUMP2 to (if (mainPump2) 1 else 0).toString(),
            getPostParameterNameBase(idx)+ ApiParams.AUXPUMP1 to (if (auxPump1) 1 else 0).toString(),
            getPostParameterNameBase(idx)+ ApiParams.AUXPUMP2 to (if (auxPump2) 1 else 0).toString(),
        )
    }

}