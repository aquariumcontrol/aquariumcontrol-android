package com.laimburggasse.aquariumcontrol.feed.edit

// This class is used as a helper to define the structure of the UI.
// It shall not hold any state nor any UI elements.
data class FeedProfileEditMenuItem(
    val id: Int,
    val level: Int,
    val text: String,
    val isToggleButton: Boolean,
    val phaseType: FeedProfileEditPhaseType? = null,
    val toggleButtonType: FeedProfileEditToggleButtonType? = null,
    val isNumericInput: Boolean,
    val children: List<FeedProfileEditMenuItem> = emptyList(),
)
