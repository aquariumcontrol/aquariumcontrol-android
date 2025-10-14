package com.laimburggasse.aquariumcontrol.feed.edit

import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.laimburggasse.aquariumcontrol.feed.FeedProfile
import com.laimburggasse.aquariumcontrol.feed.mockFeedProfilesRepository
import com.laimburggasse.aquariumcontrol.ui.theme.AquariumControllerTheme

@Composable
fun FeedProfileEditMenuItemComposable(
    feedProfileEditMenuItem: FeedProfileEditMenuItem,
    feedProfileEditViewModel: FeedProfileEditViewModel
) {
    val isExpanded: Boolean = feedProfileEditViewModel.expandedItemIds.collectAsState().value.
            contains(feedProfileEditMenuItem.id)

    // This composable displays one item of the feed profile edit menu:
    // - text describing the actuator
    // - switch allowing to enable/disable the actuator in the phase of the feed profile
    Row(
        modifier = Modifier
            .clickable {
                if (feedProfileEditMenuItem.children.isNotEmpty()) {
                    feedProfileEditViewModel.onExpanded(itemId = feedProfileEditMenuItem.id)
                }
            }
            .padding(start = (feedProfileEditMenuItem.level * 16).dp, top = 8.dp, bottom = 8.dp),
    ) {
        if (feedProfileEditMenuItem.children.isNotEmpty()) {
            Text(text = feedProfileEditMenuItem.text, Modifier.padding(top = 8.dp))
            Spacer(Modifier.weight(weight = 1f))
        }
        if (feedProfileEditMenuItem.isToggleButton) {
            Text(text = feedProfileEditMenuItem.text, Modifier.padding(top = 8.dp))
            Spacer(Modifier.weight(weight = 1f))
            if (feedProfileEditMenuItem.children.isNotEmpty() && !isExpanded) {
                // Icon to indicate expandability
                Icon(imageVector = Icons.Default.MoreVert, contentDescription = "More options")
            }
            Switch(
                checked = feedProfileEditMenuItem.isChecked,
                onCheckedChange = {
                    feedProfileEditViewModel.onToggleStateChange(
                        phaseId = feedProfileEditMenuItem.id,
                        toggleButtonType = feedProfileEditMenuItem.toggleButtonType!!,
                        isChecked = it
                    )
                })
        }
        if (feedProfileEditMenuItem.isNumericInput) {
            FeedProfileEditMenuItemDurationComposable(
                feedProfileEditMenuItem,
                feedProfileEditViewModel
            )
        }
    }
    if (isExpanded) {
        FeedProfileEditMenuComposable(
            itemsToRender = feedProfileEditMenuItem.children,
            feedProfileEditViewModel = feedProfileEditViewModel
        )
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
internal fun FeedProfileEditMenuItemComposablePreview() {
    val mockFeedProfileEditMenuItem = FeedProfileEditMenuItem(
        id = 1,
        level = 1,
        text = "Preview",
        isToggleButton = true,
        children = emptyList(),
        phaseType = FeedProfileEditPhaseType.FEED,
        toggleButtonType = FeedProfileEditToggleButtonType.Skimmer,
        isNumericInput = false,
        isChecked = true,
        duration = 0.0,
    )

    val mockInitialExpandedItemIds: Set<Int> = emptySet()

    val mockFeedProfileEditViewModelFactory = FeedProfileEditViewModelFactory(
        feedProfile = mockFeedProfile,
        feedProfilesRepository = mockFeedProfilesRepository,
        initialExpandedItemIds = mockInitialExpandedItemIds
    )

    AquariumControllerTheme {
        Surface(
            color = MaterialTheme.colorScheme.background
        ) {
            FeedProfileEditMenuItemComposable(
                feedProfileEditMenuItem = mockFeedProfileEditMenuItem,
                feedProfileEditViewModel = mockFeedProfileEditViewModelFactory.create(
                    FeedProfileEditViewModel::class.java
                )
            )
        }
    }

    fun createFeedProfileEditMenuItems(currentFeedProfile: FeedProfile): List<FeedProfileEditMenuItem> {
        return List(10) { index ->
            FeedProfileEditMenuItem(
                id = index,
                level = 1,
                text = "Phase ${index + 1}",
                isToggleButton = false,
                isNumericInput = false,
                isChecked = false,
                duration = 0.0,
                children = mutableListOf(
                    FeedProfileEditMenuItem(
                        id = 0,
                        level = 2,
                        text = "Pause",
                        isToggleButton = false,
                        isNumericInput = false,
                        isChecked = false,
                        duration = 0.0,
                        phaseType = FeedProfileEditPhaseType.PAUSE,
                        children = mutableListOf(
                            FeedProfileEditMenuItem(
                                id = 0,
                                level = 3,
                                text = "Duration",
                                isToggleButton = false,
                                phaseType = FeedProfileEditPhaseType.PAUSE,
                                isNumericInput = true,
                                isChecked = false,
                                duration = currentFeedProfile.feedProfilePhases[index * 2].duration,
                            ),
                            FeedProfileEditMenuItem(
                                id = (index * 2) * 10 + 1,
                                level = 3,
                                text = "Skimmer",
                                isToggleButton = true,
                                phaseType = FeedProfileEditPhaseType.PAUSE,
                                toggleButtonType = FeedProfileEditToggleButtonType.Skimmer,
                                isNumericInput = false,
                                isChecked = currentFeedProfile.feedProfilePhases[index * 2].skimmer,
                                duration = 0.0,
                            ),
                            FeedProfileEditMenuItem(
                                id = (index * 2) * 10 + 2,
                                level = 3,
                                text = "Main Pump 1",
                                phaseType = FeedProfileEditPhaseType.PAUSE,
                                toggleButtonType = FeedProfileEditToggleButtonType.MainPump1,
                                isNumericInput = false,
                                isChecked = currentFeedProfile.feedProfilePhases[index * 2].mainPump1,
                                duration = 0.0,
                            ),
                            FeedProfileEditMenuItem(
                                id = (index * 2) * 10 + 3,
                                level = 3,
                                text = "Main Pump 2",
                                phaseType = FeedProfileEditPhaseType.PAUSE,
                                toggleButtonType = FeedProfileEditToggleButtonType.MainPump2,
                                isNumericInput = false,
                                isChecked = currentFeedProfile.feedProfilePhases[index * 2].mainPump2,
                                duration = 0.0,
                            ),
                            FeedProfileEditMenuItem(
                                id = (index * 2) * 10 + 4,
                                level = 3,
                                text = "Aux. Pump 1",
                                phaseType = FeedProfileEditPhaseType.PAUSE,
                                toggleButtonType = FeedProfileEditToggleButtonType.AuxPump1,
                                isNumericInput = false,
                                isChecked = currentFeedProfile.feedProfilePhases[index * 2].auxPump1,
                                duration = 0.0,
                            ),
                            FeedProfileEditMenuItem(
                                id = (index * 2) * 10 + 5,
                                level = 3,
                                text = "Aux. Pump 2",
                                phaseType = FeedProfileEditPhaseType.PAUSE,
                                toggleButtonType = FeedProfileEditToggleButtonType.AuxPump2,
                                isNumericInput = false,
                                isChecked = currentFeedProfile.feedProfilePhases[index * 2].auxPump2,
                                duration = 0.0,
                            ),
                        )
                    ),
                    FeedProfileEditMenuItem(
                        id = 1,
                        level = 2,
                        text = "Feed",
                        isToggleButton = false,
                        isNumericInput = false,
                        isChecked = false,
                        duration = 0.0,
                        phaseType = FeedProfileEditPhaseType.FEED,
                        children = mutableListOf(
                            FeedProfileEditMenuItem(
                                id = (index * 2 + 1) * 10 + 0,
                                level = 3,
                                text = "Duration",
                                isToggleButton = false,
                                phaseType = FeedProfileEditPhaseType.FEED,
                                isNumericInput = true,
                                isChecked = false,
                                duration = currentFeedProfile.feedProfilePhases[index * 2 + 1].duration,
                            ),
                            FeedProfileEditMenuItem(
                                id = (index * 2 + 1) * 10 + 1,
                                level = 3,
                                text = "Skimmer",
                                isToggleButton = true,
                                phaseType = FeedProfileEditPhaseType.FEED,
                                toggleButtonType = FeedProfileEditToggleButtonType.Skimmer,
                                isNumericInput = false,
                                isChecked = currentFeedProfile.feedProfilePhases[index * 2 + 1].skimmer,
                                duration = 0.0,
                            ),
                            FeedProfileEditMenuItem(
                                id = (index * 2 + 1) * 10 + 2,
                                level = 3,
                                text = "Main Pump 1",
                                phaseType = FeedProfileEditPhaseType.FEED,
                                toggleButtonType = FeedProfileEditToggleButtonType.MainPump1,
                                isNumericInput = false,
                                isChecked = currentFeedProfile.feedProfilePhases[index * 2 + 1].mainPump1,
                                duration = 0.0,
                            ),
                            FeedProfileEditMenuItem(
                                id = (index * 2 + 1) * 10 + 3,
                                level = 3,
                                text = "Main Pump 2",
                                phaseType = FeedProfileEditPhaseType.FEED,
                                toggleButtonType = FeedProfileEditToggleButtonType.MainPump2,
                                isNumericInput = false,
                                isChecked = currentFeedProfile.feedProfilePhases[index * 2 + 1].mainPump2,
                                duration = 0.0,
                            ),
                            FeedProfileEditMenuItem(
                                id = (index * 2 + 1) * 10 + 4,
                                level = 3,
                                text = "Aux. Pump 1",
                                phaseType = FeedProfileEditPhaseType.FEED,
                                toggleButtonType = FeedProfileEditToggleButtonType.AuxPump1,
                                isNumericInput = false,
                                isChecked = currentFeedProfile.feedProfilePhases[index * 2 + 1].auxPump1,
                                duration = 0.0,
                            ),
                            FeedProfileEditMenuItem(
                                id = (index * 2 + 1) * 10 + 5,
                                level = 3,
                                text = "Aux. Pump 2",
                                phaseType = FeedProfileEditPhaseType.FEED,
                                toggleButtonType = FeedProfileEditToggleButtonType.AuxPump2,
                                isNumericInput = false,
                                isChecked = currentFeedProfile.feedProfilePhases[index * 2 + 1].auxPump2,
                                duration = 0.0,
                            )
                        )
                    )
                )
            )
        }
    }
}