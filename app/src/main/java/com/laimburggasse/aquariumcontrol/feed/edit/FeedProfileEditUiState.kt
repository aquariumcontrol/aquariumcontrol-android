package com.laimburggasse.aquariumcontrol.feed.edit

import com.github.mikephil.charting.data.LineData
import com.laimburggasse.aquariumcontrol.feed.common.FeedProfile

data class FeedProfileEditUiState(
    val feedProfile: FeedProfile = FeedProfile(),
    val expandedItemIds: Set<Int> = emptySet(),
    val lineDataFeeder: LineData? = null,
    val lineDataSkimmer: LineData? = null,
    val lineDataMainPump1: LineData? = null,
    val lineDataMainPump2: LineData? = null,
    val lineDataAuxPump1: LineData? = null,
    val lineDataAuxPump2: LineData? = null,
    val totalDuration: Float = 0f
)
