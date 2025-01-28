package com.example.aquariumcontroller.feed.edit

import android.util.Log
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.aquariumcontroller.feed.FeedProfile

class FeedProfileEditViewModel(private val myFeedProfile: FeedProfile) : ViewModel() {

    var phaseMenuItems: List<FeedProfileEditMenuItem>

    init {
        phaseMenuItems = createFeedProfileEditMenuItems()
    }

    private fun resetFeedProfileEditMenuItems() {
        for ((index, value) in phaseMenuItems.withIndex()) {
            for (value2 in value.children) {
                when (value2.phaseType) {
                    FeedProfileEditPhaseType.PAUSE -> {
                        for (value3 in value2.children) {
                            when (value3.toggleButtonType) {
                                FeedProfileEditToggleButtonType.Skimmer -> {
                                    value3.updateIsChecked(
                                        myFeedProfile.feedProfilePhases[index * 2].skimmer
                                    )
                                }

                                FeedProfileEditToggleButtonType.MainPump1 -> {
                                    value3.updateIsChecked(
                                        myFeedProfile.feedProfilePhases[index * 2].mainPump1
                                    )
                                }

                                FeedProfileEditToggleButtonType.MainPump2 -> {
                                    value3.updateIsChecked(
                                        myFeedProfile.feedProfilePhases[index * 2].mainPump2
                                    )
                                }

                                FeedProfileEditToggleButtonType.AuxPump1 -> {
                                    value3.updateIsChecked(
                                        myFeedProfile.feedProfilePhases[index * 2].auxPump1
                                    )
                                }

                                FeedProfileEditToggleButtonType.AuxPump2 -> {
                                    value3.updateIsChecked(
                                        myFeedProfile.feedProfilePhases[index * 2].auxPump2
                                    )
                                }

                                null -> {
                                    value3.updateInputValue(
                                        myFeedProfile.feedProfilePhases[index * 2].duration
                                    )
                                    Log.d("FeedProfileEditViewModel", "setting myFeedProfile.feedProfilePhases[${index * 2}].duration = ${value3.inputValue.value}")
                                }
                            }
                        }
                    }

                    FeedProfileEditPhaseType.FEED -> {
                        for (value3 in value2.children) {
                            when (value3.toggleButtonType) {
                                FeedProfileEditToggleButtonType.Skimmer -> {
                                    value3.updateIsChecked(
                                        myFeedProfile.feedProfilePhases[index * 2 + 1].skimmer
                                    )
                                }

                                FeedProfileEditToggleButtonType.MainPump1 -> {
                                    value3.updateIsChecked(
                                        myFeedProfile.feedProfilePhases[index * 2 + 1].mainPump1
                                    )
                                }

                                FeedProfileEditToggleButtonType.MainPump2 -> {
                                    value3.updateIsChecked(
                                        myFeedProfile.feedProfilePhases[index * 2 + 1].mainPump2
                                    )
                                }

                                FeedProfileEditToggleButtonType.AuxPump1 -> {
                                    value3.updateIsChecked(
                                        myFeedProfile.feedProfilePhases[index * 2 + 1].auxPump1
                                    )
                                }

                                FeedProfileEditToggleButtonType.AuxPump2 -> {
                                    value3.updateIsChecked(
                                        myFeedProfile.feedProfilePhases[index * 2 + 1].auxPump2
                                    )
                                }

                                null -> {
                                    value3.updateInputValue(
                                        myFeedProfile.feedProfilePhases[index * 2].duration
                                    )
                                    Log.d("FeedProfileEditViewModel", "setting myFeedProfile.feedProfilePhases[${index * 2 + 1}].duration = ${value3.inputValue.value}")
                                }
                            }
                        }
                    }

                    null -> {
                        // do nothing
                    }
                }
            }
        }
    }

    private fun createFeedProfileEditMenuItems(): List<FeedProfileEditMenuItem> {
        return List(10) { index ->
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
                        phaseType = FeedProfileEditPhaseType.PAUSE,
                        children = mutableListOf(
                            FeedProfileEditMenuItem(
                                id = index * 2,
                                level = 3,
                                text = "Duration",
                                isNumericInput = true,
                                phaseType = FeedProfileEditPhaseType.PAUSE,
                                inputValue = mutableDoubleStateOf(
                                    myFeedProfile.feedProfilePhases[index * 2].duration
                                ),
                                isToggleButton = false,
                            ),
                            FeedProfileEditMenuItem(
                                id = index * 2,
                                level = 3,
                                text = "Skimmer",
                                toggleButtonType = FeedProfileEditToggleButtonType.Skimmer,
                                isToggleButton = true,
                                isNumericInput = false,
                                phaseType = FeedProfileEditPhaseType.PAUSE,
                                isChecked = mutableStateOf(
                                    myFeedProfile.feedProfilePhases[index * 2].skimmer
                                ),
                            ),
                            FeedProfileEditMenuItem(
                                id = index * 2,
                                level = 3,
                                text = "Main Pump 1",
                                toggleButtonType = FeedProfileEditToggleButtonType.MainPump1,
                                isToggleButton = true,
                                isNumericInput = false,
                                phaseType = FeedProfileEditPhaseType.PAUSE,
                                isChecked = mutableStateOf(
                                    myFeedProfile.feedProfilePhases[index * 2].mainPump1
                                ),
                            ),
                            FeedProfileEditMenuItem(
                                id = index * 2,
                                level = 3,
                                text = "Main Pump 2",
                                toggleButtonType = FeedProfileEditToggleButtonType.MainPump2,
                                isToggleButton = true,
                                isNumericInput = false,
                                phaseType = FeedProfileEditPhaseType.PAUSE,
                                isChecked = mutableStateOf(
                                    myFeedProfile.feedProfilePhases[index * 2].mainPump2
                                ),
                            ),
                            FeedProfileEditMenuItem(
                                id = index * 2,
                                level = 3,
                                text = "Aux. Pump 1",
                                toggleButtonType = FeedProfileEditToggleButtonType.AuxPump1,
                                isToggleButton = true,
                                isNumericInput = false,
                                phaseType = FeedProfileEditPhaseType.PAUSE,
                                isChecked = mutableStateOf(
                                    myFeedProfile.feedProfilePhases[index * 2].auxPump1
                                ),
                            ),
                            FeedProfileEditMenuItem(
                                id = index * 2,
                                level = 3,
                                text = "Aux. Pump 2",
                                toggleButtonType = FeedProfileEditToggleButtonType.AuxPump2,
                                isToggleButton = true,
                                isNumericInput = false,
                                phaseType = FeedProfileEditPhaseType.PAUSE,
                                isChecked = mutableStateOf(
                                    myFeedProfile.feedProfilePhases[index * 2].auxPump2
                                ),
                            ),
                        ),
                    ),
                    FeedProfileEditMenuItem(
                        id = 1,
                        level = 2,
                        text = "Feed",
                        isToggleButton = false,
                        phaseType = FeedProfileEditPhaseType.FEED,
                        children = mutableListOf(
                            FeedProfileEditMenuItem(
                                id = index * 2 + 1,
                                level = 3,
                                text = "Duration",
                                isToggleButton = false,
                                isNumericInput = true,
                                phaseType = FeedProfileEditPhaseType.FEED,
                                inputValue = mutableDoubleStateOf(
                                    myFeedProfile.feedProfilePhases[index * 2 + 1].duration
                                ),
                            ),
                            FeedProfileEditMenuItem(
                                id = index * 2 + 1,
                                level = 3,
                                text = "Skimmer",
                                toggleButtonType = FeedProfileEditToggleButtonType.Skimmer,
                                isToggleButton = true,
                                isNumericInput = false,
                                phaseType = FeedProfileEditPhaseType.FEED,
                                isChecked = mutableStateOf(
                                    myFeedProfile.feedProfilePhases[index * 2 + 1].skimmer
                                ),
                            ),
                            FeedProfileEditMenuItem(
                                id = index * 2 + 1,
                                level = 3,
                                text = "Main Pump 1",
                                toggleButtonType = FeedProfileEditToggleButtonType.MainPump1,
                                isToggleButton = true,
                                isNumericInput = false,
                                phaseType = FeedProfileEditPhaseType.FEED,
                                isChecked = mutableStateOf(
                                    myFeedProfile.feedProfilePhases[index * 2 + 1].mainPump1
                                ),
                            ),
                            FeedProfileEditMenuItem(
                                id = index * 2 + 1,
                                level = 3,
                                text = "Main Pump 2",
                                toggleButtonType = FeedProfileEditToggleButtonType.MainPump2,
                                isToggleButton = true,
                                isNumericInput = false,
                                phaseType = FeedProfileEditPhaseType.FEED,
                                isChecked = mutableStateOf(
                                    myFeedProfile.feedProfilePhases[index * 2 + 1].mainPump2
                                ),
                            ),
                            FeedProfileEditMenuItem(
                                id = index * 2 + 1,
                                level = 3,
                                text = "Aux. Pump 1",
                                toggleButtonType = FeedProfileEditToggleButtonType.AuxPump1,
                                isToggleButton = true,
                                isNumericInput = false,
                                phaseType = FeedProfileEditPhaseType.FEED,
                                isChecked = mutableStateOf(
                                    myFeedProfile.feedProfilePhases[index * 2 + 1].auxPump1
                                ),
                            ),
                            FeedProfileEditMenuItem(
                                id = index * 2 + 1,
                                level = 3,
                                text = "Aux. Pump 2",
                                toggleButtonType = FeedProfileEditToggleButtonType.AuxPump2,
                                isToggleButton = true,
                                isNumericInput = false,
                                phaseType = FeedProfileEditPhaseType.FEED,
                                isChecked = mutableStateOf(
                                    myFeedProfile.feedProfilePhases[index * 2 + 1].auxPump2
                                ),
                            ),
                        ),
                    ),
                ),
            )
        }
    }


    fun updatePhaseDuration(phaseIndex: Int, newDuration: Double) {
        Log.d("FeedProfileEditViewModel", "Updating phase($phaseIndex) duration to $newDuration")
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

    fun resetFeedProfile() {
        myFeedProfile.resetFeedProfile()
        this.phaseMenuItems = createFeedProfileEditMenuItems()
        resetFeedProfileEditMenuItems()
    }
}