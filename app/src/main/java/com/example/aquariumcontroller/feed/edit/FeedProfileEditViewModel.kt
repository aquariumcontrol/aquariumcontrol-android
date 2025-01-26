package com.example.aquariumcontroller.feed.edit

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.aquariumcontroller.feed.FeedProfile

class FeedProfileEditViewModel(myFeedProfile: FeedProfile) : ViewModel() {

    var phaseMenuItems: List<FeedProfileEditMenuItem> = List(10) { index ->
        FeedProfileEditMenuItem(
            id = index,
            level = 1,
            text = "Phase ${index + 1}",
            isToggleButton = false,
            children = mutableListOf(
                FeedProfileEditMenuItem(
                    id = index * 10,
                    level = 2,
                    text = "Pause",
                    isToggleButton = false,
                    children = mutableListOf(
                        FeedProfileEditMenuItem(
                            id = index * 100 + 1,
                            level = 3,
                            text = "Duration",
                            isToggleButton = false,
                            isExpanded = mutableStateOf(false) // Initially collapsed
                        ),
                        FeedProfileEditMenuItem(
                            id = index * 100 + 2,
                            level = 3,
                            text = "Skimmer",
                            isToggleButton = true,
                            isChecked = mutableStateOf(
                                myFeedProfile.feedProfilePhases[index*2].skimmer),
                            isExpanded = mutableStateOf(false) // Initially collapsed
                        ),
                        FeedProfileEditMenuItem(
                            id = index * 100 + 3,
                            level = 3,
                            text = "Main Pump 1",
                            isToggleButton = true,
                            isChecked = mutableStateOf(
                                myFeedProfile.feedProfilePhases[index*2].mainPump1),
                            isExpanded = mutableStateOf(false) // Initially collapsed
                        ),
                        FeedProfileEditMenuItem(
                            id = index * 100 + 4,
                            level = 3,
                            text = "Main Pump 2",
                            isToggleButton = true,
                            isChecked = mutableStateOf(
                                myFeedProfile.feedProfilePhases[index*2].mainPump2),
                            isExpanded = mutableStateOf(false) // Initially collapsed
                        ),
                        FeedProfileEditMenuItem(
                            id = index * 100 + 5,
                            level = 3,
                            text = "Aux. Pump 1",
                            isToggleButton = true,
                            isChecked = mutableStateOf(
                                myFeedProfile.feedProfilePhases[index*2].auxPump1),
                            isExpanded = mutableStateOf(false) // Initially collapsed
                        ),
                        FeedProfileEditMenuItem(
                            id = index * 100 + 6,
                            level = 3,
                            text = "Aux. Pump 2",
                            isToggleButton = true,
                            isChecked = mutableStateOf(
                                myFeedProfile.feedProfilePhases[index*2].auxPump2),
                            isExpanded = mutableStateOf(false) // Initially collapsed
                        ),
                    )
                ),
                FeedProfileEditMenuItem(
                    id = index * 10,
                    level = 2,
                    text = "Feed",
                    isToggleButton = false,
                    children = mutableListOf(
                        FeedProfileEditMenuItem(
                            id = index * 100 + 1,
                            level = 3,
                            text = "Duration",
                            isToggleButton = false,
                            isExpanded = mutableStateOf(false) // Initially collapsed
                        ),
                        FeedProfileEditMenuItem(
                            id = index * 100 + 2,
                            level = 3,
                            text = "Skimmer",
                            isToggleButton = true,
                            isChecked = mutableStateOf(
                                myFeedProfile.feedProfilePhases[index*2+1].skimmer),
                            isExpanded = mutableStateOf(false) // Initially collapsed
                        ),
                        FeedProfileEditMenuItem(
                            id = index * 100 + 3,
                            level = 3,
                            text = "Main Pump 1",
                            isToggleButton = true,
                            isChecked = mutableStateOf(
                                myFeedProfile.feedProfilePhases[index*2+1].mainPump1),
                            isExpanded = mutableStateOf(false) // Initially collapsed
                        ),
                        FeedProfileEditMenuItem(
                            id = index * 100 + 4,
                            level = 3,
                            text = "Main Pump 2",
                            isToggleButton = true,
                            isChecked = mutableStateOf(
                                myFeedProfile.feedProfilePhases[index*2+1].mainPump2),
                            isExpanded = mutableStateOf(false) // Initially collapsed
                        ),
                        FeedProfileEditMenuItem(
                            id = index * 100 + 5,
                            level = 3,
                            text = "Aux. Pump 1",
                            isToggleButton = true,
                            isChecked = mutableStateOf(
                                myFeedProfile.feedProfilePhases[index*2+1].auxPump1),
                            isExpanded = mutableStateOf(false) // Initially collapsed
                        ),
                        FeedProfileEditMenuItem(
                            id = index * 100 + 6,
                            level = 3,
                            text = "Aux. Pump 2",
                            isToggleButton = true,
                            isChecked = mutableStateOf(
                                myFeedProfile.feedProfilePhases[index*2+1].auxPump2),
                            isExpanded = mutableStateOf(false) // Initially collapsed
                        ),
                    )
                )
            ),
            isExpanded = mutableStateOf(false) // Initially collapsed
        )
    }

    fun updatePhaseDuration(phaseIndex: Int, newDuration: Double) {
        /*
        val profile = _feedProfile.value ?: return
        val updatedPhases = profile.feedProfilePhases.toMutableList()
        updatedPhases[phaseIndex] = updatedPhases[phaseIndex].copy(duration = newDuration)
//        _feedProfile.value = profile.copy(feedProfilePhases = updatedPhases)

         */
    }
}