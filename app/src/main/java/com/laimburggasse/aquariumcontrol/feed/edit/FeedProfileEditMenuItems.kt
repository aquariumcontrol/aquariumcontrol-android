package com.laimburggasse.aquariumcontrol.feed.edit

// This class is used as a helper to define the structure of the UI.
// It shall not hold any state nor any UI elements.
// It is a recursive data structure of elements of type FeedProfileEditMenuItem
object FeedProfileEditMenuItems {
    val phases: List<FeedProfileEditMenuItem> = List(size = 10) { index ->
        FeedProfileEditMenuItem(
            id = index,
            level = 1,
            text = "Phase ${index + 1}",
            isToggleButton = false,
            isNumericInput = false,
            children = mutableListOf(
                FeedProfileEditMenuItem(
                    id = 0,
                    level = 2,
                    text = "Pause",
                    isToggleButton = false,
                    isNumericInput = false,
                    phaseType = FeedProfileEditPhaseType.PAUSE,
                    children = mutableListOf(
                        FeedProfileEditMenuItem(
                            id = 0,
                            level = 3,
                            text = "Duration",
                            isToggleButton = false,
                            phaseType = FeedProfileEditPhaseType.PAUSE,
                            isNumericInput = true,
                        ),
                        FeedProfileEditMenuItem(
                            id = (index * 2) * 10 + 1,
                            level = 3,
                            text = "Skimmer",
                            isToggleButton = true,
                            phaseType = FeedProfileEditPhaseType.PAUSE,
                            toggleButtonType = FeedProfileEditToggleButtonType.Skimmer,
                            isNumericInput = false,
                        ),
                        FeedProfileEditMenuItem(
                            id = (index * 2) * 10 + 2,
                            level = 3,
                            text = "Main Pump 1",
                            phaseType = FeedProfileEditPhaseType.PAUSE,
                            toggleButtonType = FeedProfileEditToggleButtonType.MainPump1,
                            isNumericInput = false,
                        ),
                        FeedProfileEditMenuItem(
                            id = (index * 2) * 10 + 3,
                            level = 3,
                            text = "Main Pump 2",
                            phaseType = FeedProfileEditPhaseType.PAUSE,
                            toggleButtonType = FeedProfileEditToggleButtonType.MainPump2,
                            isNumericInput = false,
                        ),
                        FeedProfileEditMenuItem(
                            id = (index * 2) * 10 + 4,
                            level = 3,
                            text = "Aux. Pump 1",
                            phaseType = FeedProfileEditPhaseType.PAUSE,
                            toggleButtonType = FeedProfileEditToggleButtonType.AuxPump1,
                            isNumericInput = false,
                        ),
                        FeedProfileEditMenuItem(
                            id = (index * 2) * 10 + 5,
                            level = 3,
                            text = "Aux. Pump 2",
                            phaseType = FeedProfileEditPhaseType.PAUSE,
                            toggleButtonType = FeedProfileEditToggleButtonType.AuxPump2,
                            isNumericInput = false,
                        ),
                    )
                ),
                FeedProfileEditMenuItem(
                    id = 1,
                    level = 2,
                    text = "Feed",
                    isToggleButton = false,
                    isNumericInput = false,
                    phaseType = FeedProfileEditPhaseType.FEED,
                    children = mutableListOf(
                        FeedProfileEditMenuItem(
                            id = (index * 2 + 1) * 10 + 0,
                            level = 3,
                            text = "Duration",
                            isToggleButton = false,
                            phaseType = FeedProfileEditPhaseType.FEED,
                            isNumericInput = true,
                        ),
                        FeedProfileEditMenuItem(
                            id = (index * 2 + 1) * 10 + 1,
                            level = 3,
                            text = "Skimmer",
                            isToggleButton = true,
                            phaseType = FeedProfileEditPhaseType.FEED,
                            toggleButtonType = FeedProfileEditToggleButtonType.Skimmer,
                            isNumericInput = false,
                        ),
                        FeedProfileEditMenuItem(
                            id = (index * 2 + 1) * 10 + 2,
                            level = 3,
                            text = "Main Pump 1",
                            phaseType = FeedProfileEditPhaseType.FEED,
                            toggleButtonType = FeedProfileEditToggleButtonType.MainPump1,
                            isNumericInput = false,
                        ),
                        FeedProfileEditMenuItem(
                            id = (index * 2 + 1) * 10 + 3,
                            level = 3,
                            text = "Main Pump 2",
                            phaseType = FeedProfileEditPhaseType.FEED,
                            toggleButtonType = FeedProfileEditToggleButtonType.MainPump2,
                            isNumericInput = false,
                        ),
                        FeedProfileEditMenuItem(
                            id = (index * 2 + 1) * 10 + 4,
                            level = 3,
                            text = "Aux. Pump 1",
                            phaseType = FeedProfileEditPhaseType.FEED,
                            toggleButtonType = FeedProfileEditToggleButtonType.AuxPump1,
                            isNumericInput = false,
                        ),
                        FeedProfileEditMenuItem(
                            id = (index * 2 + 1) * 10 + 5,
                            level = 3,
                            text = "Aux. Pump 2",
                            phaseType = FeedProfileEditPhaseType.FEED,
                            toggleButtonType = FeedProfileEditToggleButtonType.AuxPump2,
                            isNumericInput = false,
                        )
                    )
                )
            )
        )
    }
}
