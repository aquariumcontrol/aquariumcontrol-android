package com.laimburggasse.aquariumcontrol.feed.edit

import com.github.mikephil.charting.data.LineData

data class FeedProfileEditUiState(
    val isLoading: Boolean = true,
    val menuItems: List<FeedProfileEditMenuItem> = emptyList(),
    val lineDataFeeder: LineData? = null,
    val lineDataSkimmer: LineData? = null,
    val lineDataMainPump1: LineData? = null,
    val lineDataMainPump2: LineData? = null,
    val lineDataAuxPump1: LineData? = null,
    val lineDataAuxPump2: LineData? = null,
    val expandedItemIds: Set<Int> = emptySet()
)
