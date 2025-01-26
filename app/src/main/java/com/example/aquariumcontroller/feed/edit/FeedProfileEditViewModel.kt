package com.example.aquariumcontroller.feed.edit

import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.aquariumcontroller.feed.FeedProfile

class FeedProfileEditViewModel(private val myFeedProfile: FeedProfile) : ViewModel() {

    var phaseMenuItems: List<FeedProfileEditMenuItem> = List(10) { index ->
        FeedProfileEditMenuItem(
            id = index,
            level = 1,
            text = "Phase ${index + 1}",
            isToggleButton = false,
            children = mutableListOf(
                FeedProfileEditMenuItem(
                    id = 0,
                    level = 2,
                    text = "Pause",
                    isToggleButton = false,
                    children = mutableListOf(
                        FeedProfileEditMenuItem(
                            id = index * 2,
                            level = 3,
                            text = "Duration",
                            isNumericInput = true,
                            inputValue = mutableDoubleStateOf(
                                myFeedProfile.feedProfilePhases[index * 2].duration
                            ),
                            isToggleButton = false,
                            isExpanded = mutableStateOf(false) // Initially collapsed
                        ),
                        FeedProfileEditMenuItem(
                            id = index * 2,
                            level = 3,
                            text = "Skimmer",
                            toggleButtonType = FeedProfileEditToggleButtonType.Skimmer,
                            isToggleButton = true,
                            isNumericInput = false,
                            isChecked = mutableStateOf(
                                myFeedProfile.feedProfilePhases[index * 2].skimmer
                            ),
                            isExpanded = mutableStateOf(false) // Initially collapsed
                        ),
                        FeedProfileEditMenuItem(
                            id = index * 2,
                            level = 3,
                            text = "Main Pump 1",
                            toggleButtonType = FeedProfileEditToggleButtonType.MainPump1,
                            isToggleButton = true,
                            isNumericInput = false,
                            isChecked = mutableStateOf(
                                myFeedProfile.feedProfilePhases[index * 2].mainPump1
                            ),
                            isExpanded = mutableStateOf(false) // Initially collapsed
                        ),
                        FeedProfileEditMenuItem(
                            id = index * 2,
                            level = 3,
                            text = "Main Pump 2",
                            toggleButtonType = FeedProfileEditToggleButtonType.MainPump2,
                            isToggleButton = true,
                            isNumericInput = false,
                            isChecked = mutableStateOf(
                                myFeedProfile.feedProfilePhases[index * 2].mainPump2
                            ),
                            isExpanded = mutableStateOf(false) // Initially collapsed
                        ),
                        FeedProfileEditMenuItem(
                            id = index * 2,
                            level = 3,
                            text = "Aux. Pump 1",
                            toggleButtonType = FeedProfileEditToggleButtonType.AuxPump1,
                            isToggleButton = true,
                            isNumericInput = false,
                            isChecked = mutableStateOf(
                                myFeedProfile.feedProfilePhases[index * 2].auxPump1
                            ),
                            isExpanded = mutableStateOf(false) // Initially collapsed
                        ),
                        FeedProfileEditMenuItem(
                            id = index * 2,
                            level = 3,
                            text = "Aux. Pump 2",
                            toggleButtonType = FeedProfileEditToggleButtonType.AuxPump2,
                            isToggleButton = true,
                            isNumericInput = false,
                            isChecked = mutableStateOf(
                                myFeedProfile.feedProfilePhases[index * 2].auxPump2
                            ),
                            isExpanded = mutableStateOf(false) // Initially collapsed
                        ),
                    ),
                    isExpanded = mutableStateOf(false) // Initially collapsed
                ),
                FeedProfileEditMenuItem(
                    id = 1,
                    level = 2,
                    text = "Feed",
                    isToggleButton = false,
                    children = mutableListOf(
                        FeedProfileEditMenuItem(
                            id = index * 2 + 1,
                            level = 3,
                            text = "Duration",
                            isToggleButton = false,
                            isNumericInput = true,
                            inputValue = mutableDoubleStateOf(
                                myFeedProfile.feedProfilePhases[index * 2 + 1].duration
                            ),
                            isExpanded = mutableStateOf(false) // Initially collapsed
                        ),
                        FeedProfileEditMenuItem(
                            id = index * 2 + 1,
                            level = 3,
                            text = "Skimmer",
                            toggleButtonType = FeedProfileEditToggleButtonType.Skimmer,
                            isToggleButton = true,
                            isNumericInput = false,
                            isChecked = mutableStateOf(
                                myFeedProfile.feedProfilePhases[index * 2 + 1].skimmer
                            ),
                            isExpanded = mutableStateOf(false) // Initially collapsed
                        ),
                        FeedProfileEditMenuItem(
                            id = index * 2 + 1,
                            level = 3,
                            text = "Main Pump 1",
                            toggleButtonType = FeedProfileEditToggleButtonType.MainPump1,
                            isToggleButton = true,
                            isNumericInput = false,
                            isChecked = mutableStateOf(
                                myFeedProfile.feedProfilePhases[index * 2 + 1].mainPump1
                            ),
                            isExpanded = mutableStateOf(false) // Initially collapsed
                        ),
                        FeedProfileEditMenuItem(
                            id = index * 2 + 1,
                            level = 3,
                            text = "Main Pump 2",
                            toggleButtonType = FeedProfileEditToggleButtonType.MainPump2,
                            isToggleButton = true,
                            isNumericInput = false,
                            isChecked = mutableStateOf(
                                myFeedProfile.feedProfilePhases[index * 2 + 1].mainPump2
                            ),
                            isExpanded = mutableStateOf(false) // Initially collapsed
                        ),
                        FeedProfileEditMenuItem(
                            id = index * 2 + 1,
                            level = 3,
                            text = "Aux. Pump 1",
                            toggleButtonType = FeedProfileEditToggleButtonType.AuxPump1,
                            isToggleButton = true,
                            isNumericInput = false,
                            isChecked = mutableStateOf(
                                myFeedProfile.feedProfilePhases[index * 2 + 1].auxPump1
                            ),
                            isExpanded = mutableStateOf(false) // Initially collapsed
                        ),
                        FeedProfileEditMenuItem(
                            id = index * 2 + 1,
                            level = 3,
                            text = "Aux. Pump 2",
                            toggleButtonType = FeedProfileEditToggleButtonType.AuxPump2,
                            isToggleButton = true,
                            isNumericInput = false,
                            isChecked = mutableStateOf(
                                myFeedProfile.feedProfilePhases[index * 2 + 1].auxPump2
                            ),
                            isExpanded = mutableStateOf(false) // Initially collapsed
                        ),
                    ),
                    isExpanded = mutableStateOf(false) // Initially collapsed
                ),
            ),
            isExpanded = mutableStateOf(false) // Initially collapsed
        )
    }

    fun updatePhaseDuration(phaseIndex: Int, newDuration: Double) {
        myFeedProfile.feedProfilePhases[phaseIndex].duration = newDuration
    }

    fun updateSkimmer(phaseIndex: Int, newValue: Boolean) {
        myFeedProfile.feedProfilePhases[phaseIndex].skimmer = newValue
    }

    fun updateMainPump1(phaseIndex: Int, newValue: Boolean) {
        myFeedProfile.feedProfilePhases[phaseIndex].mainPump1 = newValue
    }

    fun updateMainPump2(phaseIndex: Int, newValue: Boolean) {
        myFeedProfile.feedProfilePhases[phaseIndex].mainPump2 = newValue
    }

    fun updateAuxPump1(phaseIndex: Int, newValue: Boolean) {
        myFeedProfile.feedProfilePhases[phaseIndex].auxPump1 = newValue
    }

    fun updateAuxPump2(phaseIndex: Int, newValue: Boolean) {
        myFeedProfile.feedProfilePhases[phaseIndex].auxPump2 = newValue
    }
}