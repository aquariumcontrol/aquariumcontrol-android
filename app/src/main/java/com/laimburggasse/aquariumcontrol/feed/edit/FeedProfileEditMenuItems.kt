package com.laimburggasse.aquariumcontrol.feed.edit

import android.util.Log

fun mapIndexToMenuItemId(
    level: Int,
    phaseType: FeedProfileEditPhaseType?,
    index: Int,
    toggleButtonType: FeedProfileEditToggleButtonType? = null,
): Int {
    val isFeedPhase = phaseType == FeedProfileEditPhaseType.FEED
    return when (level) {
        1 -> index
        2 -> (index + 1) * 10 + if (isFeedPhase) 1 else 0
        3 -> (index + 1) * 100 + (if (isFeedPhase) 10 else 0) + (toggleButtonType?.value ?: 0)
        else -> 0
    }
}

// Mapping of unique menu item id to feed profile phase id in the range
// of 0...19
fun mapMenuItemIdToPhaseId(id: Int): Int {
    val base: Int = id / 100
    return (base - 1) * 2 + if (id % 100 >= 10) 1 else 0
}

fun createFeedProfileEditMenuItem(
    level: Int,
    index: Int,
    phaseType: FeedProfileEditPhaseType? = null,
    toggleButtonType: FeedProfileEditToggleButtonType? = null,
    children: List<FeedProfileEditMenuItem>? = null,
): FeedProfileEditMenuItem {
    val feedProfileNumber = index + 1
    val text = when (level) {
        1 -> "Phase $feedProfileNumber"
        2 -> if (phaseType == FeedProfileEditPhaseType.FEED) "Feed" else "Pause"
        3 -> when (toggleButtonType) {
            FeedProfileEditToggleButtonType.Skimmer -> "Skimmer"
            FeedProfileEditToggleButtonType.MainPump1 -> "Main Pump 1"
            FeedProfileEditToggleButtonType.MainPump2 -> "Main Pump 2"
            FeedProfileEditToggleButtonType.AuxPump1 -> "Aux. Pump 1"
            FeedProfileEditToggleButtonType.AuxPump2 -> "Aux. Pump 2"
            null -> ""
        }
        else -> ""
    }
    val id: Int = mapIndexToMenuItemId(level, phaseType, index, toggleButtonType)
    //Log.d("createFeedProfileEditMenuItem", "id: $id, level: $level, text: $text")

    return FeedProfileEditMenuItem(
        id,
        level,
        text,
        isToggleButton = toggleButtonType != null,
        phaseType,
        toggleButtonType,
        isNumericInput = (level == 3) && (toggleButtonType == null),
        children = children ?: emptyList()
    )
}

// This class is used as a helper to define the structure of the UI.
// It shall not hold any state nor any UI elements.
// It is a recursive data structure of elements of type FeedProfileEditMenuItem
object FeedProfileEditMenuItems {
    val phases: List<FeedProfileEditMenuItem> = List(size = 10) { index ->
        createFeedProfileEditMenuItem(
            level = 1,
            index = index,
            children = mutableListOf(
                createFeedProfileEditMenuItem(
                    level = 2,
                    index = index,
                    phaseType = FeedProfileEditPhaseType.PAUSE,
                    children = mutableListOf(
                        createFeedProfileEditMenuItem(
                            level = 3,
                            index = index,
                            phaseType = FeedProfileEditPhaseType.PAUSE,
                        ),
                        createFeedProfileEditMenuItem(
                            level = 3,
                            index = index,
                            phaseType = FeedProfileEditPhaseType.PAUSE,
                            toggleButtonType = FeedProfileEditToggleButtonType.Skimmer,
                        ),
                        createFeedProfileEditMenuItem(
                            level = 3,
                            index = index,
                            phaseType = FeedProfileEditPhaseType.PAUSE,
                            toggleButtonType = FeedProfileEditToggleButtonType.MainPump1,
                        ),
                        createFeedProfileEditMenuItem(
                            level = 3,
                            index = index,
                            phaseType = FeedProfileEditPhaseType.PAUSE,
                            toggleButtonType = FeedProfileEditToggleButtonType.MainPump2,
                        ),
                        createFeedProfileEditMenuItem(
                            level = 3,
                            index = index,
                            phaseType = FeedProfileEditPhaseType.PAUSE,
                            toggleButtonType = FeedProfileEditToggleButtonType.AuxPump1,
                        ),
                        createFeedProfileEditMenuItem(
                            level = 3,
                            index = index,
                            phaseType = FeedProfileEditPhaseType.PAUSE,
                            toggleButtonType = FeedProfileEditToggleButtonType.AuxPump2,
                        ),
                    )
                ),
                createFeedProfileEditMenuItem(
                    level = 2,
                    index = index,
                    phaseType = FeedProfileEditPhaseType.FEED,
                    children = mutableListOf(
                        createFeedProfileEditMenuItem(
                            level = 3,
                            index = index,
                            phaseType = FeedProfileEditPhaseType.FEED,
                        ),
                        createFeedProfileEditMenuItem(
                            level = 3,
                            index = index,
                            phaseType = FeedProfileEditPhaseType.FEED,
                            toggleButtonType = FeedProfileEditToggleButtonType.Skimmer,
                        ),
                        createFeedProfileEditMenuItem(
                            level = 3,
                            index = index,
                            phaseType = FeedProfileEditPhaseType.FEED,
                            toggleButtonType = FeedProfileEditToggleButtonType.MainPump1,
                        ),
                        createFeedProfileEditMenuItem(
                            level = 3,
                            index = index,
                            phaseType = FeedProfileEditPhaseType.FEED,
                            toggleButtonType = FeedProfileEditToggleButtonType.MainPump2,
                        ),
                        createFeedProfileEditMenuItem(
                            level = 3,
                            index = index,
                            phaseType = FeedProfileEditPhaseType.FEED,
                            toggleButtonType = FeedProfileEditToggleButtonType.AuxPump1,
                        ),
                        createFeedProfileEditMenuItem(
                            level = 3,
                            index = index,
                            phaseType = FeedProfileEditPhaseType.FEED,
                            toggleButtonType = FeedProfileEditToggleButtonType.AuxPump2,
                        ),
                    )
                ),
            )
        )
    }
}
