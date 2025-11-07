package com.laimburggasse.aquariumcontrol.feed.presentation.edit

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
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.laimburggasse.aquariumcontrol.feed.domain.model.FeedProfile
import com.laimburggasse.aquariumcontrol.ui.theme.AquariumControllerTheme

@Composable
fun FeedProfileEditMenuItemComposable(
    feedProfileEditMenuItem: FeedProfileEditMenuItem,
    isExpanded: Set<Int>,
    feedProfile: FeedProfile,
    onFeedProfileEditUiEvent: (FeedProfileEditUiEvent) -> Unit
) {
    // conversion of unique ID of menu item to feed profile phase ID
    val phaseId: Int = mapMenuItemIdToPhaseId(id = feedProfileEditMenuItem.id)
    val isThisItemExpanded: Boolean = feedProfileEditMenuItem.id in isExpanded

    val isChecked: Boolean = if (phaseId < feedProfile.feedProfilePhases.size) {
        when (feedProfileEditMenuItem.toggleButtonType) {
            FeedProfileEditToggleButtonType.Skimmer -> feedProfile.feedProfilePhases[phaseId].skimmer
            FeedProfileEditToggleButtonType.MainPump1 -> feedProfile.feedProfilePhases[phaseId].mainPump1
            FeedProfileEditToggleButtonType.MainPump2 -> feedProfile.feedProfilePhases[phaseId].mainPump2
            FeedProfileEditToggleButtonType.AuxPump1 -> feedProfile.feedProfilePhases[phaseId].auxPump1
            FeedProfileEditToggleButtonType.AuxPump2 -> feedProfile.feedProfilePhases[phaseId].auxPump2
            null -> false // Not a toggle button
        }
    } else {
        false // Default value if phaseId is out of bounds
    }

    // This composable displays one item of the feed profile edit menu:
    // - text describing the actuator
    // - switch allowing to enable/disable the actuator in the phase of the feed profile
    Row(
        modifier = Modifier
            .clickable {
                if (feedProfileEditMenuItem.children.isNotEmpty()) {
                    onFeedProfileEditUiEvent(FeedProfileEditUiEvent.ExpandChildren(
                        menuItemId = feedProfileEditMenuItem.id)
                    )
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
            if (feedProfileEditMenuItem.children.isNotEmpty() && !isThisItemExpanded) {
                // Icon to indicate expandability
                Icon(imageVector = Icons.Default.MoreVert, contentDescription = "More options")
            }
            Switch(
                checked = isChecked,
                onCheckedChange = {
                    onFeedProfileEditUiEvent(FeedProfileEditUiEvent.SwitchToggle(
                        menuItemId = feedProfileEditMenuItem.id,
                        toggleButtonType = feedProfileEditMenuItem.toggleButtonType!!,
                        isChecked = it
                    ))
                })
        }
        if (feedProfileEditMenuItem.isNumericInput) {
            FeedProfileEditMenuItemDurationComposable(
                feedProfile,
                feedProfileEditMenuItem,
                onFeedProfileEditUiEvent
            )
        }
    }
    if (isThisItemExpanded) {
        FeedProfileEditMenuComposable(
            itemsToRender = feedProfileEditMenuItem.children,
            feedProfile,
            isExpanded,
            onFeedProfileEditUiEvent
        )
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
internal fun FeedProfileEditMenuItemComposablePreview() {
    val mockFeedProfileEditMenuItem = FeedProfileEditMenuItem(
        id = 100,
        level = 3,
        text = "Preview",
        isToggleButton = true,
        children = emptyList(),
        phaseType = FeedProfileEditPhaseType.FEED,
        toggleButtonType = FeedProfileEditToggleButtonType.Skimmer,
        isNumericInput = false,
    )

    val mockInitialExpandedItemIds: Set<Int> = emptySet()

    AquariumControllerTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            FeedProfileEditMenuItemComposable(
                feedProfileEditMenuItem = mockFeedProfileEditMenuItem,
                feedProfile = mockFeedProfile,
                onFeedProfileEditUiEvent = {},
                isExpanded = mockInitialExpandedItemIds,
            )
        }
    }
}