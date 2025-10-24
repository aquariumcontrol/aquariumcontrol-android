package com.laimburggasse.aquariumcontrol.feed.edit

sealed interface FeedProfileEditUiEvent {
    data class DurationChange(val menuItemId: Int, val newDuration: Double): FeedProfileEditUiEvent
    data class SwitchToggle(
        val menuItemId: Int,
        val toggleButtonType: FeedProfileEditToggleButtonType,
        val isChecked: Boolean
    ): FeedProfileEditUiEvent
    data class ExpandChildren(val menuItemId: Int): FeedProfileEditUiEvent
    object Update: FeedProfileEditUiEvent
    object Reset: FeedProfileEditUiEvent
}