package com.laimburggasse.aquariumcontrol.feed.edit

import com.github.mikephil.charting.data.LineData

data class FeedProfileChartDataBundle(
    val lineDataFeeder: LineData?,
    val lineDataSkimmer: LineData?,
    val lineDataMainPump1: LineData?,
    val lineDataMainPump2: LineData?,
    val lineDataAuxPump1: LineData?,
    val lineDataAuxPump2: LineData?,
    val totalDuration: Float
)