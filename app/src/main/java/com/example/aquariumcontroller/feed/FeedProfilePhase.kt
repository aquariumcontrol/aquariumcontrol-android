package com.example.aquariumcontroller.feed

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
}