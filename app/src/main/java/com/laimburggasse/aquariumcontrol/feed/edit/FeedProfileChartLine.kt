package com.laimburggasse.aquariumcontrol.feed.edit

import com.github.mikephil.charting.data.LineData
import com.laimburggasse.aquariumcontrol.feed.common.FeedProfilePhase
import kotlinx.coroutines.flow.MutableStateFlow

data class FeedProfileChartLine(
    val name: String,
    val signal: FeedProfileSignal,
    val lineDataStateFlow: MutableStateFlow<LineData?>,
    val getValue: (FeedProfilePhase) -> Boolean,
    val yAxisValueOn: Float,
    val yAxisValueOff: Float
)