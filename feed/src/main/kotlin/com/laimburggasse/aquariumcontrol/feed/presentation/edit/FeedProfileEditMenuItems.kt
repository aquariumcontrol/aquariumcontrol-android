/*
 * Copyright 2025 Uwe Martin
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.laimburggasse.aquariumcontrol.feed.presentation.edit

import com.laimburggasse.aquariumcontrol.common.domain.GlobalConstants

fun mapIndexToMenuItemId(
    level: Int,
    phaseType: FeedProfileEditPhaseType?,
    index: Int,
    toggleButtonType: FeedProfileEditToggleButtonType? = null,
): Int {
    val isFeedPhase: Boolean = phaseType == FeedProfileEditPhaseType.FEED
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
    val feedProfileNumber: Int = index + 1
    val text: String = when (level) {
        1 -> GlobalConstants.LABEL_PHASE + feedProfileNumber
        2 -> if (phaseType == FeedProfileEditPhaseType.FEED)
                 GlobalConstants.LABEL_FEED
             else
                 GlobalConstants.LABEL_PAUSE
        3 -> when (toggleButtonType) {
            FeedProfileEditToggleButtonType.Skimmer -> GlobalConstants.LABEL_SKIMMER
            FeedProfileEditToggleButtonType.MainPump1 -> GlobalConstants.LABEL_MAIN_PUMP_1
            FeedProfileEditToggleButtonType.MainPump2 -> GlobalConstants.LABEL_MAIN_PUMP_2
            FeedProfileEditToggleButtonType.AuxPump1 -> GlobalConstants.LABEL_AUX_PUMP_1
            FeedProfileEditToggleButtonType.AuxPump2 -> GlobalConstants.LABEL_AUX_PUMP_2
            null -> ""
        }
        else -> ""
    }
    val id: Int = mapIndexToMenuItemId(level, phaseType, index, toggleButtonType)

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
