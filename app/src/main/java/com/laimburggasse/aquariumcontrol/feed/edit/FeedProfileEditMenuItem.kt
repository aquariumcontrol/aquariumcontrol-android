package com.laimburggasse.aquariumcontrol.feed.edit

data class FeedProfileEditMenuItem(
    val id: Int,
    val level: Int,
    val text: String,
    val isToggleButton: Boolean = false,
    val phaseType: FeedProfileEditPhaseType? = null,
    val toggleButtonType: FeedProfileEditToggleButtonType? = null,
    val isNumericInput: Boolean = false,
    val isChecked: Boolean,
    val duration: Double,
    val children: List<FeedProfileEditMenuItem> = emptyList(),
)
